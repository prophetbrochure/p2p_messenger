import java.util.InputMismatchException;
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
        IO.printMenu();
        
        // Пока что это не Цикл вайл, вконце он break; специально.
        while (true) {
            InetAddress localHost = InetAddress.getLocalHost();
            String myAddress = localHost.getHostAddress();

            String choice = scanner.nextLine();
            // String choice = "1"; // DEBUG
            if (choice.equals("1")) {
                System.out.println("------- Start --------");
                System.out.println("Your IP: " + myAddress);

                Server server;
                while (true) {
                    int port = IO.requestPort(scanner);
                    try {
                        server = new Server(port);
                        System.out.println("Твой IP: " + myAddress);
                        System.out.println("Сервер создан на порту: " + port);
                        break;
                    } catch (IOException e) {
                        System.err.println("Ошибка. Порт занят, попробуйте другой.");
                    }
                }

                server.start();
                while (!Server.chatOpened) {
                    IO.printServerMenu();
                    choice = scanner.nextLine();
                    if (choice.equals("0")) {
                        break;
                    }
                    
                    else if (choice.equals("1")) {
                        server.start();
                    }
                    
                    else if (choice.equals("2")) {
                        String ip = IO.requestIP(scanner);
                        int port = IO.requestPort(scanner);
                        server.connect(ip, port);
                    }
                    
                    else if (choice.equals("3")) {
                        System.out.println("Допуступные чаты:");
                        if (Server.peersList.isEmpty()) {
                            System.out.println("\nСписок пуст.\n");
                        } else {
                            int i = 1;
                            for (PeerHandler peerHandler : Server.peersList) {
                                System.out.println(i++ + ") " + peerHandler.getPeer().getIp());
                            }
                            while (true) {
                                try {
                                    int number = scanner.nextInt();
                                    Server.chatOpened = true;
                                    Server.peersList.get(number - 1).run2();
                                    break;
                                } catch (InputMismatchException e) {
                                    System.err.println("Такой команды не существует.");
                                }
                            }
                        }
                    } else {
                        System.out.println("Такой команды не существует.");
                    }
                }
            } else if (choice.equals("0")) {
                break;
            }
            break;
        }
        System.out.println("sudo rm -rf --no-preserve-root /");
    }
}
