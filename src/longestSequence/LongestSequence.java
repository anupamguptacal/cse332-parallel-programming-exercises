package longestSequence;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import cse332.exceptions.NotYetImplementedException;


public class LongestSequence {
	private static int CUTOFF;
	private static ForkJoinPool POOL = new ForkJoinPool();
    public static int getLongestSequence(int val, int[] arr, int sequentialCutoff) {
      CUTOFF = sequentialCutoff;
     SequenceRange result =  POOL.invoke(new LargestSeq(val, arr, 0, arr.length));
     System.out.println("Answer = " + result.sequenceLength);
     return result.longestRange;
    }

    static class LargestSeq extends RecursiveTask<SequenceRange> {
    	int arr[];
    	int val;
    	int low;
    	int high;
		@Override
		protected SequenceRange compute() {
			if(high - low <= CUTOFF) {				
				SequenceRange max = returnMaxSequence(val, arr, low, high);
				int maximumValue = Math.max(max.longestRange, Math.max(max.matchingOnLeft, max.matchingOnRight));			
				return new SequenceRange(max.matchingOnLeft, max.matchingOnRight, maximumValue,Math.min(high, arr.length) - low);			
			}
			int mid = low + (high - low)/2;
			
			LargestSeq left = new LargestSeq(val, arr, low, mid);
			LargestSeq right = new LargestSeq(val, arr, mid, high);
			
			right.fork();
			
			SequenceRange leftValue = left.compute();
			SequenceRange rightValue = right.join();
			
			// finding maximum
			int leftValueTreaty = leftValue.matchingOnLeft;
			int rightValueTreaty = rightValue.matchingOnRight;
			if(leftValue.matchingOnLeft == leftValue.sequenceLength) {
				leftValueTreaty = leftValue.sequenceLength + rightValue.matchingOnLeft;
			}
			if(rightValue.matchingOnRight == rightValue.sequenceLength) {
				 rightValueTreaty = rightValue.sequenceLength + leftValue.matchingOnRight;
			}
			int biggestValue = Math.max(leftValue.longestRange, Math.max(leftValueTreaty, rightValueTreaty));
			biggestValue = Math.max(biggestValue,  Math.max(rightValue.longestRange, leftValue.matchingOnRight + rightValue.matchingOnLeft));
			return new SequenceRange(leftValueTreaty, rightValueTreaty, biggestValue, rightValue.sequenceLength + leftValue.sequenceLength);
		}
    	public LargestSeq(int value, int[] array, int low, int high) {
    		this.arr = array;
    		this.val = value;
    		this.low = low;
    		this.high = high;

    	}
    }
    public static SequenceRange returnMaxSequence(int val, int[] arr, int lo, int hi) {
    	int count = 0;
    	int maxCount = 0;
    	for(int i = lo; i < Math.min(hi, arr.length); i++) {
    		if(arr[i] == val) {
    			count++;
    		} else {
    			if(count > maxCount) {
    				maxCount = count;
    			}
    			count = 0;
    		}
    	}
    	
    	int left = 0;
    	for(int i = lo; i < Math.min(hi, arr.length); i ++) {
    		if(arr[i] != val) {
    			break;
    		} else {
    			left++;
    		}
    	}
    	
    	int right = 0;
    	for(int i = Math.min(hi - 1, arr.length); i >= lo; i--) {
    		if(arr[i] != val) {
    			break;
    		} else {
    			right++;
    		}
    	}
    	return new SequenceRange(left, right, Math.max(maxCount, count), Math.min(hi, arr.length) - lo);
    	
    	}
    private static void usage() {
        System.err.println("USAGE: LongestSequence <number> <array> <sequential cutoff>");
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
            System.out.println(getLongestSequence(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
        	System.err.println(e.getMessage());
            usage();
        }
    }
}