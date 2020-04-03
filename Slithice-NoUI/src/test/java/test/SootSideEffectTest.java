package test;

import soot.Scene;
import soot.SideEffectTester;
import soot.SootMethod;
import soot.jimple.toolkits.pointer.PASideEffectTester;
import soot.jimple.toolkits.pointer.SideEffectTagger;
import soot.options.Options;

public class SootSideEffectTest implements AllTestCases {

    public static void main(String[] args) {
        Test.loadConfig("../config.xml");
        Test.loadClasses(true);

        //SootUtils.doSparkPointsToAnalysis(Collections.EMPTY_MAP);
        Test.doFastSparkPointsToAnalysis();

        String methodSignature = SideEffect.MY_CASES[1];
        SootMethod method = Scene.v().getMethod(methodSignature);
        Test.out.println("\nTesting method: " + method);
//        SideEffectTester sideEffectTester = new PASideEffectTester();
//        sideEffectTester.newMethod(method);
        SideEffectTagger.v().transform(method.getActiveBody());
        Test.out.println("\nFinish test method: " + method);
    }

}
