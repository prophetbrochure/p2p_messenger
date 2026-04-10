package p2pmessenger.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
        System.out.println("Введи Имя пользователя.\n('" + DEFAULT_USER_NAME + "' по умолчанию, нажми Enter)");
        while (true) {
            String input = scanner.nextLine();
            try {
                if (input.isEmpty()) {
                    return DEFAULT_USER_NAME;
                } else if (input.length() > 20) {
                    System.err.println("Ошибка. Имя слишком длинное.");
                    continue;
                }
                return input;
            } catch (Exception e) {
                System.err.println("Ошибка. Неверный формат Имени.");
            }
        }
    }

    public static String requestIP(Scanner scanner) {
        System.out.println("Введи Айпи.\n('" + DEFAULT_IP + "' по умолчанию, нажми Enter)");
        while (true) {
            String input = scanner.nextLine();
            try {
                return getIp(input);
            } catch (UnknownHostException e) {
                System.err.println("Ошибка. Неверный формат Айпи или узел недоступен.");
            }
        }
    }

    private static String getIp(String input) throws UnknownHostException {
        if (input.isEmpty()) {
            return DEFAULT_IP;
        }
        InetAddress.getByName(input);
        return input;
    }

    public static int requestPort(Scanner scanner) {
        System.out.println("Введите порт.\n('" + DEFAULT_PORT + "' по умолчанию, нажми Enter)");
        while (true) {
            String input = scanner.nextLine();
            try {
                return getPort(input);
            } catch (NumberFormatException e) {
                System.err.println("Ошибка. Порт должен состоять только из цифр");
            } catch (IllegalArgumentException e) {
                System.err.println("Порт должен быть от 1 до 65535 !");
            }
        }
    }

    private static int getPort(String input) {
        if (input.isEmpty()) {
            return DEFAULT_PORT;
        }
        int port = Integer.parseInt(input);
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException();
        } else {
            return port;
        }
    }

    // OUTPUT
    public static void printHelloMessage() {
        System.out.println("Добро пожаловать в P2P messenger");
        System.out.println(
                "Над проектом работали:\n\tДмитриев Андрей\n\tКорзухин Михаил\n\tСтройлов Виталий\n\tШилов Игорь\n");
    }

    public static void printServerMenu() {
        System.out.println("\n------------- Сервер Меню -------------");
        System.out.println("1. Подключиться к пользователю");
        System.out.println("2. Список подключений (Пиров)");
        System.out.println("3. Просканировать сеть с указанным портом");
        System.out.println("0. Выйти");
    }

    public static void printServerInfo(String myAddress, int port, String username) {
        if (username != null) {
            System.out.println("\nСервер создан");
            System.out.println("Твой Айпи: " + myAddress);
            System.out.println("Твой Порт: " + port);
            System.out.println("Твой Ник: " + username);
        } else {
            System.out.println("Сервер не запущен");
        }
    }
}
