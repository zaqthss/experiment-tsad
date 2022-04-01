package cn.edu.bit.cs.anomaly.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class SAX {
	public int wordsize;
	public double comrate;
	public int alphasize;
	public double[] beta;
	public int aOffset =(int)('a');
	public PAA paa;
	public String saxword;
	public double[][] cuts = {
			{0},
			{-0.430727,0.430727},
			{-0.67449,0,0.67449},
			{-0.841621,-0.253347,0.253347,0.841621},
			{-0.967422,-0.430727,0,0.430727,0.967422},
			{-1.06757,-0.565949,-0.180012,0.180012,0.565949,1.06757},
			{-1.15035,-0.67449,-0.318639,0,0.318639,0.67449,1.15035},
			{-1.22064,-0.76471,-0.430727,-0.13971,0.13971,0.430727,0.76471,1.22064},
			{-1.28155,-0.841621,-0.524401,-0.253347,0,0.253347,0.524401,0.841621,1.28155},
			{-1.33518,-0.908458,-0.604585,-0.348756,-0.114185,0.114185,0.348756,0.604585,
			 0.908458,1.33518},
			{-1.38299,-0.967422,-0.67449,-0.430727,-0.210428,-1.39146e-16,0.210428,0.430727,
			 0.67449,0.967422,1.38299},
			{-1.42608,-1.02008,-0.736316,-0.502402,-0.293381,-0.0965586,0.0965586,0.293381,
			 0.502402,0.736316,1.02008,1.42608},
			{-1.46523,-1.06757,-0.791639,-0.565949,-0.366106,-0.180012,-2.78292e-16,0.180012,
			 0.366106,0.565949,0.791639,1.06757,1.46523},
			{-1.50109,-1.11077,-0.841621,-0.622926,-0.430727,-0.253347,-0.0836517,0.0836517,
			 0.253347,0.430727,0.622926,0.841621,1.11077,1.50109},
			{-1.53412,-1.15035,-0.887147,-0.67449,-0.488776,-0.318639,-0.157311,0,0.157311,
			 0.318639,0.488776,0.67449,0.887147,1.15035,1.53412},
			 {-1.5647264713618, -1.18683143275582, -0.928899491647271,
			      -0.721522283982343, -0.541395085129088, -0.377391943828554, -0.223007830940367,
			      -0.0737912738082727, 0.0737912738082727, 0.223007830940367, 0.377391943828554,
			      0.541395085129088, 0.721522283982343, 0.928899491647271, 1.18683143275582, 1.5647264713618 },
			 { -1.59321881802305, -1.22064034884735, -0.967421566101701,
			          -0.764709673786387, -0.589455797849779, -0.430727299295457, -0.282216147062508,
			          -0.139710298881862, 0, 0.139710298881862, 0.282216147062508, 0.430727299295457,
			          0.589455797849779, 0.764709673786387, 0.967421566101701, 1.22064034884735, 1.59321881802305 },
			 { -1.61985625863827, -1.25211952026522, -1.00314796766253,
			              -0.8045963803603, -0.633640000779701, -0.47950565333095, -0.336038140371823,
			              -0.199201324789267, -0.0660118123758407, 0.0660118123758406, 0.199201324789267,
			              0.336038140371823, 0.47950565333095, 0.633640000779701, 0.8045963803603, 1.00314796766253,
			              1.25211952026522, 1.61985625863827 },
			 { -1.64485362695147, -1.2815515655446, -1.03643338949379,
			                  -0.841621233572914, -0.674489750196082, -0.524400512708041, -0.385320466407568,
			                  -0.2533471031358, -0.125661346855074, 0, 0.125661346855074, 0.2533471031358,
			                  0.385320466407568, 0.524400512708041, 0.674489750196082, 0.841621233572914, 1.03643338949379,
			                  1.2815515655446, 1.64485362695147 }
		};
	public SAX(double[] t,double comrate,int alphasize) {
		super();
		this.comrate = comrate;
		this.alphasize = alphasize;	
		this.beta = cuts[this.alphasize-2];
		
		paa=to_PAA(t);
		saxword=alphabetize(paa.approximation);
	}
	

	public static void main(String[] args) {
		double[] t= {26.9,26.8,27.4,26.7,64.5, 65.1,62.1, 64.4, 62.2, 62.7, 27.1, 25.2,25.4};
		t=zscore(t,0);
		SAX sax=new SAX(t,0.5,4);
			
		int[] inn= {4, 5, 6,8,9};
		String innstr=sax.toSax(inn);
		System.out.println(countFrequency(sax.saxword,innstr));
		int[] inn2= {1, 2, 3, 10, 11, 12};
		String innstr2=sax.toSax(inn2);
		System.out.println(countFrequency(sax.saxword,innstr2));
		int[] inn3= {8,9};
		String innstr3=sax.toSax(inn3);
		
		System.out.println(countFrequency(sax.saxword,innstr3));
		System.out.println();
	}
	
	public static Long countFrequency(String a,String b) {
		int m=a.length();
		int n=b.length();
		long MOD=2000120420010122L;
		if(n==0) {
			return 0L;
		}
		long[][] dp=new long[(m+1)][n+1];
		for (int i = 0; i <= n; i++)
		    dp[0][i] = 0;
		 
		  for (int i = 0; i <= m; i++)
		    dp[i][0] = 1;
		 
		  //fill the dp table
		  for (int i = 1; i <= m; i++) {
		    for (int j = 1; j <= n; j++) {
		      if (a.charAt(i-1)==b.charAt(j-1))
		        dp[i][j] = (dp[i - 1][j] + dp[i - 1][j - 1])%MOD;
		      else
		        dp[i][j] = dp[i - 1][j]%MOD;
		    }
		  }
		long t=dp[m][n];
        return dp[m][n];
		/*int count=0;
		while (a.indexOf(b) != -1) {
			count++;
			a= a.substring(a.indexOf(b)+b.length());
		}
		return count;*/
	}
	public String toSax(int[] inn) {
		char[] res=new char[inn.length];
		String re="";
		int index=-1;
		int oldindex=index;
		for(int i=0;i<inn.length;i++) {		
			for(int j=0;j<paa.indices.size();j++) {
				int flag=0;
				int[] temp=paa.indices.get(j);
				for(int t=0;t<temp.length;t++) {
						if(temp[t]==inn[i]) {
							index=j;
							flag=1;
							break;
						}
				}
				if(flag==1) {
					break;
				}
			}
			res[i]=saxword.charAt(index);
			if(oldindex!=index) {
				re+=saxword.charAt(index);
				oldindex=index;
			}
		}
		return re;
	}


	public PAA to_PAA(double[] x) {
		int n=x.length;
		wordsize=(int) (n*comrate);
		double stepfloat=(double)n/wordsize;
		int step=(int) Math.ceil(stepfloat);
		int framestart=0;
		int i=0;
		double[] approximation=new double[wordsize];
		HashMap<Integer,int[]> indices=new HashMap<Integer,int[]>();
		while(framestart<=n-step){
				double[] thisframe=Arrays.copyOfRange(x, framestart, framestart+step);
				approximation[i]=calcMean(thisframe);
				int[] in=new int[step];
				for(int j=framestart;j<framestart+step;j++) {
					in[j-framestart]=j;
				}
				indices.put(i, in);
				i++;
				framestart=(int)(i*stepfloat);
		}
		PAA paa=new PAA(approximation,indices);
		return paa;
	}
	public String alphabetize(double[] paa) {
		String alphabetizedX="";
		for(int i=0;i<paa.length;i++) {
			boolean letterfound=false;
			for(int j=0;j<beta.length;j++) {
				if(paa[i]<beta[j]) {
					//alphabetizedX[i]=(char) (aOffset+j);
					alphabetizedX+=(char)(aOffset+j);
					letterfound=true;
					break;
				}
			}
			if(!letterfound) {
				//alphabetizedX[i]=(char) (aOffset+beta.length);
				alphabetizedX+=(char) (aOffset+beta.length);
			}
		}
		return alphabetizedX;
	}
	public static double[] zscore(double[] t,int ddof) {
		double mean=calcMean(t);
		double std=calcStd(t,ddof);
		for (int i = 0; i <t.length; i++) {
	         t[i]=(t[i]-mean)/std;
	        }
		return t;	
	}
	public static double calcMean(double[] t) {
		  double sum=0;
		  for (int i = 0; i < t.length; i++) {
	          sum += t[i];
	      }
		  double mean = sum/t.length;
		  return mean;
		  
	  }
	  public static double calcStd(double[] t,int ddof) {
		  double total=0;
		  double mean=calcMean(t);
		  for (int i = 0; i<t.length; i++) {
	          total +=
	              (t[i]-mean)
	                  * (t[i]-mean);
	        }
		  int dev=t.length;
		  if(ddof==1) {
			  dev=dev-1;
		  }
		  double std = Math.sqrt(total/dev);
 		  return std;
		  
	  }
}
class PAA{
	public double[] approximation;
	public HashMap<Integer,int[]> indices;
	public PAA(double[] approximation, HashMap<Integer,int[]> indices) {
		super();
		this.approximation = approximation;
		this.indices = indices;
	}
	
}
