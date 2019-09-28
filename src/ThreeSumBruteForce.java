import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;

public class ThreeSumBruteForce {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE =  2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 70;
    static int MAXINPUTSIZE  = (int) Math.pow(2,10);
    static int MININPUTSIZE  =  1;

    static String ResultsFolderPath = "/home/caitlin/Documents/Lab2/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {
        verifyThreeSum();

        //direct the verification test results to file
        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        System.out.println("Running first full experiment...");
        runFullExperiment("ThreeSumBF-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment...");
        runFullExperiment("ThreeSumBF-Exp2.txt");
        System.out.println("Running third full experiment...");
        runFullExperiment("ThreeSumBF-Exp3.txt");

    }

    public static long[] createRandomListOfIntegers(int size){ //creating the random list of integers using the max and min values for the specified size
        long[] newList = new long[size];
        for(int j=0;j<size;j++){
            newList[j] = (long)(MINVALUE + Math.random() * (MAXVALUE - MINVALUE));
        }
        return newList;
    }

  public static void verifyThreeSum()
    {
        //print a small random list and apply the threesum method to values I know will satisfy the algorithm
        System.out.println("Starting Verify");
        System.out.println("Random List 1");
        long[] list1 = new long[]{-1, 0 ,1,4, -1, -3};
        System.out.println(Arrays.toString(list1));
        int list1Count = threeSum(list1);
        System.out.println("Three Sum 1");
        System.out.println(list1Count);


        System.out.println("Random List 2");
        long[] list2 = new long[]{-1,-1,2, 4, -1, -3};
        System.out.println(Arrays.toString(list2));
        int list2Count = threeSum(list2);
        System.out.println("Three Sum 2");
        System.out.println(list2Count);

    }

    static void runFullExperiment(String resultsFileName){
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#InputSize    AverageTime"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        /* for each size of input we want to test: in this case starting small and doubling the size each time */
        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*=2) {
            // progress message...
            System.out.println("Running test for input size "+inputSize+" ... ");

            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;
            // generate a list of randomly spaced integers in ascending sorted order to use as test input
            // In this case we're generating one list to use for the entire set of trials (of a given input size)
            // but we will randomly generate the search key for each trial
            System.out.print("    Generating test data...");
            long[] testList = createRandomListOfIntegers(inputSize);
            System.out.println("...done.");
            System.out.print("    Running trial batch...");

            /* force garbage collection before each batch of trials run so it is not included in the time */
            System.gc();

            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves
            //BatchStopwatch.start(); // comment this line if timing trials individually

            // run the trials
            for (long trial = 0; trial < numberOfTrials; trial++) {
                // generate a random key to search in the range of a the min/max numbers in the list
                long testSearchKey = (long) (0 + Math.random() * (testList[testList.length-1]));
                /* force garbage collection before each trial run so it is not included in the time */
                System.gc();

                TrialStopwatch.start(); // *** uncomment this line if timing trials individually
                /* run the function we're testing on the trial input */
                int threeSumTotal = threeSum(testList);
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
            }

            //batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f \n",inputSize, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    public static int threeSum(long[] a){
        int n = a.length; //n is the length of the list of array
        int count = 0;

        for (int i = 0; i < n; i++) //for the length of a...
        {
            for (int j = i+1; j < n; j++){  //for j less than the length of a
                for (int k = j + 1; k < n; k ++){ //for k less than the length of a
                    if (a[i] + a[j] + a[k] == 0){ //if the three values sum to be 0 increase count
                        count ++;
                    }
                }
            }
        }
        return count; //return the total 3 sum triples that were found
    }

}
