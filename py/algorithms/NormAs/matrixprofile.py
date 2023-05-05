import math

import numpy as np


def is_similarity_join(ts_a, ts_b):
    """
    Helper function to determine if a similarity join is occuring or not.

    Parameters
    ----------
    ts_a : array_like
        Time series A.
    ts_b : array_like, None
        Time series B.

    Returns
    -------
    True or false respectively.
    """
    return is_array_like(ts_a) and is_array_like(ts_b)

def is_array_like(a):
    """
    Helper function to determine if a value is array like.

    Parameters
    ----------
    a : obj
        Object to test.

    Returns
    -------
    True or false respectively.
    """
    return isinstance(a, (list, tuple, np.ndarray))

def to_np_array(a):
    """
    Helper function to convert tuple or list to np.ndarray.

    Parameters
    ----------
    a : Tuple, list or np.ndarray
        The object to transform.

    Returns
    -------
    The np.ndarray.

    Raises
    ------
    ValueError
        If a is not a valid type.
    """
    if isinstance(a, np.ndarray):
        return a

    if not is_array_like(a):
        raise ValueError('Unable to convert to np.ndarray!')

    return np.array(a)


def get_profile_length(ts_a, ts_b, m):
    """
    Determines the profile length based on the provided inputs.

    Parameters
    ----------
    ts_a : array_like
        Time series containing the queries for which to calculate the Matrix Profile.
    ts_b : array_line
        Time series containing the queries for which to calculate the Matrix Profile.
    m : int
        Length of subsequence to compare.

    Returns
    -------
    int - the length of the matrix profile.
    """
    return len(ts_a) - m + 1


def find_skip_locations(ts, profile_length, window_size):
    """
    Determines which locations should be skipped based on nan or inf values.

    Parameters
    ----------
    ts : array_like
        Time series containing the queries for which to calculate the Matrix Profile.
    query : array_line
        Time series containing the queries for which to calculate the Matrix Profile.
    window_size : int
        Length of subsequence to compare.

    Returns
    -------
    int - the length of the matrix profile.
    """
    skip_loc = np.zeros(profile_length).astype(bool)

    for i in range(profile_length):
        segment = ts[i:i + window_size]
        search = (np.isinf(segment) | np.isnan(segment))

        if np.any(search):
            skip_loc[i] = True

    return skip_loc


def clean_nan_inf(ts):
    """
    Replaces nan and inf values with zeros per matrix profile algorithms.

    Parameters
    ----------
    ts: array_like
        Time series to clean.

    Returns
    -------
    np.ndarray - The cleaned time series.

    Raises
    ------
    ValueError
        When the ts is not array like.
    """
    ts = to_np_array(ts)
    search = (np.isinf(ts) | np.isnan(ts))
    ts[search] = 0

    return ts

def moving_avg_std(a, window=3):
    """
    Computes the moving avg and std. over an array given a window size.

    Parameters
    ----------
    a : array_like
        The array to compute the moving std. on.
    window : int
        The window size.

    Returns
    -------
    The moving avg and std. over the array as a tuple.
    (avg, std)
    """
    a = a.astype("float")
    s = np.insert(np.cumsum(a), 0, 0)
    sSq = np.insert(np.cumsum(a ** 2), 0, 0)
    segSum = s[window:] - s[:-window]
    segSumSq = sSq[window:] - sSq[:-window]
    mu=segSum/window
    sig=segSumSq/window-mu**2
    sig=np.sqrt((segSumSq/window)-mu**2)
    return mu,sig

def fft_convolve(ts, query):
    """
    Computes the sliding dot product for query over the time series using
    the quicker FFT convolution approach.

    Parameters
    ----------
    ts : array_like
        The time series.
    query : array_like
        The query.

    Returns
    -------
    array_like - The sliding dot product.
    """
    n = len(ts)
    m = len(query)
    x = np.fft.fft(ts)
    y = np.append(np.flipud(query), np.zeros([1, n - m]))
    y = np.fft.fft(y)
    z = np.fft.ifft(x * y)

    return np.real(z[m - 1:n])

