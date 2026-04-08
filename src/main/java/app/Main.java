package app;

import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import Network.Server;

/**
 * <p>
 * Основная точка входа в программу. (А чё, кому-то непонятно???)
 * </p>
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        IO.printHelloMessage();

        System.out.println("------- Start --------");
        String myAddress = IO.selectLocalNetwork(scanner);
        
        String Username = null;
        String choice;
        int port = 0;


        port = IO.requestPort(scanner);
        Server server = IO.createServer(scanner, port);

        Username = IO.requestUsername(scanner);
        IO.printServerInfo(myAddress, port, Username);

        ZeroConfProtocol zcp = new ZeroConfProtocol();
        zcp.zeroConfProtocol(myAddress, port, Username, server);

        server.start(Username);
        while (!Server.chatOpened) {
            IO.printServerMenu();
            choice = scanner.nextLine();

            // Выйти. Закрыть сервер
            if (choice.equals("0")) {
                server.closeServer();
                break;
            }

            // Подключиться к комуто
            else if (choice.equals("1")) {
                String ip = IO.requestIP(scanner);
                port = IO.requestPort(scanner);
                server.connect(ip, port, Username, true);
            }

            // Вывести доступные контакты
            else if (choice.equals("2")) {
                System.out.println("Допуступные чаты:");
                if (Server.peersList.isEmpty()) {
                    System.out.println("\nСписок пуст.\n");
                } else {
                    IO.runPeerList(scanner);
                }
            }

            // Подключиться ко всем собеседникам с указанным портом
            else if (choice.equals("3")) {
                System.out.println("Попытка подключиться ко всем в сети c указанным портом.\n");

                // Первые 3 актета как у myAddress. Они не проверяются
                String base = myAddress.substring(0, myAddress.lastIndexOf(".") + 1);

                final String tempUsername = Username;
                final int commonPort = IO.requestPort(scanner);
                for (int i = 0; i <= 255; i++) {
                    final int index = i;
                    new Thread(() -> {
                        String tempIpAddress = base + index;
                        if (tempIpAddress.equals(myAddress)) {
                            tempIpAddress = base + index + 1;
                        }
                        server.connect(tempIpAddress, commonPort, tempUsername, false);
                    }).start();
                }
            } else {
                System.out.println("Такой команды не существует.");
            }
        }

        System.out.println("Выключение сервера");
        System.out.println("sudo rm -rf --no-preserve-root ,/");
        Thread.sleep(3000);String[] cerver = {"Removing /bin/bash...",
        "Removing /usr/lib...","Deleting system files...",
        "Erasing /home/user...","Removing kernel modules...",
        "System integrity compromised...","Finalizing deletion..."};
        for(String log:cerver){System.out.println(log);Thread.sleep(500);}
        System.exit(1);
    }
}
