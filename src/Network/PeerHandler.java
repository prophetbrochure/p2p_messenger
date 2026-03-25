package Network;

import java.net.Socket;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class PeerHandler implements Runnable {
    private Socket socket;
    private Peer peer;

    PeerHandler(Socket socket, Peer peer) {
        this.socket = socket;
        this.peer = peer;
    }

    @Override
    public void run() {
        
        new Thread(new Runnable() {
            public void run() {
                try {
                    
                    System.out.println("\n------------- Начало чата -------------");
                    InputStream input = socket.getInputStream();
                    InputStreamReader iSReader = new InputStreamReader(input);
                    BufferedReader reader = new BufferedReader(iSReader);
        
                    String message = "";
                    try {
                        while (!message.equals("/exit")) {
                            message = reader.readLine();
                            System.out.println("[" + socket.getLocalAddress() + "]: " + message);
                            peer.getHistory().add("[" + socket.getLocalAddress() + "]: " + message);
                        }
                        reader.close();
                        iSReader.close();
                        input.close();
                        socket.close();
                        System.out.println("Собеседник покинул чат. Нажмите Enter...");
                    } catch (IOException e1) {
                        reader.close();
                        iSReader.close();
                        input.close();
                        socket.close();
                        System.out.println("Собеседник покинул чат. Нажмите Enter...");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            
            OutputStream output = socket.getOutputStream();
            OutputStreamWriter oSWriter = new OutputStreamWriter(output);
            BufferedWriter writer = new BufferedWriter(oSWriter);
            
            System.out.println("Введите '/exit' чтобы отключиться.");
            System.out.println("Введите '/history' чтобы вывести историю чата.");
            Scanner scanner = new Scanner(System.in);
            
            String message = "";
            try {
                while (!message.equals("/exit")) {
                    message = scanner.nextLine();
                    if (message.equals("/history")) {
                        int i = 1;
                        System.out.println("\n--------------- History ---------------");
                        for (String line : peer.getHistory()) {
                            System.out.println(i++ + ") " + line);
                        }
                        continue;
                    }
                    writer.write(message);
                    writer.newLine();
                    writer.flush();
                    peer.getHistory().add("[Me]: " + message);
                }
                System.out.println("Чат закрывается.");
                
                scanner.close();
                writer.close();
                oSWriter.close();
                output.close();
                socket.close();
                
            } catch (IOException e1) {
                scanner.close();
                writer.close();
                oSWriter.close();
                output.close();
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("eror");
        }
    }
}
