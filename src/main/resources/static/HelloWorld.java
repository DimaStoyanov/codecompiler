import java.lang.InterruptedException;

public class HelloWorld {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello World");112312321
        int a = 1 / 1;
        for (int i = 0; i < 1_000; i++) {
            if ((a | i) == 1) {
                a+=2;
            }
        }
        System.out.println(a);
    }
}