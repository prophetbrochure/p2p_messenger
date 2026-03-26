import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * <p>Основная точка входа в программу. (А чё, кому-то непонятно???)</p>
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        // Титры, тоси, боси
        IO.printHelloMessage();
        IO.printMenu();
    }
}
