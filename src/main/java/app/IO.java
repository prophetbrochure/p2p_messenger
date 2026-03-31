package app;

import java.util.Scanner;

/**
 * Класс для работы с вводом/выводом.
 */
public class IO {
    private static final int DEFAULT_PORT = 5000;
    private static final String DEFAULT_IP = "127.0.0.1";
    private static final String DEFAULT_USER_NAME = "User";

    // INPUT
    public static String requestUsername(Scanner scanner) {
        System.out.println("Введи Имя пользователя, для идентификации в сети.");
        String input = scanner.nextLine();
        while (true) {
            try {
                if (input.isEmpty()) {
                    return DEFAULT_USER_NAME;
                }
                return input;
            } catch (Exception e) {
                System.err.println("Ошибка. Неверный формат Имени.");
            }
        }
    }

    public static String requestIP(Scanner scanner) {
        System.out.println("Введи Айпи пользователя.\n(" + DEFAULT_IP + " по умолчанию, нажми Enter)");
        while (true) {
            String input = scanner.nextLine();
            try {
                return getIp(input);
            } catch (NumberFormatException e) {
                System.err.println("Ошибка. Айпи должен состоять только из цыфр");
            }
        }
    }

    private static String getIp(String input) {
        if (input.isEmpty()) {
            return DEFAULT_IP;
        }
        // TODO Логика обработки неверного Айпи
        return input;
    }

    public static int requestPort(Scanner scanner) {
        System.out.println("Введите порт пользователя.\n(" + DEFAULT_PORT + " по умолчанию, нажми Enter)");
        while (true) {
            String input = scanner.nextLine();
            try {
                return getPort(input);
            } catch (NumberFormatException e) {
                System.err.println("Ошибка. Порт должен состоять только из цыфр");
            }
        }
    }

    private static int getPort(String input) {
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
        System.out.println("Над проектом работали:\n\tДмитриев Андрей\n\tКорзухин Михаил\n\tСтройлов Виталий\n\tШилов Игорь");
    }

    public static void printServerMenu() {
        System.out.println("\n------------- Сервер Меню -------------");
        System.out.println("1. Подключиться к пользователю");
        System.out.println("2. Список подключений (Пиров)");
        System.out.println("0. Выйти");
    }
}
