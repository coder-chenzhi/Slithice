package test;

import jqian.sootex.sideeffect.FastEscapeAnalysis;
import soot.Scene;
import soot.jimple.toolkits.callgraph.CallGraph;

public class LocalVarsTest implements AllTestCases {

    public static void main(String[] args) {
        Test.loadConfig("../localVarsConfig.xml");
        Test.loadClasses(true);

        //SootUtils.doSparkPointsToAnalysis(Collections.EMPTY_MAP);
        Test.doFastSparkPointsToAnalysis(false, true);

        CallGraph cg = Scene.v().getCallGraph();
        FastEscapeAnalysis escape = new FastEscapeAnalysis(cg);
        escape.build();
        System.out.println("Pause");
    }

}
