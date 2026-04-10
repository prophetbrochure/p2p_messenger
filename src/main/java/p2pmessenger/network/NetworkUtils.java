package p2pmessenger.network;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class NetworkUtils {
    public static Server createServer(Scanner scanner, int port) {
        Server server;
        while (true) {
            try {
                server = new Server(port, scanner);
                break;
            } catch (IOException e) {
                System.err.println("Ошибка. Порт занят, попробуйте другой.");
            }
        }
        return server;
    }

    public static void runPeerList(Scanner scanner, Server server) {
        int i = 1;
        for (PeerHandler peerHandler : server.getPeersList()) {
            System.out.println(i++ + ") " + peerHandler.getPeer().getUsername());
        }
        while (true) {
            String input = scanner.nextLine();
            try {
                int number = Integer.parseInt(input);

                server.setChatOpened(true);
                server.getPeersList().get(number - 1).runWriter();
                break;
            } catch (NumberFormatException e) {
                System.err.println("Введите номер чата.");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Такого номера не существует.");
            }
        }
    }

    public static ArrayList<String> getLocalIP() throws SocketException {

        Enumeration<NetworkInterface> interfacesList = NetworkInterface.getNetworkInterfaces();
        ArrayList<String> addressesList = new ArrayList<>();
        while (interfacesList.hasMoreElements()) {
            NetworkInterface networkInterface = interfacesList.nextElement();
            if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                continue;
            }

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address) {
                    addressesList.add(addr.getHostAddress());
                }
            }
        }
        return addressesList;
    }

    public static String selectLocalNetwork(Scanner scanner) throws Exception {
        List<String> addressesList = getLocalIP();
        if (addressesList.size() >= 2) {
            System.out.println("Выбери локальную сеть.\n('" + addressesList.get(0) + "' по умолчанию, нажми Enter)");
            for (int i = 0; i < addressesList.size(); i++) {
                System.out.println(i + 1 + ") " + addressesList.get(i));
            }
            int number;
            while (true) {
                String input = scanner.nextLine();
                if (input.equals("")) {
                    input = "1";
                }
                try {
                    number = Integer.parseInt(input);
                    if (number > addressesList.size()) {
                        System.err.println("Такого номера не существует");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("Ошибка. Вводите только цифры");
                }
            }
            return addressesList.get(number - 1);
        } else {
            return addressesList.get(0);
        }
    }
}
