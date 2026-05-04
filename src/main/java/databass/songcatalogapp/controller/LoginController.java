package databass.songcatalogapp.controller;

import databass.songcatalogapp.MainApplication;
import databass.songcatalogapp.Session;
import databass.songcatalogapp.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Please enter both username and password.");
            return;
        }

        String sql = "SELECT user_id, username FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Session.setCurrentUserId(rs.getInt("user_id"));
                Session.setCurrentUsername(rs.getString("username"));
                MainApplication.changeScene("home-view.fxml");
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert.AlertType type = title.equals("Database Error") || title.equals("Login Failed") || title.equals("Login Error")
                ? Alert.AlertType.ERROR
                : Alert.AlertType.INFORMATION;

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}