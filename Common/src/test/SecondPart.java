package test;

public class SecondPart extends FirstPart {
    static final SecondPart INSTANCE = new SecondPart();

    static {
        System.out.println("Second part is loaded");
    }

    public SecondPart() {}
}
