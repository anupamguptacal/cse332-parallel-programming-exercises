package getLeftMostIndex;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import cse332.exceptions.NotYetImplementedException;

public class GetLeftMostIndex {
	private static ForkJoinPool POOL  = new ForkJoinPool();
	private static int CUTOFF;
    public static int getLeftMostIndex(char[] needle, char[] haystack, int sequentialCutoff) {
        CUTOFF = sequentialCutoff;
        return POOL.invoke(new LeftMostIndex(needle, haystack, 0, haystack.length));
    }
    static class LeftMostIndex extends RecursiveTask<Integer> {
    	int lo;
    	int high;
    	char[] needle;
    	char[] haystack;
    	public LeftMostIndex(char[] ne, char[] ha, int low, int hi) {
    		this.needle = ne;
    		this.haystack = ha;
    		this.lo = low;
    		this.high = hi;
    	}
		@Override
		protected Integer compute() {
			if(high - lo <= CUTOFF) {
				for(int i = lo; i < high; i ++) {
					if(i + needle.length <= haystack.length) {
						boolean needleFound = true;
						for(int j = 0; j < needle.length; j++) {
							if(haystack[i + j] != needle[j]) {
								needleFound = false;
								break;
							}
						}
						if(needleFound) {
							return i;
						}
					}
				}
				return -1;
			}			
			
			int mid = lo + (high - lo)/2;
			LeftMostIndex left = new LeftMostIndex(needle, haystack, lo, mid);
			LeftMostIndex right = new LeftMostIndex(needle, haystack, mid, high);
			right.fork();
			
			int returned = left.compute();
			int value = right.join();
			
			if(returned == -1) {
				return value;
			} else {
				return returned;
			}
		}
		
    	
    }
    private static void usage() {
        System.err.println("USAGE: GetLeftMostIndex <needle> <haystack> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        char[] needle = args[0].toCharArray();
        char[] haystack = args[1].toCharArray();
        try {
            System.out.println(getLeftMostIndex(needle, haystack, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}
