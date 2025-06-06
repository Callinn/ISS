package mpp.iss_iteratia1_;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mpp.iss_iteratia1_.controller.LoginController;
import mpp.iss_iteratia1_.domain.validators.UserValidator;
import mpp.iss_iteratia1_.repository.*;
import mpp.iss_iteratia1_.repository.database.*;
import mpp.iss_iteratia1_.services.*;

public class TheaterReservationApplication extends Application {

    private UserService userService;
    private PlayService playService;
    private SeatService seatService;
    private ReservationService reservationService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize services
        initializeServices();

        // Load login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mpp/iss_iteratia1_/login-view.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setUserService(userService);
        loginController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Theater Reservation System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeServices() {
        // Database configuration
        String url = "jdbc:postgresql://localhost:5432/teatru";
        String username = "postgres";
        String password = "admin";

        // Initialize validators
        UserValidator userValidator = new UserValidator();

        // Initialize repositories
        UserRepository userRepository = new UserDBRepository(url, username, password, userValidator);
        SeatRepository seatRepository = new SeatDBRepository(url, username, password);
        PlayRepository playRepository = new PlayDBRepository(url, username, password, seatRepository);
        ReservationRepository reservationRepository = new ReservationDBRepository(url, username, password,
                userRepository, playRepository, seatRepository);

        // Initialize services
        userService = new UserService(userRepository);
        seatService = new SeatService(seatRepository);
        playService = new PlayService(playRepository);
        reservationService = new ReservationService(reservationRepository, seatService);
    }

    public static void main(String[] args) {
        launch(args);
    }
}