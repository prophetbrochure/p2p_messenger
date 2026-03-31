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
 //import server;

 public class Main extends Application {

     // --- 1. Поля класса для доступа из разных методов ---
     private ImageView wifiIcon;
     private Image noWifi;
     private Image wifi;
     //private server;
     
     // Стили кнопок и элементов
     private final String activeStyle = "-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;";
     private final String inactiveStyle = "-fx-background-color: white; -fx-text-fill: #5f6368; -fx-border-color: #dadce0; -fx-padding: 10 20;";
     private final String btnEnabledStyle = "-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand;";
     private final String btnDisabledStyle = "-fx-background-color: #cbd5e0; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: default;";
     private final String fieldStyle = "-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 15px; -fx-padding: 0 15 0 15;";

     @Override
     public void start(Stage primaryStage) {
         primaryStage.setTitle("P2P Messenger");

         // --- 2. Загрузка ресурсов ---
         try {
             InputStream mainIcon = getClass().getResourceAsStream("/icon.png");
             if (mainIcon != null) primaryStage.getIcons().add(new Image(mainIcon));

             InputStream isNo = getClass().getResourceAsStream("/icon-no-wifi.png");
             InputStream isYes = getClass().getResourceAsStream("/icon-wifi.png");
             if (isNo != null) noWifi = new Image(isNo);
             if (isYes != null) wifi = new Image(isYes);
         } catch (Exception e) {
             System.out.println("Ошибка загрузки изображений: " + e.getMessage());
         }

         // --- 3. Создание узлов интерфейса (UI Nodes) ---

         // Переключатель режима
         Button btn_Wait = new Button("Wait for Connection");
         Button btn_Con = new Button("Connect to User");

         // Карточка
         VBox card = new VBox(15);
         card.setPadding(new Insets(30));
         card.setMaxWidth(380);
         card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 15, 0, 0, 8);");

         // Заголовок
         wifiIcon = new ImageView();
         wifiIcon.setFitWidth(24);
         wifiIcon.setPreserveRatio(true);
         if (noWifi != null) wifiIcon.setImage(noWifi);

         Text title = new Text("Wait for Connection");
         title.setStyle("-fx-font-size: 20px; -fx-font-family: 'Segoe UI', sans-serif; -fx-fill: #2d3748;");

         HBox headerLine = new HBox(10, wifiIcon, title);
         headerLine.setAlignment(Pos.CENTER_LEFT);

         Text description = new Text("Start listening for incoming connections on a specific port");
         description.setStyle("-fx-fill: #718096; -fx-font-size: 13px;");
         description.setWrappingWidth(300);

         // Поля ввода
         Label nameLabel = new Label("Your Nickname");
         nameLabel.setStyle("-fx-text-fill: #4a5568; -fx-font-weight: bold;");
         TextField nameField = new TextField();
         nameField.setPromptText("Enter your nickname");
         nameField.setPrefHeight(45);
         nameField.setStyle(fieldStyle);

         Label portLabel = new Label("Your Port");
         portLabel.setStyle("-fx-text-fill: #4a5568; -fx-font-weight: bold;");
         TextField portField = new TextField("5000");
         portField.setPrefHeight(45);
         portField.setStyle(fieldStyle);

         VBox inputFieldsContainer = new VBox(10, portLabel, portField);

         Button actionBtn = new Button("Start Waiting");
         actionBtn.setMaxWidth(Double.MAX_VALUE);
         actionBtn.setPrefHeight(48);

         // --- 4. Функциональная логика ---

         // Проверка заполнения полей
         Runnable updateUIState = () -> {
             boolean isPortFilled = !portField.getText().trim().isEmpty();
             boolean isNameFilled = !nameField.getText().trim().isEmpty();
             boolean isConnectMode = inputFieldsContainer.getChildren().contains(nameField);

             boolean canActivate = isConnectMode ? (isPortFilled && isNameFilled) : isPortFilled;

             actionBtn.setStyle(canActivate ? btnEnabledStyle : btnDisabledStyle);
             actionBtn.setDisable(!canActivate);
         };

         // Слушатели ввода
         portField.textProperty().addListener((obs, old, val) -> updateUIState.run());
         nameField.textProperty().addListener((obs, old, val) -> updateUIState.run());

         // Действие: Режим Ожидания
         btn_Wait.setOnAction(e -> {
             btn_Wait.setStyle(activeStyle + "-fx-background-radius: 10 0 0 10; -fx-border-radius: 10 0 0 10;");
             btn_Con.setStyle(inactiveStyle + "-fx-background-radius: 0 10 10 0; -fx-border-radius: 0 10 10 0; -fx-border-width: 1 1 1 0;");

             if (noWifi != null) wifiIcon.setImage(noWifi);
             title.setText("Wait for Connection");
             description.setText("Start listening for incoming connections on a specific port");
             portLabel.setText("Your Port");
             actionBtn.setText("Start Waiting");

             inputFieldsContainer.getChildren().setAll(portLabel, portField);
             updateUIState.run();
         });

         // Действие: Режим Подключения
         btn_Con.setOnAction(e -> {
             btn_Con.setStyle(activeStyle + "-fx-background-radius: 0 10 10 0; -fx-border-radius: 0 10 10 0;");
             btn_Wait.setStyle(inactiveStyle + "-fx-background-radius: 10 0 0 10; -fx-border-radius: 10 0 0 10; -fx-border-width: 1 1 1 0;");

             if (wifi != null) wifiIcon.setImage(wifi);
             title.setText("Connect to User");
             description.setText("Create a connection to another user's port");
             portLabel.setText("Target Port");
             actionBtn.setText("Create Connection");

             inputFieldsContainer.getChildren().setAll(nameLabel, nameField, portLabel, portField);
             updateUIState.run();
         });

         // --- 5. Компоновка (Layout Assembly) ---

         HBox toggleGroup = new HBox(0, btn_Wait, btn_Con);
         toggleGroup.setAlignment(Pos.CENTER);

         // Начальное состояние кнопок переключателя
         btn_Wait.setStyle(activeStyle + "-fx-background-radius: 10 0 0 10; -fx-border-radius: 10 0 0 10;");
         btn_Con.setStyle(inactiveStyle + "-fx-background-radius: 0 10 10 0; -fx-border-radius: 0 10 10 0; -fx-border-width: 1 1 1 0;");

         card.getChildren().addAll(headerLine, description, inputFieldsContainer, actionBtn);

         VBox mainLayout = new VBox(30, toggleGroup, card);
         mainLayout.setAlignment(Pos.CENTER);
         mainLayout.setStyle("-fx-background-color: #f8f9fa;"); // Светло-серый фон окна

         // Устанавливаем начальное состояние кнопки
         updateUIState.run();

         Scene scene = new Scene(new StackPane(mainLayout), 600, 550);
         primaryStage.setScene(scene);
         primaryStage.show();
     }

     public static void main(String[] args) {
         launch(args);
     }
 }