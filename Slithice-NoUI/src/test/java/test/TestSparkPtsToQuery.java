package test;

import java.util.*;
import jqian.sootex.ptsto.IPtsToQuery;
import jqian.sootex.ptsto.SparkPtsToQuery;
import soot.*;

public class TestSparkPtsToQuery{

    public static String CASES[] = {
            "<test.cases.PtsTo: void testStringBuilder()>",
            "<test.cases.PtsTo: void testStringBuilderWrapper()>",
            "<test.cases.PtsTo: void testStringBuilderWrapperFieldAccess()>",
            "<test.cases.PtsTo: void testStringBuilderWrapperFieldAccess(test.cases.StringBuilderWrapper)>",
            "<test.cases.StringBuilderWrapper: test.cases.StringBuilderWrapper append(int)>"
    };
    /**
     * Usage: java CLASS <main_class> [analyzed_method]*
     * Example:
     *     test.util.cases.AccessPathCase "<test.cases.AccessPathCase: void test1()>"
     */
    public static void main(String[] args) {
    	Properties options = Test.loadConfig("/ptsConfig.xml");
    	Test.loadClasses(true);
    	Test.doFastSparkPointsToAnalysis();    	 
    	
    	SootMethod method = Scene.v().getMethod(TestSparkPtsToQuery.CASES[3]);
        System.out.println("Testing method: "+method);
        IPtsToQuery query = new SparkPtsToQuery(); 
        PtsToTester.testPtsToQuery(Test.out,method,query);
    }  
}
