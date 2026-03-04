public class IO {

    // INPUT
    public static String requestUsername() {
        System.out.println("Твоё имя?");
        return "Антон (Чё звал сларк)";
    }

    public static String requestIP() {
        System.out.println("Введи ip, с которым хочешь начать чат (или чёт такое)");
        return "192.168.67.67";
    }


    // OUTPUT
    public static void printHelloMessage() {
        System.out.println("Добро пожаловать в P2P messenger");
        System.out.println("Над проектом работали:\n\tДмитриев Андрей\n\tКорзухин Михаил\n\tСтройлов Виталий\n\tШилов Игорь");
    }

    public static void printMenu() {
        System.out.println("1. Изменить свой Username");
        System.out.println("2. Начать чат");
        System.out.println("-1. Закрыть программу");
    }

}