def apply_exclusion_zone(exclusion_zone, is_join, window_size, data_length,
    index, distance_profile):
    if exclusion_zone > 0 and not is_join:
        ez_start = np.max([0, index - exclusion_zone])
        ez_end = np.min([data_length - window_size + 1, index + exclusion_zone + 1])
        distance_profile[ez_start:ez_end] = np.inf

    return distance_profile

def generate_batch_jobs(profile_length, n_jobs):
    """
    Generates start and end positions for a matrix profile length and number
    of jobs.

    Parameters
    ----------
    profile_length : int
        The length of the matrix profile to compute.
    n_jobs : int
        The number of jobs (cpu cores).


    Returns
    -------
    Yielded start and end index for each job.
    """
    batch_size = int(math.ceil(profile_length / n_jobs))

    if batch_size == profile_length:
        yield (0, profile_length)
    else:
        for i in range(n_jobs):
            start = i * batch_size
            end = (i + 1) * batch_size

            if end > profile_length:
                end = profile_length

            yield (start, end)

            if end == profile_length:
                break

def distanceprofile(prod, ws, data_mu, data_sig, query_mu, query_sig):
    """
    Computes the distance profile for the given statistics.

    Parameters
    ----------
    prod : array_like
        The sliding dot product between the time series and query.
    ws : int
        The window size.
    data_mu : array_like
        The time series moving average.
    data_sig : array_like
        The time series moving standard deviation.
    query_mu : array_like
        The querys moving average.
    query_sig : array_like
        The querys moving standard deviation.


    Returns
    -------
    array_like - The distance profile.
    """
    distance_profile = (
        2 * (ws - (prod - ws * data_mu * query_mu) / (data_sig * query_sig))
    )

    with np.errstate(divide='ignore', invalid='ignore'):
        distance_profile = np.sqrt(np.real(distance_profile))

    return distance_profile


