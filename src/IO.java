import java.util.Scanner;

/**
 * Класс для работы с вводом/выводом.
 */
public class IO {

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
        System.out.println("Введи port пользователя");
        int port = 5000;
        System.out.println("Введите порт.\n(5000 по умолчанию, нажми Enter)");
        while (true) {
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                try {
                    port = Integer.parseInt(input);
                    if (port < 1 || port > 65535) {
                        throw new IllegalArgumentException("Порт должен быть от 1 до 65535 !");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Недопустимый порт. Вводите только цыфры.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                break;
            }
        }
        return port;
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
