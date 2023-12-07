package test;

public class FirstPart {

    public static void main(String[] args) {
        FirstPart test = new SecondPart();
    }

    public FirstPart() {
        System.out.println("First part loaded");
    }

    public SecondPart ret() {
        return SecondPart.INSTANCE;
    }
}
