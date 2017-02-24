package filterEmpty;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import cse332.exceptions.NotYetImplementedException;


public class FilterEmpty {
    static ForkJoinPool POOL = new ForkJoinPool();
    private static int CUTOFF = 1;

    public static int[] filterEmpty(String[] arr) {
        int[] bitset = mapToBitSet(arr);
        //System.out.println(java.util.Arrays.toString(bitset));
        int[] bitsum = ParallelPrefixSum.parallelPrefixSum(bitset);
        //System.out.println(java.util.Arrays.toString(bitsum));
        int[] result = mapToOutput(arr, bitsum, bitset);
        return result;
    }

    public static int[] mapToBitSet(String[] arr) {
    	int[] result = new int[arr.length];
    	POOL.invoke(new MapTask(arr, result, 0, arr.length));
    	return result;
    }
    
    @SuppressWarnings("serial")
	static class MapTask extends RecursiveAction {
    	//int[] array;
    	int lo;
    	int hi;
    	String[] value;
    	int[] result;
    	
    	public MapTask(String[] arr, int[] result, int low, int high) {
    		lo = low;
    		hi = high;
    		//array = new int[arr.length];
    		value = arr;   		
    		this.result = result;
    	}
    	
		@Override
		protected void compute() {
			if(hi - lo <= CUTOFF) {
				for (int i = lo; i < hi; i++) {
					result[i] = value[i].length() > 0 ? 1 : 0;
				}
				return ;
			}
			
			int mid = lo + (hi - lo)/2;
			
			MapTask left = new MapTask(value, result, lo, mid);
			MapTask right = new MapTask(value, result, mid, hi);
			right.fork();
			left.compute();
			right.join();		
		}  	
    }


    
    public static int[] mapToOutput(String[] input, int[] bitsum, int[] bitset) {
    	if (bitsum.length == 0) {
    		return new int[0];
    	}
    	int length = bitsum[bitsum.length - 1];
    	int[] returned = new int[length];
    	POOL.invoke(new OutputTask(input, returned, bitsum, bitset, 0, input.length));
    	return returned;
    }
    
    public static class OutputTask extends RecursiveAction {
		int[] bitset;
    	int[] bitsum;
    	String[] original;
    	int[] output;
    	int lo;
    	int high;
    	
    	public OutputTask(String[] input, int[] output, int[] bitsum, int[] bitset, int lo, int hi ) {
    		this.original = input;
    		this.bitsum = bitsum;
    		this.bitset = bitset;
    		this.output = output;
    		this.lo = lo;
    		this.high = hi;
    	}
		@Override
		protected void compute() {
			if(high - lo <= CUTOFF) {
				for(int i = lo; i < high; i ++) {
					if(bitset[i] == 1) {
						output[bitsum[i] - 1] = original[i].length();
					}
				}
				return ;
			}
			
			int mid = lo + (high - lo)/2;
			OutputTask left = new OutputTask(original, output, bitsum, bitset, lo, mid);
			OutputTask right = new OutputTask(original, output, bitsum, bitset, mid, high);
			left.fork();
			right.compute();
			left.join();
			
		}
    	
    }

    private static void usage() {
        System.err.println("USAGE: FilterEmpty <String array>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String[] arr = args[0].replaceAll("\\s*", "").split(",");
        System.out.println(Arrays.toString(filterEmpty(arr)));
    }
}