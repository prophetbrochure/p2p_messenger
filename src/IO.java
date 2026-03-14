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
        System.out.println("Введи ip, с которым хочешь начать чат (или чёт такое)");
        return scanner.nextLine();
    }


    // OUTPUT
    public static void printHelloMessage() {
        System.out.println("Добро пожаловать в P2P messenger");
        System.out.println("Над проектом работали:\n\tДмитриев Андрей\n\tКорзухин Михаил\n\tСтройлов Виталий\n\tШилов Игорь");
    }

    public static void printMenu() {
        System.out.println("\t\tМеню:");
        System.out.println("1. Изменить свой Username");
        System.out.println("2. Начать чат");
        System.out.println("-1. Закрыть программу");
    }

}
