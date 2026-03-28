import java.util.Scanner;

/**
 * Класс для работы с вводом/выводом.
 */
public class IO {
    private static final int DEFAULT_PORT = 5000;

    // INPUT
    public static String requestUsername(Scanner scanner) {
        System.out.println("Твоё имя?");
        return scanner.nextLine();
    }

    public static String requestIP(Scanner scanner) {
        System.out.println("Введи ip пользователя");
        return scanner.nextLine();
    }

    public static int requestPort(Scanner scanner) {
        System.out.println("Введите порт.\n(" + DEFAULT_PORT + " по умолчанию, нажми Enter)");
        while (true) {
            String input = scanner.nextLine();
            try {
                return getPort(input);
            } catch (NumberFormatException e) {
                System.err.println("Ошибка. Порт должен состоять только из цыфр");
            }
        }
    }

    public static int getPort(String input) {
        if (input.isEmpty()) {
            return DEFAULT_PORT;
        }
        int port = Integer.parseInt(input);
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Порт должен быть от 1 до 65535 !");
        } else {
            return port;
        }
    }

    // OUTPUT
    public static void printHelloMessage() {
        System.out.println("Добро пожаловать в P2P messenger");
        // System.out.println("Над проектом работали:\n\tДмитриев Андрей\n\tКорзухин Михаил\n\tСтройлов Виталий\n\tШилов Игорь");
    }

    public static void printMenu() {
        System.out.println("\t\tМеню:");
        // System.out.println("1. Изменить свой Username");
        System.out.println("1. Начать чат");
        System.out.println("0. Закрыть программу");
    }

    public static void printServerMenu() {
        System.out.println("1. Ожидать подключения.");
        System.out.println("2. Подключиться к пользователю");
        System.out.println("0. Выйти");
    }
}
