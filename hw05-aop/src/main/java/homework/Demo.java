package homework;

public class Demo {
    public static void main(String[] args) {
        TestLoggingInterface myClass = Ioc.createMyClass();
        myClass.calculation(123);
        myClass.calculation(123, 456);
        myClass.calculation(123, 456, "789");
        myClass.process("Hello world");
    }
}
