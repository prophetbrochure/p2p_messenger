import java.io.IOException;
import java.util.Scanner;
import Network.Server;
import java.nio.charset.StandardCharsets;

/**
 * <p>Основная точка входа в программу. (А чё, кому-то непонятно???)</p>
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        // Титры, тоси, боси
        IO.printHelloMessage();
        IO.printMenu();
        
        // Пока что это не Цикл вайл, вконце он break; специально.
        while (true) {
            String choice = scanner.nextLine();
            // String choice = "1"; // DEBUG
            if (choice.equals("1")) {
                System.out.println("------- Start --------");
                Server server;
                while (true) {

                    try {
                        int port = 5000;
                        System.out.println("Введите порт для входа.\n(5000 по умолчанию, нажми Enter)");
                        while (true) {
                            String input = scanner.nextLine();
                            if (!input.isEmpty()) {
                                System.err.println("input " + input);
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
                        server = new Server(port);
                        break;
                    } catch (IOException e) {
                        System.err.println("Ошибка. Порт занят, попробуйте другой.");
                    }
                }

                System.out.println("1. Ожидать подключения.");
                System.out.println("2. Подключиться к пользователю");
                System.out.println("0. Выйти");
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

            } else if (choice.equals("0")) {
                break;
            }
            break;
        }
    }
}
