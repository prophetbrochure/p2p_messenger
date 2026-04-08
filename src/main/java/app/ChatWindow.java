package app;

import Network.Peer;
import Network.PeerHandler;
import Network.Server;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatWindow {

    private VBox contactList;
    private VBox messagesBox;
    private ScrollPane scrollPane;
    private Server server;
    private Text rightProfileName;
    private Text rightStatusText;
    private Circle rightStatusCircle;
    private Text rightAvatarLetter;
    private Text chatTitle;

    // Единое название для выбранного пользователя
    private Peer currentPeer;

    public void show(Stage stage, String nickname, Server server) {
        this.server = server;
        server.setChatWindow(this);
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // --- 1. ЛЕВАЯ ПАНЕЛЬ (SIDEBAR) ---
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(260);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 1 0 0;");

        VBox myProfileHeader = new VBox(2);
        myProfileHeader.setPadding(new Insets(0, 0, 15, 0));
        myProfileHeader.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");

        Text staticMyName = new Text(nickname);
        staticMyName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #1a202c;");

        String localIp;
        try {
            localIp = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            localIp = "127.0.0.1";
        }

        Text staticMyIp = new Text("My IP: " + localIp);
        staticMyIp.setStyle("-fx-font-size: 12px; -fx-fill: #718096;");
        Text staticMyPort = new Text("My Port: " + server.getPort());
        staticMyPort.setStyle("-fx-font-size: 12px; -fx-fill: #718096;");

        myProfileHeader.getChildren().addAll(staticMyName, staticMyIp, staticMyPort);

        Button addConBtn = new Button("+ Add Connection");
        addConBtn.setMaxWidth(Double.MAX_VALUE);
        addConBtn.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold; -fx-padding: 10;");
        addConBtn.setOnAction(e -> showAddConnectionDialog(stage, nickname));

        contactList = new VBox(5);
        VBox.setVgrow(contactList, Priority.ALWAYS);

        sidebar.getChildren().addAll(myProfileHeader, addConBtn, contactList);

        // --- 2. ЦЕНТРАЛЬНАЯ ПАНЕЛЬ (CHAT) ---
        VBox chatArea = new VBox();
        HBox chatHeader = new HBox(10);
        chatHeader.setPadding(new Insets(15));
        chatHeader.setAlignment(Pos.CENTER_LEFT);
        chatHeader.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");

        chatTitle = new Text("No active chat");
        chatTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        chatHeader.getChildren().add(new VBox(chatTitle));

        scrollPane = new ScrollPane();
        messagesBox = new VBox(15);
        messagesBox.setPadding(new Insets(20));
        messagesBox.setStyle("-fx-background-color: #f8f9fa;");
        scrollPane.setContent(messagesBox);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox inputArea = new HBox(10);
        inputArea.setPadding(new Insets(15));
        inputArea.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1 0 0 0;");

        TextField messageField = new TextField();
        messageField.setPromptText("Type a message...");
        HBox.setHgrow(messageField, Priority.ALWAYS);
        messageField.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: #f1f3f4;");

        Button sendBtn = new Button("Send");
        sendBtn.setDisable(true);
        sendBtn.setStyle("-fx-background-color: #cbd5e0; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 20;");

        messageField.textProperty().addListener((obs, old, newVal) -> {
            boolean empty = newVal.trim().isEmpty();
            sendBtn.setDisable(empty);
            sendBtn.setStyle(empty ? "-fx-background-color: #cbd5e0;" : "-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-cursor: hand;");
        });

        sendBtn.setOnAction(e -> {
            String text = messageField.getText().trim();
            if (!text.isEmpty() && currentPeer != null) {
                for (PeerHandler ph : Server.peersList) {
                    if (ph.getPeer().equals(currentPeer)) {
                        ph.send(text);
                        currentPeer.getHistory().add("You:" + text);
                        addMessageToUI("You", text, true);
                        messageField.clear();
                        break;
                    }
                }
            }
        });

        inputArea.getChildren().addAll(messageField, sendBtn);
        chatArea.getChildren().addAll(chatHeader, scrollPane, inputArea);

        // --- 3. ПРАВАЯ ПАНЕЛЬ ---
        VBox details = new VBox(20);
        details.setPrefWidth(240);
        details.setPadding(new Insets(30, 20, 20, 20));
        details.setAlignment(Pos.TOP_CENTER);
        details.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 0 1;");

        rightAvatarLetter = new Text("?");
        rightAvatarLetter.setStyle("-fx-fill: white; -fx-font-size: 30px; -fx-font-weight: bold;");
        StackPane avatarStack = new StackPane(new Circle(40, Color.web("#6366f1")), rightAvatarLetter);

        rightProfileName = new Text("Waiting...");
        rightProfileName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        rightStatusCircle = new Circle(5, Color.GRAY);
        rightStatusText = new Text("Offline");
        HBox statusBox = new HBox(5, rightStatusCircle, rightStatusText);
        statusBox.setAlignment(Pos.CENTER);

        details.getChildren().addAll(avatarStack, rightProfileName, statusBox);

        root.setLeft(sidebar);
        root.setCenter(chatArea);
        root.setRight(details);

        updateUI();
        stage.setScene(new Scene(root, 1100, 700));

        stage.setMinWidth(900);
        stage.setMinHeight(600);

        stage.centerOnScreen();
        stage.show();
    }

    public void selectChat(Peer peer) {
        this.currentPeer = peer;
        messagesBox.getChildren().clear();

        chatTitle.setText(peer.getUsername());
        rightProfileName.setText(peer.getUsername());
        rightAvatarLetter.setText(peer.getUsername().isEmpty() ? "?" : peer.getUsername().substring(0, 1).toUpperCase());

        updateStatusUI(peer.isOnline());

        for (String msg : peer.getHistory()) {
            if (msg.startsWith("You:")) addMessageToUI("You", msg.substring(4), true);
            else if (msg.startsWith("Other:")) addMessageToUI(peer.getUsername(), msg.substring(6), false);
        }
    }

    private void updateStatusUI(boolean online) {
        rightStatusText.setText(online ? "Online" : "Offline");
        rightStatusCircle.setFill(online ? Color.web("#38a169") : Color.GRAY);
    }

    public void onIncomingMessage(Peer sender, String message) {
        Platform.runLater(() -> {
            if (currentPeer != null && currentPeer.equals(sender)) {
                addMessageToUI(sender.getUsername(), message, false);
            }
        });
    }

    public void updateUI() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::updateUI);
            return;
        }
        contactList.getChildren().clear();
        for (PeerHandler ph : Server.peersList) {
            HBox item = createContactItem(ph.getPeer());
            contactList.getChildren().add(item);
        }
    }

    private HBox createContactItem(Peer peer) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(10));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: #f1f3f4; -fx-background-radius: 8; -fx-cursor: hand;");

        item.setOnMouseClicked(e -> selectChat(peer));

        Circle statusCircle = new Circle(5, peer.isOnline() ? Color.web("#38a169") : Color.web("#718096"));
        VBox txt = new VBox(2);
        Text nameText = new Text(peer.getUsername());
        Text statusLabel = new Text(peer.isOnline() ? "Online" : "Offline");
        statusLabel.setStyle("-fx-font-size: 10px;");

        txt.getChildren().addAll(nameText, statusLabel);
        item.getChildren().addAll(statusCircle, txt);
        if (!peer.isOnline()) item.setOpacity(0.7);

        return item;
    }

    private void addMessageToUI(String sender, String text, boolean isMine) {
        Platform.runLater(() -> {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            messagesBox.getChildren().add(createMessageBubble(sender, text, time, isMine));
            scrollPane.setVvalue(1.0);
        });
    }

    private HBox createMessageBubble(String sender, String text, String time, boolean isMine) {
        VBox bubble = new VBox(2);
        bubble.setPadding(new Insets(10));
        bubble.setMaxWidth(300);
        bubble.setStyle(isMine ?
                "-fx-background-color: #1a73e8; -fx-background-radius: 15 15 2 15;" :
                "-fx-background-color: white; -fx-background-radius: 15 15 15 2; -fx-border-color: #e2e8f0;");

        Text s = new Text(isMine ? "Вы" : sender);
        s.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        s.setFill(isMine ? Color.LIGHTGRAY : Color.GRAY);

        Text t = new Text(text);
        t.setFill(isMine ? Color.WHITE : Color.BLACK);
        t.setWrappingWidth(280);

        Text tm = new Text(time);
        tm.setStyle("-fx-font-size: 9px;");
        tm.setFill(isMine ? Color.LIGHTGRAY : Color.GRAY);

        bubble.getChildren().addAll(s, t, tm);
        HBox container = new HBox(bubble);
        container.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return container;
    }
    //===============================СТИЛИ СЮДА==============================================
    private void showAddConnectionDialog(Stage owner, String myNickname) {
        Stage dialog = new Stage();
        dialog.initOwner(owner); // Привязываем окно к основному
        dialog.setTitle("Add Connection");

        // Пытаемся поставить иконку и для этого окна тоже
        try {
            InputStream iconStream = getClass().getResourceAsStream("/icon.png");
            if (iconStream != null) {
                dialog.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            System.out.println("Иконка для диалога не загружена");
        }

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f8f9fa;"); // Светлый фон для диалога

        Label ipLabel = new Label("IP Address");
        TextField ip = new TextField("127.0.0.1");
        ip.setPrefHeight(40);
        ip.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 0 10;");

        Label portLabel = new Label("Port");
        TextField port = new TextField();
        port.setPromptText("e.g. 8081");
        port.setPrefHeight(40);
        port.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 0 10;");

        Button btn = new Button("Connect");
        btn.setMaxWidth(Double.MAX_VALUE);
        // Делаем кнопку сразу синей, так как IP уже заполнен
        btn.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10; -fx-font-weight: bold; -fx-cursor: hand;");

        btn.setOnAction(e -> {
            String portText = port.getText().trim();
            if (!portText.isEmpty()) {
                new Thread(() -> {
                    try {
                        server.connect(ip.getText(), Integer.parseInt(portText), myNickname);
                        // После коннекта принудительно обновляем список в UI
                        Platform.runLater(this::updateUI);
                    } catch (NumberFormatException ex) {
                        System.out.println("Неверный формат порта");
                    }
                }).start();
                dialog.close();
            }
        });

        layout.getChildren().addAll(ipLabel, ip, portLabel, port, btn);

        Scene scene = new Scene(layout, 300, 280);
        dialog.setScene(scene);
        dialog.setResizable(false); // Окно коннекта лучше сделать фиксированным
        dialog.show();
    }
}