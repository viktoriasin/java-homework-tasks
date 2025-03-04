package ru.sinvic.homework;

@SuppressWarnings("all")
public class Demo {
    public static void main(String[] args) {

        //        TestLoggingInterface myClass = IoC.createMyClass();
        TestLoggingInterface myClass = new TestLoggingInterfaceImpl();
        System.out.println("создали инстанс " + myClass);

        System.out.println("tstr: " + myClass);

        System.out.println(myClass.calculation(321));

        //        System.out.println("ща сделаем 1 вызов");
        //        myClass.calculation(123);
        //        System.out.println("сделали 1 вызов");
        //        System.out.println("ща сделаем 2 вызов");
        //        myClass.calculation(666);
        //        System.out.println("сделали 2 вызов");
        //        myClass.calculation(123, 456);
        //        myClass.calculation(123, 456, "789");
        //        myClass.process("Hello world");
    }
}
