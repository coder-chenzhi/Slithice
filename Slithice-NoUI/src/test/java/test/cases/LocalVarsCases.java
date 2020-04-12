package test.cases;

public class LocalVarsCases {

    public static String PREFIX = "TEST";

    public static void main(String[] args) {
        LocalVarsCases cases = new LocalVarsCases();

        testStringBuilder();
        testCascadeStringBuilder();
        testStringFormat();
        testUtilCaller1();
        testUtilCaller2();
        testStaticAccess("test");
        testArrayAccess("test");

        cases.exchangeName();
    }

    static void testStringBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.append("Test");
    }

    static void testCascadeStringBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.append("This").append("a").append("Test");
    }

    static void testStringFormat() {
        String s = String.format("%s", "test");
        s = s + "test";
    }

    static void testUtilCaller1() {
        util1(new StringBuilder());
    }

    static void util1(StringBuilder sb) {
        sb.append("Test");
    }

    static void testUtilCaller2() {
        String s = util2();
    }

    static String util2() {
        StringBuilder sb = new StringBuilder();
        sb.append("Test");
        return sb.toString();
    }

    static String testStaticAccess(String s) {
        return s + PREFIX;
    }

    static String[] testArrayAccess(String s) {
        String[] strs = new String[10];
        strs[0] = s;
        return strs;
    }

    void exchangeName() {
        Person a = new Person("Daniel", true, 18);
        Person b = new Person("Lily", false, 18);
        String name = a.name;
        a.name = b.name;
        b.name = name;
    }

    class Person {
        String name;
        Boolean isMale;
        Integer age;

        public Person(String name, Boolean isMale, Integer age) {
            this.name = name;
            this.isMale = isMale;
            this.age = age;
        }
    }

}
