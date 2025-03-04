package ru.sinvic.homework;

@SuppressWarnings("all")
public class Demo {
    public static void main(String[] args) {

        //        TestLoggingInterface myClass = IoC.createMyClass();
        TestLoggingInterface myClass = new TestLoggingInterfaceImpl();
        myClass.calculation(321);
        myClass.calculation(123, 666);
        myClass.calculation(123, 666, "Hello");
        myClass.process("Hello");
    }
}
