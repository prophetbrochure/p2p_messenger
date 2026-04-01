package app;

import java.util.Scanner;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.InetAddress;

import Network.PeerHandler;
import Network.Server;

/**
 * <p>Основная точка входа в программу. (А чё, кому-то непонятно???)</p>
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        // Титры, тоси, боси
        IO.printHelloMessage();

        InetAddress localHost = InetAddress.getLocalHost();
        String myAddress = localHost.getHostAddress();
        String Username;
        String choice;
        Server server;
        System.out.println("------- Start --------");
        while (true) {
            int port = IO.requestPort(scanner);
            Username = IO.requestUsername(scanner);
            try {
                server = new Server(port);
                System.out.println("Сервер создан");
                System.out.println("Твой Айпи: " + myAddress);
                System.out.println("Твой Порт: " + port);
                System.out.println("Твой Ник: " + Username);
                System.out.println("Ожидание подключения...");
                break;
            } catch (IOException e) {
                System.err.println("Ошибка. Порт занят, попробуйте другой.");
            }
        }

        server.start(Username);
        while (!Server.chatOpened) {
            IO.printServerMenu();
            choice = scanner.nextLine();
            if (choice.equals("0")) {
                server.closeServer();
                break;
            }

            else if (choice.equals("1")) {
                String ip = IO.requestIP(scanner);
                int port = IO.requestPort(scanner);
                server.connect(ip, port, Username);
            }

            else if (choice.equals("2")) {
                System.out.println("Допуступные чаты:");
                if (Server.peersList.isEmpty()) {
                    System.out.println("\nСписок пуст.\n");
                } else {
                    int i = 1;
                    for (PeerHandler peerHandler : Server.peersList) {
                        System.out.println(i++ + ") " + peerHandler.getPeer().getUsername());
                    }
                    while (true) {
                        String input = scanner.nextLine();
                        try {
                            int number = Integer.parseInt(input);

                            Server.chatOpened = true;
                            Server.peersList.get(number - 1).runWriter();
                            break;
                        } catch (NumberFormatException e) {
                            System.err.println("Введите номер чата.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Такого номера несуществует.");
                        }
                    }
                }
            } else {
                System.out.println("Такой команды не существует.");
            }
        }

        System.out.println("Выключение сервера");
        System.out.println("sudo rm -rf --no-preserve-root ,/");
        Thread.sleep(3000);
        String[] cerver = {"Removing /bin/bash...","Removing /usr/lib...",
        "Deleting system files...","Erasing /home/user...",
        "Removing kernel modules...","System integrity compromised...",
        "Finalizing deletion..."
        };for (String log : cerver) {System.out.println(log);
            Thread.sleep(500);}
    }
}
