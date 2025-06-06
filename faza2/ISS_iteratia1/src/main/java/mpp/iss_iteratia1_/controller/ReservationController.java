package mpp.iss_iteratia1_.controller;

import mpp.iss_iteratia1_.domain.*;
import mpp.iss_iteratia1_.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.*;
import java.util.stream.Collectors;

public class ReservationController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private ComboBox<Play> playComboBox;
    @FXML
    private GridPane seatGrid;
    @FXML
    private VBox selectedSeatsBox;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Button reserveButton;
    @FXML
    private Button refreshButton;
    @FXML
    private ListView<Reservation> reservationsList;

    private User currentUser;
    private PlayService playService;
    private SeatService seatService;
    private ReservationService reservationService;
    private UserService userService;

    private List<Seat> selectedSeats = new ArrayList<>();
    private ObservableList<Play> plays = FXCollections.observableArrayList();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
        }
    }

    public void initializeServices() {
        // These would be injected in a real application
        // For now, they need to be set from the main application
    }

    public void setServices(PlayService playService, SeatService seatService,
                            ReservationService reservationService, UserService userService) {
        this.playService = playService;
        this.seatService = seatService;
        this.reservationService = reservationService;
        this.userService = userService;

        loadPlays();
        loadUserReservations();
    }

    @FXML
    private void initialize() {
        playComboBox.setOnAction(this::handlePlaySelection);
        reserveButton.setDisable(true);
        totalPriceLabel.setText("Total: $0");

        // Set up play combo box display
        playComboBox.setCellFactory(param -> new ListCell<Play>() {
            @Override
            protected void updateItem(Play item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getPlayName() + " - " + item.getDate().toLocalDate());
                }
            }
        });

        playComboBox.setButtonCell(new ListCell<Play>() {
            @Override
            protected void updateItem(Play item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getPlayName() + " - " + item.getDate().toLocalDate());
                }
            }
        });
    }

    private void loadPlays() {
        if (playService != null) {
            plays.clear();
            for (Play play : playService.findAll()) {
                plays.add(play);
            }
            playComboBox.setItems(plays);
        }
    }

    private void loadUserReservations() {
        if (reservationService != null && currentUser != null) {
            ObservableList<Reservation> userReservations = FXCollections.observableArrayList();
            for (Reservation reservation : reservationService.findReservationsByUserId(currentUser.getId())) {
                userReservations.add(reservation);
            }
            reservationsList.setItems(userReservations);

            // Set up reservation list display
            reservationsList.setCellFactory(param -> new ListCell<Reservation>() {
                @Override
                protected void updateItem(Reservation item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String seatInfo = item.getSeats().stream()
                                .map(seat -> seat.getRow() + seat.getNumber())
                                .collect(Collectors.joining(", "));
                        setText(item.getPlay().getPlayName() + " - Seats: " + seatInfo);
                    }
                }
            });
        }
    }

    @FXML
    private void handlePlaySelection(ActionEvent event) {
        Play selectedPlay = playComboBox.getSelectionModel().getSelectedItem();
        if (selectedPlay != null) {
            loadSeats(selectedPlay);
            clearSelection();
        }
    }

    private void loadSeats(Play play) {
        seatGrid.getChildren().clear();
        selectedSeats.clear();

        if (seatService != null) {
            List<Seat> seats = new ArrayList<>();
            for (Seat seat : seatService.findSeatsByPlayId(play.getId())) {
                seats.add(seat);
            }

            // Group seats by row
            Map<String, List<Seat>> seatsByRow = seats.stream()
                    .collect(Collectors.groupingBy(Seat::getRow));

            int rowIndex = 0;
            for (Map.Entry<String, List<Seat>> entry : seatsByRow.entrySet()) {
                String row = entry.getKey();
                List<Seat> rowSeats = entry.getValue();
                rowSeats.sort(Comparator.comparing(Seat::getNumber));

                // Add row label
                Label rowLabel = new Label(row);
                rowLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
                seatGrid.add(rowLabel, 0, rowIndex);

                // Add seats
                for (int i = 0; i < rowSeats.size(); i++) {
                    Seat seat = rowSeats.get(i);
                    Button seatButton = createSeatButton(seat);
                    seatGrid.add(seatButton, i + 1, rowIndex);
                }
                rowIndex++;
            }
        }

        seatGrid.setPadding(new Insets(10));
        seatGrid.setHgap(5);
        seatGrid.setVgap(5);
    }

    private Button createSeatButton(Seat seat) {
        Button button = new Button(String.valueOf(seat.getNumber()));
        button.setPrefSize(40, 40);

        if (seat.isAvailable()) {
            button.setStyle("-fx-background-color: lightgreen;");
            button.setOnAction(e -> toggleSeatSelection(seat, button));
        } else {
            button.setStyle("-fx-background-color: lightcoral;");
            button.setDisable(true);
        }

        // Add tooltip with price
        Tooltip tooltip = new Tooltip("$" + seat.getPrice());
        button.setTooltip(tooltip);

        return button;
    }

    private void toggleSeatSelection(Seat seat, Button button) {
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
            button.setStyle("-fx-background-color: lightgreen;");
        } else {
            selectedSeats.add(seat);
            button.setStyle("-fx-background-color: yellow;");
        }

        updateSelectedSeatsDisplay();
        updateTotalPrice();
        reserveButton.setDisable(selectedSeats.isEmpty());
    }

    private void updateSelectedSeatsDisplay() {
        selectedSeatsBox.getChildren().clear();

        if (!selectedSeats.isEmpty()) {
            Label headerLabel = new Label("Selected Seats:");
            headerLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
            selectedSeatsBox.getChildren().add(headerLabel);

            for (Seat seat : selectedSeats) {
                Label seatLabel = new Label(seat.getRow() + seat.getNumber() + " - $" + seat.getPrice());
                selectedSeatsBox.getChildren().add(seatLabel);
            }
        }
    }

    private void updateTotalPrice() {
        int total = selectedSeats.stream().mapToInt(Seat::getPrice).sum();
        totalPriceLabel.setText("Total: $" + total);
    }

    @FXML
    private void handleReservation(ActionEvent event) {
        if (selectedSeats.isEmpty()) {
            showAlert("No seats selected", "Please select at least one seat.", Alert.AlertType.WARNING);
            return;
        }

        Play selectedPlay = playComboBox.getSelectionModel().getSelectedItem();
        if (selectedPlay == null) {
            showAlert("No play selected", "Please select a play.", Alert.AlertType.WARNING);
            return;
        }

        // Show confirmation dialog
        int totalPrice = reservationService.calculateTotalPrice(selectedSeats);
        String seatInfo = selectedSeats.stream()
                .map(seat -> seat.getRow() + seat.getNumber())
                .collect(Collectors.joining(", "));

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Reservation");
        confirmationAlert.setHeaderText("Confirm your reservation");
        confirmationAlert.setContentText("Play: " + selectedPlay.getPlayName() + "\n" +
                "Seats: " + seatInfo + "\n" +
                "Total Price: $" + totalPrice);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = reservationService.makeReservation(currentUser, selectedPlay, selectedSeats);

            if (success) {
                showAlert("Reservation Successful",
                        "Your reservation has been confirmed!",
                        Alert.AlertType.INFORMATION);

                // Refresh the display
                loadSeats(selectedPlay);
                loadUserReservations();
            } else {
                showAlert("Reservation Failed",
                        "Some seats may no longer be available. Please try again.",
                        Alert.AlertType.ERROR);
                handleRefresh(null);
            }
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        Play selectedPlay = playComboBox.getSelectionModel().getSelectedItem();
        loadPlays();
        if (selectedPlay != null) {
            playComboBox.getSelectionModel().select(selectedPlay);
            loadSeats(selectedPlay);
        }
        loadUserReservations();
        clearSelection();
    }

    private void clearSelection() {
        selectedSeats.clear();
        updateSelectedSeatsDisplay();
        updateTotalPrice();
        reserveButton.setDisable(true);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}