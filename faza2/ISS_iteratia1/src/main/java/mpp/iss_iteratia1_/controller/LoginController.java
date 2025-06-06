package mpp.iss_iteratia1_.controller;

import mpp.iss_iteratia1_.domain.User;
import mpp.iss_iteratia1_.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    private UserService userService;
    private Stage primaryStage;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        Optional<User> userOpt = userService.authenticate(username, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            try {


                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/mpp/iss_iteratia1_/reservation-view.fxml"));
                    Parent root = loader.load();

                    ReservationController reservationController = loader.getController();
                    reservationController.setCurrentUser(user);
                    reservationController.initializeServices();

                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Theater Reservation System - " + user.getUsername());
            } catch (IOException e) {
                showError("Error loading reservation screen: " + e.getMessage());
            }
        } else {
            showError("Invalid username or password.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}