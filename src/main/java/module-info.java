module p2p.messenger {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens app to javafx.graphics, javafx.fxml;
    exports app;
}