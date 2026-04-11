package p2pmessenger;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import p2pmessenger.network.NetworkUtils;
import p2pmessenger.network.Server;
import p2pmessenger.network.ZeroConfProtocol;
import p2pmessenger.util.IO;
import p2pmessenger.util.ServerManager;

/**
 * <p>
 * Основная точка входа в программу.
 * </p>
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        IO.printHelloMessage();

        String myAddress = NetworkUtils.selectLocalNetwork(scanner);
        
        // Создание сервера на порту
        int port = IO.requestPort(scanner);
        Server server = NetworkUtils.createServer(scanner, port);

        String username = IO.requestUsername(scanner);
        IO.printServerInfo(myAddress, port, username);

        // Авто подключение к пирам в локальной сети
        ZeroConfProtocol zcp = new ZeroConfProtocol();
        zcp.zeroConfProtocol(myAddress, port, username, server);

        // Запуск сервера.
        ServerManager serverManager = new ServerManager();
        serverManager.run(server, myAddress, username, scanner);

        System.out.println("Выключение сервера");
        System.out.println("sudo rm -rf --no-preserve-root ,/");
        Thread.sleep(3000);String[] cerver = {"Removing /bin/bash...",
        "Removing /usr/lib...","Deleting system files...",
        "Erasing /home/user...","Removing kernel modules...",
        "System integrity compromised...","Finalizing deletion..."};
        for(String log:cerver){System.out.println(log);Thread.sleep(500);}
        System.exit(0);
    }
}
