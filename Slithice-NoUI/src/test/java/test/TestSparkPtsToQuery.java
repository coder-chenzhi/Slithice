package test;

import java.util.*;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableList;
import jqian.sootex.ptsto.IPtsToQuery;
import jqian.sootex.ptsto.SparkPtsToQuery;
import soot.*;
import soot.jimple.spark.geom.dataMgr.Obj_full_extractor;
import soot.jimple.spark.geom.dataMgr.PtSensVisitor;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.GeomQueries;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.BriefBlockGraph;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class TestSparkPtsToQuery{

    public static String CASES[] = {
            "<test.cases.PtsTo: void testStringBuilder()>",
            "<test.cases.PtsTo: void testStringBuilderWrapper()>",
            "<test.cases.PtsTo: void testStringBuilderWrapperFieldAccess()>",
            "<test.cases.PtsTo: void testStringBuilderWrapperFieldAccess(test.cases.StringBuilderWrapper)>",
            "<test.cases.StringBuilderWrapper: test.cases.StringBuilderWrapper append(int)>",
            "<test.cases.PtsTo: void test_merge()>",
            "<test.cases.PtsTo: void test_switch()>",
            "<test.cases.PtsTo: void test_branches()>",
            "<test.cases.PtsTo: void test_early_stop(boolean)>",
            "<test.cases.PtsTo: void test_simple_branch(boolean)>"
    };

    public static void testPeom() {
        Properties options = Test.loadConfig("../ptsConfig.xml");
        Test.loadClasses(true);
        Test.doFastSparkPointsToAnalysis(true, false);

//        SootMethod call0 = Scene.v().getSootClass("test.cases.PtsTo").getMethodByName("main");
//        SootMethod call1 = Scene.v().getMethod("<test.cases.StringBuilderWrapper: test.cases.StringBuilderWrapper append(int)>");
//        SootMethod call2 = Scene.v().getMethod("<java.lang.StringBuilder: java.lang.StringBuilder append(int)>");

        SootMethod caller = Scene.v().getSootClass("test.cases.PtsTo").getMethodByName("main");
        SootMethod callee = Scene.v().getMethod("<test.cases.PtsTo: void testStringBuilderWrapperFieldAccess(test.cases.StringBuilderWrapper)>");

        // Obtain the points-to analyzer
        PointsToAnalysis pa = Scene.v().getPointsToAnalysis();
        if (!(pa instanceof GeomPointsTo)) return;
        GeomPointsTo geomPts = (GeomPointsTo) pa;
        System.err.println("Query testing starts.");



        // Querying
        GeomQueries queries = new GeomQueries(geomPts);
        PtSensVisitor objFull = new Obj_full_extractor();

        // Obtain call graph and enclosing method of querying pointer
        CallGraph cg = Scene.v().getCallGraph();
        Local l = (Local) callee.getActiveBody().getLocals().toArray()[2]; // 0: this; 1: sbw; 2: $stack2
//        Local l = method.getActiveBody().getThisLocal();
        VarNode vn = geomPts.findLocalVarNode(l);

        // Try CFA
        List<Edge> it;
        Edge[] callChain = new Edge[1];


        it = ImmutableList.copyOf(cg.edgesOutOf(caller));
//        callChain[0] = it.get(1); // first call of testStringBuilderWrapperFieldAccess
        callChain[0] = it.get(2); // second call of testStringBuilderWrapperFieldAccess

//        it = ImmutableList.copyOf(cg.edgesOutOf(call1));
//        callChain[1] = it.get(0);

//        it = ImmutableList.copyOf(cg.edgesOutOf(call2));
//        callChain[2] = it.get(0);

        if ( queries.contextsGoBy(callChain[0], l, objFull) ) {
            System.out.println();
            System.out.println(vn.toString() + ":");
            objFull.debugPrint();
        }

    }

    public static void testSpark() {
        Properties options = Test.loadConfig("../ptsConfig.xml");
        Test.loadClasses(true);
        Test.doFastSparkPointsToAnalysis(false, false);

        SootMethod method = Scene.v().getMethod(TestSparkPtsToQuery.CASES[9]);
        BlockGraph blcokGraph = new BriefBlockGraph(method.getActiveBody());
        UnitGraph unitGraph = new BriefUnitGraph(method.getActiveBody());
        System.out.println("Testing method: "+method);
        IPtsToQuery query = new SparkPtsToQuery();
        PtsToTester.testPtsToQuery(Test.out,method,query);
    }

    public static void main(String[] args) {
//        testPeom();
        testSpark();

    }  
}
