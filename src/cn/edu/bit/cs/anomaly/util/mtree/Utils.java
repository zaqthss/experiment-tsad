package cn.edu.bit.cs.anomaly.util.mtree;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;

/**
 * Some utilities.
 */
public final class Utils {
    public static long peakUsedMemory = 0;

    private static final int MegaBytes = 1024 * 1024;

    /**
     * Don't let anyone instantiate this class.
     */
    private Utils() {
    }


    /**
     * Identifies the minimum and maximum elements from an iterable, according
     * to the natural ordering of the elements.
     *
     * @param items An {@link Iterable} object with the elements
     * @param <T>   The type of the elements.
     * @return A pair with the minimum and maximum elements.
     */
    public static <T extends Comparable<T>> Pair<T> minMax(Iterable<T> items) {
        Iterator<T> iterator = items.iterator();
        if (!iterator.hasNext()) {
            return null;
        }

        T min = iterator.next();
        T max = min;

        while (iterator.hasNext()) {
            T item = iterator.next();
            if (item.compareTo(min) < 0) {
                min = item;
            }
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }

        return new Pair<T>(min, max);
    }


    /**
     * Randomly chooses elements from the collection.
     *
     * @param collection The collection.
     * @param n          The number of elements to choose.
     * @param <T>        The type of the elements.
     * @return A list with the chosen elements.
     */
    public static <T> List<T> randomSample(Collection<T> collection, int n) {
        List<T> list = new ArrayList<T>(collection);
        List<T> sample = new ArrayList<T>(n);
        Random random = new Random();
        while (n > 0 && !list.isEmpty()) {
            int index = random.nextInt(list.size());
            sample.add(list.get(index));
            int indexLast = list.size() - 1;
            T last = list.remove(indexLast);
            if (index < indexLast) {
                list.set(index, last);
            }
            n--;
        }
        return sample;
    }


    public static Integer[] removeFirstElement(Integer[] x) {

        Integer[] r = new Integer[x.length - 1];
        for (int i = 1; i < x.length; i++) {
            r[i - 1] = x[i];
        }
        return r;
    }

    public static void computeUsedMemory() {

        long freeMemory = Runtime.getRuntime().freeMemory() / MegaBytes;
        long totalMemory = Runtime.getRuntime().totalMemory() / MegaBytes;
        long usedMemory = (totalMemory - freeMemory);


        if (peakUsedMemory < usedMemory) {
            peakUsedMemory = usedMemory;

        }
//        System.out.println("Peak memory: " + peakUsedMemory);

    }


    public static long getCPUTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
    }

}
