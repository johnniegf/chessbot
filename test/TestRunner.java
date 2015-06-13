package de.htwsaar.chessbot.test;

import java.util.*;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;

import org.junit.runner.JUnitCore;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


public class TestRunner {

    private Class[]   testClasses;
    private Map<String, StringBuffer> logs;
    private ResultListener resultListener;

    public static void main(String[] args) 
    {  
        if ( args.length != 1 ) {
            displayUsage();
            return;
        }
        try {
            Class<?>[] testList = parseTestList(args[0]);
            TestRunner runner   = new TestRunner(testList);
            runner.runAllTests();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    
    }
    
    public TestRunner(Class[] testList) {
        this.logs = new HashMap<String, StringBuffer>(); 
        
        this.testClasses = testList;
        this.resultListener = new ResultListener();
    }

    /**
    * Convenience method.
    */
    public void runAllTests() {
        this.resultListener = new ResultListener();
        Collection<Thread> workers = new ArrayList<Thread>();
        for (Class test : testClasses) {
            Thread t = new RunWorker(test);
            t.start();
            workers.add(t);
        }

        for (Thread w : workers) {
            try {
                w.join();
            } catch(InterruptedException ire) {
                System.out.println(ire);
            }
        }

        displaySummary();
    }
 
    private static Class<?>[] parseTestList(String filename)
            throws FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader( 
                                    new FileReader(filename) );
        Collection<Class<?>> classList = new ArrayList<Class<?>>();
        String line = "";
        while (line != null) {
            line = reader.readLine();
            try {
                classList.add(Class.forName(line.trim()));
            } catch (Exception e) {}
        }

        return classList.toArray(new Class<?>[0]);
    }
    
    private static void displayUsage() {
    
    }

    
    private void displaySummary() {
        int fails = this.resultListener.failCount;
        
        if (fails > 0) {
            for (String testName : logs.keySet()) {
                System.out.println(logs.get(testName).toString());
            }
            System.out.print("[FAIL] ");
        } else {
            System.out.print("[PASS] ");
        }
        displayOverallResult();
    }

    private void displayOverallResult() {
        StringBuilder b = new StringBuilder();
        int fails = this.resultListener.failCount;
        int count = this.resultListener.runCount;
        int ign   = this.resultListener.ignCount;
        b.append("Overall result: ");
        b.append( String.format("%d classes run, ", testClasses.length) );
        b.append( String.format("%d/%d atomic tests passed",count - fails, count) );
        if (ign > 0) 
            b.append( String.format(", %d ignored", ign) );
                
        b.append("\n");
        System.out.println(b.toString());
    }

// =============================================================
    
    private static final String  DEFAULT_TEST_LIST = "TestList.txt";
    private static final char[]  SEPARATORS        = { ' ', '\t', '\n', '-', ':', ';' };

// =============================================================
    
    private class RunWorker extends Thread {
    
        private Class<?>  testClass;
        private JUnitCore runner;

        public RunWorker(Class<?> testClass) {
            this.testClass = testClass;
            this.runner = new JUnitCore();
            this.runner.addListener(resultListener);

            StringBuffer log = new StringBuffer();
            synchronized(logs) {
                logs.put(testClass.getName(), log);
            }
            this.runner.addListener( new LogListener(testClass.getName(), log) );
        }

        public void run() {
            this.runner.run(testClass);
        }
    }

//===============================================================

    private class ResultListener extends RunListener {
        public int failCount;
        public int runCount;
        public int ignCount;

        public ResultListener() {
            failCount = runCount = ignCount = 0;
        }
        
        public synchronized void testStarted(Description d) {
            runCount++;
        }

        public synchronized void testFailure(Failure f) {
            failCount++;
        }

        public synchronized void testIgnored(Description d) {
            ignCount++;
        }
    }

//================================================================

    private class LogListener extends RunListener {
        private String testName;
        private StringBuffer log;
        private transient StringBuilder buf;

        public LogListener(String testName, StringBuffer log) {
            this.testName = testName;
            if (log == null)
                this.log = new StringBuffer();
            else
                this.log = log;
        }

        public void testAssumptionFailure(Failure f) {
        }

        public void testFailure(Failure f) {
            Description d = f.getDescription();
            Throwable   e = f.getException();
            buf = new StringBuilder();
            
            prettyPrintInfo(buf, PREFIX_FAIL, d.getDisplayName());
            prettyPrintException(buf, e);

            log.append(buf);
        }

        public void testFinished(Description d) {
        }

        public void testIgnored(Description d) {
            buf = new StringBuilder();

            prettyPrintInfo(buf,PREFIX_IGNORE, d.getDisplayName());
            log.append(buf);
        }

        public void testRunFinished(Result r) {
            buf = new StringBuilder();
            prettyPrintStats(buf, r);
            log.append(buf);
        }   

        public void testRunStarted(Description d) {
            buf = new StringBuilder();
            prettyPrintInfo(buf, PREFIX_TEST, testName);
            log.append(buf);

        }

        public void testStarted(Description d) {
            buf = new StringBuilder();
            prettyPrintInfo(buf, PREFIX_TEST_RUN, d.getDisplayName());
            log.append(buf);
        }

        private void prettyPrintStats(StringBuilder b, Result r) {
            if (r == null || b == null) return;
            int fails = r.getFailureCount();
            int count = r.getRunCount();
            int ign   = r.getIgnoreCount();
            b.append(PREFIX_RESULT).append(": ");
            b.append( String.format("%d/%d tests passed",count - fails, count) );
            if (ign > 0) 
                b.append( String.format(", %d ignored", ign) );
            b.append("\n");
        }
        private void prettyPrintInfo(StringBuilder b, String prefix, String message) {
            if (b == null || prefix == null) return;
            b.append(prefix).append(": ").append(message).append(NEWLINE);
        }

        private void prettyPrintException(StringBuilder b, Throwable e) {
            if (e == null || b == null) return;
            b.append("-- <").append(e.getClass().getName()).append(">").append(NEWLINE);
            b.append("-- \"").append(e.getMessage()).append("\"").append(NEWLINE);
            b.append("--+ stack trace").append(NEWLINE);
            for (StackTraceElement ste : e.getStackTrace()) {
                b.append("  +-> ").append(ste.toString()).append(NEWLINE);
            }

        }

        private static final String NEWLINE = "\n";

        private static final String PREFIX_FAIL     = "[FAIL]";
        private static final String PREFIX_IGNORE   = "[IGN] ";
        private static final String PREFIX_TEST     = "[TEST]";
        private static final String PREFIX_TEST_RUN = "[RUN] ";
        private static final String PREFIX_RESULT   = "[RES] ";
    }

} 
