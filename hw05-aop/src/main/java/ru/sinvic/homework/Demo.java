package ru.sinvic.homework;

@SuppressWarnings("all")
public class Demo {
    public static void main(String[] args) {

        //        TestLoggingInterface myClass =
        //                (TestLoggingInterface) IoC.createMyClass(new TestLoggingInterfaceImpl(),
        // TestLoggingInterface.class);
        //        TestInterface myClass2 = (TestInterface) IoC.createMyClass(new TestInterfaceImpl(),
        // TestInterface.class);
        //
        //        myClass2.doWork(1);
        //        myClass.calculation(321);
        //        myClass.calculation(123, 666);
        //        myClass.calculation(123, 666, "Hello");
        //        myClass.process("Hello");

        TestLoggingInterface myClassAgentAop = new TestLoggingInterfaceImpl();
        myClassAgentAop.calculation(321);
        myClassAgentAop.calculation(123, 666);
        myClassAgentAop.calculation(123, 666, "Hello");
        myClassAgentAop.process("Hello");
    }
}
