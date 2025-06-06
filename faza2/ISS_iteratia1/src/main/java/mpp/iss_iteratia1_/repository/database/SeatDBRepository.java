package mpp.iss_iteratia1_.repository.database;

import mpp.iss_iteratia1_.domain.Seat;
import mpp.iss_iteratia1_.repository.SeatRepository;

import java.sql.*;
import java.util.*;

public class SeatDBRepository implements SeatRepository {
    private String url;
    private String username;
    private String password;

    public SeatDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Seat> findOne(Long id) {
        String query = "SELECT * FROM Seats WHERE id = ?";
        Seat seat = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String row = resultSet.getString("seat_row");
                int number = resultSet.getInt("seat_number");
                int price = resultSet.getInt("price");
                boolean isAvailable = resultSet.getBoolean("is_available");
                seat = new Seat(row, number, price, isAvailable);
                seat.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(seat);
    }

    @Override
    public Iterable<Seat> findAll() {
        List<Seat> seats = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Seats");
             ResultSet resultSet = statement.executeQuery()) {

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
    public Optional<Seat> save(Seat entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Seat can't be null!");
        }

        String query = "INSERT INTO Seats(seat_row, seat_number, price, is_available) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getRow());
            statement.setInt(2, entity.getNumber());
            statement.setInt(3, entity.getPrice());
            statement.setBoolean(4, entity.isAvailable());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }


    @Override
    public Optional<Seat> delete(Long id) {
        Optional<Seat> seatToDelete = findOne(id);
        String query = "DELETE FROM Seats WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return seatToDelete;
    }

    @Override
    public Optional<Seat> update(Seat entity) {
        String query = "UPDATE Seats SET seat_row = ?, seat_number = ?, price = ?, is_available = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, entity.getRow());
            statement.setInt(2, entity.getNumber());
            statement.setInt(3, entity.getPrice());
            statement.setBoolean(4, entity.isAvailable());
            statement.setLong(5, entity.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Seat> findByPlayId(Long playId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM Seats WHERE play_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, playId);
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
    public void updateSeatAvailability(Long seatId, boolean available) {
        String query = "UPDATE Seats SET is_available = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setBoolean(1, available);
            statement.setLong(2, seatId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}