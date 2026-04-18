package p2pmessenger.network;

import java.util.Scanner;

import p2pmessenger.util.IO;

public class ServerManager {
    public void run(Server server, String myAddress, String username, Scanner scanner) {
        server.start(username);
        while (!server.getChatOpened()) {
            IO.printServerMenu();
            String choice = scanner.nextLine();

            // Выйти. Закрыть сервер
            if (choice.equals("0")) {
                server.closeServer();
                break;
            }

            // Подключиться к комуто
            else if (choice.equals("1")) {
                String ip = IO.requestIP(scanner);
                int port = IO.requestPort(scanner);
                server.connect(ip, port, username, true);
            }

            // Вывести доступные чаты
            else if (choice.equals("2")) {
                System.out.println("Допуступные чаты:");
                if (server.getPeersList().isEmpty()) {
                    System.out.println("\nСписок пуст.\n");
                } else {
                    NetworkUtils.runPeerList(scanner, server);
                }
            }

            // Подключиться ко всем собеседникам с указанным портом
            else if (choice.equals("3")) {
                System.out.println("Попытка подключиться ко всем в сети c указанным портом.\n");

                String base = myAddress.substring(0, myAddress.lastIndexOf(".") + 1);
                final String tempUsername = username;
                final int commonPort = IO.requestPort(scanner);
                for (int i = 0; i <= 255; i++) {
                    final int index = i;
                    new Thread(() -> {
                        String tempIpAddress = base + index;
                        if (!tempIpAddress.equals(myAddress)) {
                            server.connect(tempIpAddress, commonPort, tempUsername, false);
                        }
                    }).start();
                }
            } else {
                System.out.println("Такой команды не существует.");
            }
        }
    }
}
