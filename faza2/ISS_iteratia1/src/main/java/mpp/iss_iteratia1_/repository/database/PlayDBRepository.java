package mpp.iss_iteratia1_.repository.database;

import mpp.iss_iteratia1_.domain.Play;
import mpp.iss_iteratia1_.domain.Seat;
import mpp.iss_iteratia1_.repository.PlayRepository;
import mpp.iss_iteratia1_.repository.SeatRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PlayDBRepository implements PlayRepository {
    private String url;
    private String username;
    private String password;
    private SeatRepository seatRepository;

    public PlayDBRepository(String url, String username, String password, SeatRepository seatRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.seatRepository = seatRepository;
    }

    @Override
    public Optional<Play> findOne(Long id) {
        String query = "SELECT * FROM Plays WHERE id = ?";
        Play play = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String playName = resultSet.getString("play_name");
                String playDate = resultSet.getString("play_date");
                LocalDateTime date = LocalDateTime.parse(playDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                List<Seat> seats = (List<Seat>) seatRepository.findByPlayId(id);
                play = new Play(date, playName, seats);
                play.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(play);
    }

    @Override
    public Iterable<Play> findAll() {
        List<Play> plays = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Plays");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String playName = resultSet.getString("play_name");
                String playDate = resultSet.getString("play_date");
                LocalDateTime date = LocalDateTime.parse(playDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                List<Seat> seats = (List<Seat>) seatRepository.findByPlayId(id);
                Play play = new Play(date, playName, seats);
                play.setId(id);
                plays.add(play);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return plays;
    }

    @Override
    public Optional<Play> save(Play entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Play can't be null!");
        }

        String query = "INSERT INTO Plays(play_name, play_date) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getPlayName());
            statement.setString(2, entity.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

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
    public Optional<Play> delete(Long id) {
        Optional<Play> playToDelete = findOne(id);
        String query = "DELETE FROM Plays WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playToDelete;
    }

    @Override
    public Optional<Play> update(Play entity) {
        String query = "UPDATE Plays SET play_name = ?, play_date = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, entity.getPlayName());
            statement.setString(2, entity.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            statement.setLong(3, entity.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}