def _batch_compute(args):
    """
    Internal function to compute a batch of the time series in parallel.
    Parameters
    ----------
    args : tuple
        Various attributes used for computing the batch.
        (
            batch_start : int
                The starting index for this batch.
            batch_end : int
                The ending index for this batch.
            ts : array_like
                The time series to compute the matrix profile for.
            query : array_like
                The query.
            window_size : int
                The size of the window to compute the profile over.
            data_length : int
                The number of elements in the time series.
            profile_length : int
                The number of elements that will be in the final matrix
                profile.
            exclusion_zone : int
                Used to exclude trivial matches.
            is_join : bool
                Flag to indicate if an AB join or self join is occuring.
            data_mu : array_like
                The moving average over the time series for the given window
                size.
            data_sig : array_like
                The moving standard deviation over the time series for the
                given window size.
            first_product : array_like
                The first sliding dot product for the time series over index
                0 to window_size.
            skip_locs : array_like
                Indices that should be skipped for distance profile calculation
                due to a nan or inf.
        )
    Returns
    -------
    dict : profile
        The matrix profile, left and right matrix profiles and their respective
        profile indices.
    """
    batch_start, batch_end, ts, query, window_size, data_length, \
    profile_length, exclusion_zone, is_join, data_mu, data_sig, \
    first_product, skip_locs = args

    # initialize matrices
    matrix_profile = np.full(profile_length, np.inf)
    profile_index = np.full(profile_length, 0)

    left_matrix_profile = None
    right_matrix_profile = None
    left_profile_index = None
    right_profile_index = None

    if not is_join:
        left_matrix_profile = np.copy(matrix_profile)
        right_matrix_profile = np.copy(matrix_profile)
        left_profile_index = np.copy(profile_index)
        right_profile_index = np.copy(profile_index)

    # with batch 0 we do not need to recompute the dot product
    # however with other batch windows, we need the previous iterations sliding
    # dot product
    last_product = None
    if batch_start == 0:
        first_window = query[batch_start:batch_start + window_size]
        last_product = np.copy(first_product)
    else:
        first_window = query[batch_start - 1:batch_start + window_size - 1]
        last_product = fft_convolve(ts, first_window)

    query_sum = np.sum(first_window)
    query_2sum = np.sum(first_window ** 2)
    query_mu, query_sig = moving_avg_std(first_window, window_size)

    drop_value = first_window[0]

    # only compute the distance profile for index 0 and update
    if batch_start == 0:
        distance_profile = distanceprofile(
            last_product, window_size,data_mu, data_sig, query_mu, query_sig)

        # apply exclusion zone
        distance_profile = apply_exclusion_zone(exclusion_zone, is_join,
                                                     window_size, data_length, 0, distance_profile)

        # update the matrix profile
        indices = (distance_profile < matrix_profile)
        matrix_profile[indices] = distance_profile[indices]
        profile_index[indices] = 0

        # update the left matrix profile
        if not is_join:
            left_matrix_profile[indices] = distance_profile[indices]
            left_profile_index[np.argwhere(indices)] = 0

        batch_start += 1

    # make sure to compute inclusively from batch start to batch end
    # otherwise there are gaps in the profile
    if batch_end < profile_length:
        batch_end += 1

    # iteratively compute distance profile and update with element-wise mins
    for i in range(batch_start, batch_end):

        # check for nan or inf and skip
        if skip_locs[i]:
            continue

        query_window = query[i:i + window_size]
        query_sum = query_sum - drop_value + query_window[-1]
        query_2sum = query_2sum - drop_value ** 2 + query_window[-1] ** 2
        query_mu = query_sum / window_size
        query_sig2 = query_2sum / window_size - query_mu ** 2
        query_sig = np.sqrt(query_sig2)
        last_product[1:] = last_product[0:data_length - window_size] \
                           - ts[0:data_length - window_size] * drop_value \
                           + ts[window_size:] * query_window[-1]
        last_product[0] = first_product[i]
        drop_value = query_window[0]

        distance_profile = distanceprofile(
            last_product, window_size, data_mu, data_sig, query_mu, query_sig)

        # apply the exclusion zone
        distance_profile = apply_exclusion_zone(exclusion_zone, is_join,
                                                     window_size, data_length, i, distance_profile)

        # update the matrix profile
        indices = (distance_profile < matrix_profile)
        matrix_profile[indices] = distance_profile[indices]
        profile_index[indices] = i

        # update the left and right matrix profiles
        if not is_join:
            # find differences, shift left and update
            indices = distance_profile[i:] < left_matrix_profile[i:]
            falses = np.zeros(i).astype('bool')
            indices = np.append(falses, indices)
            left_matrix_profile[indices] = distance_profile[indices]
            left_profile_index[np.argwhere(indices)] = i

            # find differences, shift right and update
            indices = distance_profile[0:i] < right_matrix_profile[0:i]
            falses = np.zeros(profile_length - i).astype('bool')
            indices = np.append(indices, falses)
            right_matrix_profile[indices] = distance_profile[indices]
            right_profile_index[np.argwhere(indices)] = i

    return {
        'mp': matrix_profile,
        'pi': profile_index,
        'rmp': right_matrix_profile,
        'rpi': right_profile_index,
        'lmp': left_matrix_profile,
        'lpi': left_profile_index,
    }


