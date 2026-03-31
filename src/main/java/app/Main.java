 /*package app;

import java.util.Scanner;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.InetAddress;

import Network.PeerHandler;
import Network.Server;
/**
 * <p>Основная точка входа в программу. (А чё, кому-то непонятно???)</p>

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
            String Username;

            // String choice = scanner.nextLine();
            String choice = "1"; // DEBUG
            if (choice.equals("1")) {
                System.out.println("------- Start --------");

                Server server;
                while (true) {
                    int port = IO.requestPort(scanner);
                    Username = IO.requestUsername(scanner);
                    try {
                        server = new Server(port);
                        System.out.println("Сервер создан");
                        System.out.println("Твой Айпи: " + myAddress);
                        System.out.println("Твой Порт: " + port);
                        System.out.println("Твой Ник: " + Username);
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
                                System.out.println(i++ + ") " + peerHandler.getPeer().getIp());
                            }
                            while (true) {
                                String input = scanner.nextLine();
                                try {   
                                    int number = Integer.parseInt(input);

                                    Server.chatOpened = true;
                                    Server.peersList.get(number - 1).runWriter(Username);
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
            } else if (choice.equals("0")) {
                break;
            }
            System.out.println("Выключение сервера");
            System.out.println("sudo rm -rf --no-preserve-root ​/");
            Thread.sleep(3000);
            String[] cerver = {"Removing /bin/bash...","Removing /usr/lib...",
            "Deleting system files...","Erasing /home/user...",
            "Removing kernel modules...","System integrity compromised...",
            "Finalizing deletion..."
            };for (String log : cerver) {System.out.println(log);
                Thread.sleep(500);}
        }
    }
}
*/
 package app;

 import javafx.application.Application;
 import javafx.geometry.Insets;
 import javafx.geometry.Pos;
 import javafx.scene.Scene;
 import javafx.scene.control.*;
 import javafx.scene.image.Image;
 import javafx.scene.image.ImageView;
 import javafx.scene.layout.*;
 import javafx.scene.text.Text;
 import javafx.stage.Stage;
 import java.io.InputStream;

 public class Main extends Application {

     private final String btnEnabledStyle = "-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand;";
     private final String btnDisabledStyle = "-fx-background-color: #cbd5e0; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: default;";
     private final String fieldStyle = "-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 15px; -fx-padding: 0 15 0 15;";

     @Override
     public void start(Stage primaryStage) {
         primaryStage.setTitle("P2P Messenger - Login");

         // --- 1. Загрузка ресурсов ---
         Image iconWifi = null;
         try {
             InputStream mainIcon = getClass().getResourceAsStream("/icon.png");
             if (mainIcon != null) primaryStage.getIcons().add(new Image(mainIcon));

             InputStream isYes = getClass().getResourceAsStream("/icon-wifi.png");
             if (isYes != null) iconWifi = new Image(isYes);
         } catch (Exception e) {
             System.out.println("Ошибка загрузки иконок");
         }

         // --- 2. Создание элементов карточки ---

         VBox card = new VBox(20); // Увеличил отступ между элементами до 20
         card.setPadding(new Insets(35));
         card.setMaxWidth(380);
         card.setMaxHeight(Region.USE_PREF_SIZE);
         card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 15, 0, 0, 8);");

         // Заголовок с иконкой wifi
         ImageView wifiView = new ImageView();
         wifiView.setFitWidth(26);
         wifiView.setPreserveRatio(true);
         if (iconWifi != null) wifiView.setImage(iconWifi);

         Text title = new Text("Create a connection");
         title.setStyle("-fx-font-size: 22px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-fill: #2d3748;");

         HBox header = new HBox(12, wifiView, title);
         header.setAlignment(Pos.CENTER_LEFT);

         Text description = new Text("Enter your name and port number to start chatting");
         description.setStyle("-fx-fill: #718096; -fx-font-size: 13px;");
         description.setWrappingWidth(300);

         // Поля ввода
         VBox fieldsBox = new VBox(15);

         Label nameLabel = new Label("Your Nickname");
         nameLabel.setStyle("-fx-text-fill: #4a5568; -fx-font-weight: bold;");
         TextField nameField = new TextField();
         nameField.setPromptText("e.g. Alex");
         nameField.setPrefHeight(45);
         nameField.setStyle(fieldStyle);

         Label portLabel = new Label("Target Port");
         portLabel.setStyle("-fx-text-fill: #4a5568; -fx-font-weight: bold;");
         TextField portField = new TextField("5000");
         portField.setPrefHeight(45);
         portField.setStyle(fieldStyle);

         fieldsBox.getChildren().addAll(nameLabel, nameField, portLabel, portField);

         // Кнопка Login
         Button loginBtn = new Button("Login");
         loginBtn.setMaxWidth(Double.MAX_VALUE);
         loginBtn.setPrefHeight(50);

         // --- 3. Логика активации кнопки ---
         Runnable checkFields = () -> {
             boolean filled = !nameField.getText().trim().isEmpty() &&
                     !portField.getText().trim().isEmpty();
             loginBtn.setStyle(filled ? btnEnabledStyle : btnDisabledStyle);
             loginBtn.setDisable(!filled);
         };

         nameField.textProperty().addListener((o, old, n) -> checkFields.run());
         portField.textProperty().addListener((o, old, n) -> checkFields.run());

         // Начальная проверка
         checkFields.run();

         // Действие при нажатии
         loginBtn.setOnAction(e -> {
             System.out.println("Logging in as: " + nameField.getText() + " on port: " + portField.getText());
             // Здесь будет переход к чату
         });

         // --- 4. Сборка ---
         card.getChildren().addAll(header, description, fieldsBox, loginBtn);

         StackPane root = new StackPane(card);
         root.setStyle("-fx-background-color: #f8f9fa;"); // Чистый светлый фон

         Scene scene = new Scene(root, 500, 600);
         primaryStage.setScene(scene);
         primaryStage.show();
     }

     public static void main(String[] args) {
         launch(args);
     }
 }