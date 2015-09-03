//package de.htwsaar.chessbot.test;

import java.util.Vector;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;

import org.junit.runner.JUnitCore;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class TestRunner {

    private static final int     SCREEN_WIDTH      = 78;
    private static final String  DEFAULT_TEST_LIST = "TestList.txt";
    private static final char    INDENT            = '>';
    private static final char    LINE              = '=';
    private static final char[]  SEPARATORS        = { ' ', '\t', '\n', '-', ':', ';' };

    private Class[]   testClasses;

    private static    StringBuffer tmpSB;
    private transient JUnitCore    testRunner;

    private transient int runCount;
    private transient int ignCount;
    private transient int failCount;

    public static void main(String[] args) 
    {  
        if ( args.length != 1 ) {
            printUsage();
            return;
        }
        try {
            Class[]    testList = parseTestList(args[0]);
            TestRunner runner   = new TestRunner(testList);
            runner.runAllTests();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    
    }
    
    public TestRunner(Class[] testList) {
        this.testClasses = testList;
        this.testRunner  = new JUnitCore();
}

    /**
    * Convenience method.
    */
    public void runAllTests() {
        runCount = 0; ignCount = 0; failCount = 0;
        Result result;

        for (Class test : testClasses) {
            result     = runTest(test);
            runCount  += result.getRunCount();
            ignCount  += result.getIgnoreCount();
            failCount += result.getFailureCount();
        }
        displaySummary();
    }
 
    private Result runTest(Class<?> testClass) {
        Result result = testRunner.run(testClass);
        displayResult(testClass, result);
        return result;

    }

    private static void displayTestHeader(Class<?> testClass) {
        printBar();
        printf("=== Tests für Klasse " + testClass.getName());
        printBar();
    }

    private void displayResult(Class<?> testClass, Result r) {
        if ( r.getRunCount() > 0 && r.getFailureCount() > 0 ) {
            displayTestHeader(testClass);
            if (!r.wasSuccessful()) {
                printBar('-');
                for (Failure f : r.getFailures())
                    displayFailure(f);
            } else {
                printf("=== +++ Alle (" + r.getRunCount() + ") Testfälle bestanden +++");
            }
            printBar();
        } else
            printf("==> Test erfolgreich: <" + testClass.getName() + ">");
    }

    private static void displayFailure(Failure f) { 
        Description d = f.getDescription();
        Throwable   e = f.getException();
        printf("Test nicht bestanden: " + d.getDisplayName());
        printException(e);
        printBar('-');

    }

    private void displaySummary() {
        println();
        printBar();
        printf("=====> Zusammenfassung <=====");
        printf("+-> ++ " + runCount  + " Tests durchgeführt ++  -- " +
                ignCount + " Tests ignoriert       --");
        printf("+-> ++ " + (runCount - failCount)
                         +             " Tests bestanden    ++  -- " +
               failCount + " Tests nicht bestanden --");
        printBar();
        println();

    }

    private static void printUsage() {
    }

    private static void printException(Throwable e) {
        if (e == null) return;
        printf("- Ausnahme!  <" + e.getClass().getName() + ">");
        printf("- Nachricht: "  + e.getMessage());
        printf("----- stack trace -----");
        int i = 10;
        for (StackTraceElement ste : e.getStackTrace()) {
            if (--i < 0 ) break;
            printf("- " + ste.toString());
        }
    }

    private static void printBar() {
        printBar(LINE);
    }

    private static void printBar(char line) {
        tmpSB = new StringBuffer();
        tmpSB.append(INDENT).append(" ");
        for (int i = 0; i < SCREEN_WIDTH; ++i)
            tmpSB.append(line);
        System.out.println(tmpSB.toString());
        
    }

    private static void println() {
        printf(" ");
    }

    private static void printf(String inputText) {
        if ( inputText == null ) { 
            printf(" ");
            return;
        }

        tmpSB = new StringBuffer(inputText);
        int len = tmpSB.length();
        for (int i = 0; i < len; i += SCREEN_WIDTH) {
            if ( i + SCREEN_WIDTH > len )
                System.out.println(INDENT + " " + tmpSB.substring(i));
            else
                System.out.println(INDENT + " " + tmpSB.substring(i, i + SCREEN_WIDTH));
        }

    }

    private static Class[] parseTestList(String filename)
            throws FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader( 
                                    new FileReader(filename) );
        Vector<Class> classList = new Vector<Class>();
        String line = "";
        while (line != null) {
            line = reader.readLine();
            try {
                classList.add(Class.forName(line));
            } catch (Exception e) {}
        }

        return classList.toArray(new Class[0]);
    }

} 
