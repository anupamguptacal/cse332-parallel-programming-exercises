package tests.gitlab.getLeftMostIndex;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import cse332.exceptions.NotYetImplementedException;
import getLeftMostIndex.GetLeftMostIndex;
import tests.exceptions.InformativeException;
import tests.TestsUtility;

public class GetLeftMostIndexTests extends TestsUtility {
    public static Random RANDOM = new Random(332134);

    public static final int FULLY_SEQUENTIAL = Integer.MAX_VALUE;
    public static final int REASONABLE_CUTOFF = 1000;
    public static final int FULLY_PARALLEL = 1;

    public static final int NUM_SMALL_HAYSTACKS = 250;
    public static final int NUM_SMALL_NEEDLE_SIZES = 5;
    public static final int SMALL_HAYSTACK_SIZE  = 10;

    public static final int NUM_LARGE_HAYSTACKS = 10;
    public static final int LARGE_NEEDLE_SIZE = 10;
    public static final int LARGE_HAYSTACK_SIZE  = 100000;

    public static final int HUGE_NEEDLE_SIZE = 50;
    public static final int HUGE_HAYSTACK_SIZE   = 80000000;

    public static void main(String[] args) {
        new GetLeftMostIndexTests().run();
    }

    protected void run() {
        END_WITH_EXIT = true;
        SHOW_TESTS = true;

        ALLOWED_TIME = 25000;

        test("checkSmallSequential");
        test("checkSmallParallel");
        test("checkLarge");
        test("checkParallelism");
        finish();
    }

    public static int checkSmallSequential() { 
        boolean good = true;
        for (int i = 0; i < NUM_SMALL_HAYSTACKS; i++) {
            for (int j = 1; j <= NUM_SMALL_NEEDLE_SIZES; j++) {
                good &= test(makeInput(i, SMALL_HAYSTACK_SIZE), FULLY_SEQUENTIAL, j);
            }
        }
        return good ? 1 : 0;
    }

    public static int checkSmallParallel() { 
        boolean good = true;
        for (int i = 0; i < NUM_SMALL_HAYSTACKS; i++) {
            for (int j = 1; j <= NUM_SMALL_NEEDLE_SIZES; j++) {
                good &= test(makeInput(i, SMALL_HAYSTACK_SIZE), FULLY_PARALLEL, j);
            }
        }
        return good ? 1 : 0;
    }


    public static int checkLarge() {
        boolean good = true;
        for (int i = 0; i < NUM_LARGE_HAYSTACKS; i++) {
            good &= test(makeRandomInput(LARGE_HAYSTACK_SIZE), REASONABLE_CUTOFF, LARGE_NEEDLE_SIZE);
        }
        return good ? 1 : 0;
    }

    public static int checkParallelism() {
        String haystack = makeRandomInput(HUGE_HAYSTACK_SIZE);
        String needle = makeRandomInput(HUGE_NEEDLE_SIZE);
        int answer = sweep(needle, haystack);

        long seqTime, reasonableTime, paraTime = 0; 
        long start = System.currentTimeMillis(); 

        boolean fullySequential = runTest(needle, haystack, FULLY_SEQUENTIAL, answer) == 1;
        seqTime = System.currentTimeMillis() - start;

        boolean reasonableCutoff = runTest(needle, haystack, REASONABLE_CUTOFF, answer) == 1;
        reasonableTime = System.currentTimeMillis() - (seqTime + start);

        boolean fullyParallel = runTest(needle, haystack, FULLY_PARALLEL, answer) == 1;
        paraTime = System.currentTimeMillis() - (seqTime + reasonableTime + start);

        if (!fullySequential || !reasonableCutoff || !fullyParallel) {
            return 0;
        }

        return (paraTime > seqTime && seqTime > reasonableTime) ? 1 : 0;
    } 

    private static int sweep(String needle, String haystack) {
        int currStart = 0;
        int needleIndex = 0;
        while (currStart < haystack.length()) {
            while (needleIndex <= needle.length()) {
                if (needleIndex == needle.length()) {
                    return currStart;
                }
                else if (currStart + needleIndex >= haystack.length()) {
                    return -1;
                }
                else if (needle.charAt(needleIndex) == haystack.charAt(currStart + needleIndex)) {
                    needleIndex++;
                }
                else {
                    break;
                }
            }
            currStart++;
            needleIndex = 0;
        }
        return -1;
    }

    private static String makeInput(int num, int size) {
        StringBuilder arr = new StringBuilder();
        for (int i = size - 1; i >= 0; i--) {
            arr.append("" + (char)('0' + (char)((num >> i) & 1)));
        }
        return arr.toString();
    }

    private static String makeRandomInput(int size) {
        StringBuilder arr = new StringBuilder();
        for (int i = size - 1; i >= 0; i--) {
            arr.append("" + (char)('0' + (RANDOM.nextBoolean() ? 1 : 0)));
        }
        return arr.toString();
    }


    private static boolean test(String haystack, int cutoff, int needleSize) {
        boolean correct = true;

        for (int i = 0; i < (1 << needleSize); i++) { 
            String needle = makeInput(i, needleSize);
            correct &= runTest(needle, haystack, cutoff, sweep(needle, haystack)) == 1;
        }
        return correct;
    }
        
    private static int runTest(String needle, String haystack, int cutoff, int expected) {
        return GetLeftMostIndex.getLeftMostIndex(needle.toCharArray(), haystack.toCharArray(), cutoff) == expected ? 1 :0;
    }
}
