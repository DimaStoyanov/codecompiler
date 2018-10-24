import java.io.BufferedReader;
        import java.io.FileReader;
        import java.io.PrintWriter;
        import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            try (PrintWriter writer = new PrintWriter("output.txt")) {
                writer.write(br.readLine());
            }
        }
    }
}