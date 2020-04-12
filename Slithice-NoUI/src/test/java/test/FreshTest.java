package test;

import jqian.sootex.sideeffect.FreshAnalysis;
import soot.Scene;

public class FreshTest implements AllTestCases {

    public static void main(String[] args) {
        Test.loadConfig("../localVarsConfig.xml");
        Test.loadClasses(true);

        //SootUtils.doSparkPointsToAnalysis(Collections.EMPTY_MAP);
        Test.doFastSparkPointsToAnalysis(false, true);

        FreshAnalysis freshAnalysis = new FreshAnalysis(Scene.v().getCallGraph());
        freshAnalysis.build();
        System.out.println("Pause");
    }

}
