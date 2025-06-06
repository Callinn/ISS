package mpp.iss_iteratia1_.repository.database;

import mpp.iss_iteratia1_.domain.Reservation;
import mpp.iss_iteratia1_.domain.User;
import mpp.iss_iteratia1_.domain.Play;
import mpp.iss_iteratia1_.domain.Seat;
import mpp.iss_iteratia1_.repository.ReservationRepository;
import mpp.iss_iteratia1_.repository.UserRepository;
import mpp.iss_iteratia1_.repository.PlayRepository;
import mpp.iss_iteratia1_.repository.SeatRepository;

import java.sql.*;
import java.util.*;

public class ReservationDBRepository implements ReservationRepository {
    private String url;
    private String username;
    private String password;
    private UserRepository userRepository;
    private PlayRepository playRepository;
    private SeatRepository seatRepository;

    public ReservationDBRepository(String url, String username, String password,
                                   UserRepository userRepository, PlayRepository playRepository,
                                   SeatRepository seatRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
        this.playRepository = playRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public Optional<Reservation> findOne(Long id) {
        String query = "SELECT * FROM Reservations WHERE id = ?";
        Reservation reservation = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long userId = resultSet.getLong("user_id");
                Long playId = resultSet.getLong("play_id");

                User user = userRepository.findOne(userId).orElse(null);
                Play play = playRepository.findOne(playId).orElse(null);
                List<Seat> seats = findSeatsByReservationId(id);

                if (user != null && play != null) {
                    reservation = new Reservation(user, seats, play);
                    reservation.setId(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(reservation);
    }

    @Override
    public Iterable<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Reservations");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long userId = resultSet.getLong("user_id");
                Long playId = resultSet.getLong("play_id");

                User user = userRepository.findOne(userId).orElse(null);
                Play play = playRepository.findOne(playId).orElse(null);
                List<Seat> seats = findSeatsByReservationId(id);

                if (user != null && play != null) {
                    Reservation reservation = new Reservation(user, seats, play);
                    reservation.setId(id);
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reservations;
    }

    @Override
    public Optional<Reservation> save(Reservation entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Reservation can't be null!");
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);

            // Insert reservation
            String reservationQuery = "INSERT INTO Reservations(user_id, play_id) VALUES (?, ?)";
            PreparedStatement reservationStmt = connection.prepareStatement(reservationQuery, Statement.RETURN_GENERATED_KEYS);
            reservationStmt.setLong(1, entity.getUser().getId());
            reservationStmt.setLong(2, entity.getPlay().getId());

            int affectedRows = reservationStmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = reservationStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Long reservationId = generatedKeys.getLong(1);
                    entity.setId(reservationId);

                    // Insert reservation-seat relationships
                    String seatQuery = "INSERT INTO Reservation_Seats(reservation_id, seat_id) VALUES (?, ?)";
                    PreparedStatement seatStmt = connection.prepareStatement(seatQuery);
                    for (Seat seat : entity.getSeats()) {
                        seatStmt.setLong(1, reservationId);
                        seatStmt.setLong(2, seat.getId());
                        seatStmt.addBatch();

                        // Update seat availability
                        seatRepository.updateSeatAvailability(seat.getId(), false);
                    }
                    seatStmt.executeBatch();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Reservation> delete(Long id) {
        Optional<Reservation> reservationToDelete = findOne(id);

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);

            // First, get seats to update their availability
            if (reservationToDelete.isPresent()) {
                for (Seat seat : reservationToDelete.get().getSeats()) {
                    seatRepository.updateSeatAvailability(seat.getId(), true);
                }
            }

            // Delete reservation (cascade will handle Reservation_Seats)
            String query = "DELETE FROM Reservations WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            statement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return reservationToDelete;
    }

    @Override
    public Optional<Reservation> update(Reservation entity) {
        // For simplicity, we'll implement this as delete + save
        delete(entity.getId());
        save(entity);
        return Optional.empty();
    }

    private List<Seat> findSeatsByReservationId(Long reservationId) {
        List<Seat> seats = new ArrayList<>();
        String query = """
            SELECT s.* FROM Seats s 
            JOIN Reservation_Seats rs ON s.id = rs.seat_id 
            WHERE rs.reservation_id = ?
        """;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, reservationId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String row = resultSet.getString("seat_row");
                int number = resultSet.getInt("seat_number");
                int price = resultSet.getInt("price");
                boolean isAvailable = resultSet.getBoolean("is_available");
                Seat seat = new Seat(row, number, price, isAvailable);
                seat.setId(id);
                seats.add(seat);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return seats;
    }

    @Override
    public Iterable<Reservation> findByUserId(Long userId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM Reservations WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long playId = resultSet.getLong("play_id");

                User user = userRepository.findOne(userId).orElse(null);
                Play play = playRepository.findOne(playId).orElse(null);
                List<Seat> seats = findSeatsByReservationId(id);

                if (user != null && play != null) {
                    Reservation reservation = new Reservation(user, seats, play);
                    reservation.setId(id);
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reservations;
    }
}