def stomp(ts, window_size, query=None, n_jobs=1):
    """
    Computes matrix profiles for a single dimensional time series using the
    parallelized STOMP algorithm (by default). Ray or Python's multiprocessing
    library may be used. When you have initialized Ray on your machine,
    it takes priority over using Python's multiprocessing.
    Parameters
    ----------
    ts : array_like
        The time series to compute the matrix profile for.
    window_size: int
        The size of the window to compute the matrix profile over.
    query : array_like
        Optionally, a query can be provided to perform a similarity join.
    n_jobs : int, Default = 1
        Number of cpu cores to use.
    Returns
    -------
    dict : profile
        A MatrixProfile data structure.
    Raises
    ------
    ValueError
        If window_size < 4.
        If window_size > query length / 2.
        If ts is not a list or np.array.
        If query is not a list or np.array.
        If ts or query is not one dimensional.
    """
    is_join = is_similarity_join(ts, query)
    if not is_join:
        query = ts

    # data conversion to np.array
    ts = to_np_array(ts)
    query = to_np_array(query)

    if window_size < 4:
        error = "window size must be at least 4."
        raise ValueError(error)

    if window_size > len(query) / 2:
        error = "Time series is too short relative to desired window size"
        raise ValueError(error)

    # multiprocessing or single threaded approach
    if n_jobs == 1:
        pass

    # precompute some common values - profile length, query length etc.
    profile_length = get_profile_length(ts, query, window_size)
    data_length = len(ts)
    query_length = len(query)
    num_queries = query_length - window_size + 1
    exclusion_zone = int(np.ceil(window_size / 2.0))

    # do not use exclusion zone for join
    if is_join:
        exclusion_zone = 0

    # find skip locations, clean up nan and inf in the ts and query
    skip_locs = find_skip_locations(ts, profile_length, window_size)
    ts = clean_nan_inf(ts)
    query = clean_nan_inf(query)

    # initialize matrices
    matrix_profile = np.full(profile_length, np.inf)
    profile_index = np.full(profile_length, 0)

    # compute left and right matrix profile when similarity join does not happen
    left_matrix_profile = None
    right_matrix_profile = None
    left_profile_index = None
    right_profile_index = None

    if not is_join:
        left_matrix_profile = np.copy(matrix_profile)
        right_matrix_profile = np.copy(matrix_profile)
        left_profile_index = np.copy(profile_index)
        right_profile_index = np.copy(profile_index)

    # precompute some statistics on ts
    data_mu, data_sig = moving_avg_std(ts, window_size)
    first_window = query[0:window_size]
    first_product = fft_convolve(ts, first_window)

    batch_windows = []
    results = []

    # batch compute with multiprocessing
    args = []
    for start, end in generate_batch_jobs(num_queries, n_jobs):
        args.append((
            start, end, ts, query, window_size, data_length,
            profile_length, exclusion_zone, is_join, data_mu, data_sig,
            first_product, skip_locs
        ))
        batch_windows.append((start, end))

    # we are running single threaded stomp - no need to initialize any
    # parallel environments.
    if n_jobs == 1 or len(args) == 1:
        results.append(_batch_compute(args[0]))

    # now we combine the batch results
    if len(results) == 1:
        result = results[0]
        matrix_profile = result['mp']
        profile_index = result['pi']
        left_matrix_profile = result['lmp']
        left_profile_index = result['lpi']
        right_matrix_profile = result['rmp']
        right_profile_index = result['rpi']
    else:
        for index, result in enumerate(results):
            start = batch_windows[index][0]
            end = batch_windows[index][1]

            # update the matrix profile
            indices = result['mp'] < matrix_profile
            matrix_profile[indices] = result['mp'][indices]
            profile_index[indices] = result['pi'][indices]

            # update the left and right matrix profiles
            if not is_join:
                indices = result['lmp'] < left_matrix_profile
                left_matrix_profile[indices] = result['lmp'][indices]
                left_profile_index[indices] = result['lpi'][indices]

                indices = result['rmp'] < right_matrix_profile
                right_matrix_profile[indices] = result['rmp'][indices]
                right_profile_index[indices] = result['rpi'][indices]

    return matrix_profile
    '''return {
        'mp': matrix_profile,
        'pi': profile_index,
        'rmp': right_matrix_profile,
        'rpi': right_profile_index,
        'lmp': left_matrix_profile,
        'lpi': left_profile_index,
        'metric': 'euclidean',
        'w': window_size,
        'ez': exclusion_zone,
        'join': is_join,
        'sample_pct': 1,
        'data': {
            'ts': ts,
            'query': query
        },
        'class': "MatrixProfile",
        'algorithm': "stomp"
    }'''