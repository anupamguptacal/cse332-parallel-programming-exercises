package hasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import cse332.exceptions.NotYetImplementedException;

public class HasOver {
	private static int CUTOFF;
	private static ForkJoinPool POOL = new ForkJoinPool();
    public static boolean hasOver(int val, int[] arr, int sequentialCutoff) {
    	CUTOFF = sequentialCutoff;
    	return POOL.invoke(new MaxTask(val, arr, 0, arr.length));
    }
    
    static class MaxTask extends RecursiveTask<Boolean> {
    	int[] arr;
    	int value;
    	int low;
    	int hi;
    	public MaxTask(int val, int[] arrayParse, int lo, int high) {
    		this.arr = arrayParse;
    		this.value = val;
    		this.low = lo;
    		this.hi = high;
    	}

		@Override
		protected Boolean compute() {
			if(hi - low <= CUTOFF) {
    			if(returnMax(arr, low, hi) > value) {
    				return true;
    			} else {
    				return false;
    			}
    		}
    		int mid = low + (hi - low)/2;
    		MaxTask left = new MaxTask(value, arr, low, mid);
    		MaxTask right = new MaxTask(value, arr, mid, hi);
    		
    		right.fork();
    		
    		boolean leftgetAnswer = left.compute();
    		boolean rightAnswer = right.join();
    		
    		return leftgetAnswer || rightAnswer;
		}
    	
    }
    private static int returnMax(int[]arr, int lo, int hi) {
    	int max = arr[lo];
    	for(int i = lo + 1; i < hi; i++) {
    		if(arr[i] > max) {
    			max = arr[i];
    		}
    	}
    	return max;
    }

    private static void usage() {
        System.err.println("USAGE: HasOver <number> <array> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        int val = 0;
        int[] arr = null;

        try {
            val = Integer.parseInt(args[0]); 
            String[] stringArr = args[1].replaceAll("\\s*",  "").split(",");
            arr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++) {
                arr[i] = Integer.parseInt(stringArr[i]);
            }
            System.out.println(hasOver(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
        
    }
}
