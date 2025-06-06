package mpp.iss_iteratia1_.repository.database;

import mpp.iss_iteratia1_.domain.User;
import mpp.iss_iteratia1_.domain.validators.UserValidator;
import mpp.iss_iteratia1_.repository.UserRepository;

import java.sql.*;
import java.util.*;

public class UserDBRepository implements UserRepository {
    private String url;
    private String username;
    private String password;
    private UserValidator userValidator;

    public UserDBRepository(String url, String username, String password, UserValidator userValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userValidator = userValidator;
    }

    @Override
    public Optional<User> findOne(Long id) {
        String query = "SELECT * FROM Users WHERE id = ?";
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                boolean role = resultSet.getBoolean("is_admin");
                user = new User(username, password, role);
                user.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                boolean role = resultSet.getBoolean("is_admin");
                User user = new User(username, password, role);
                user.setId(id);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public Optional<User> save(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("User can't be null!");
        }
        userValidator.validate(entity);

        String query = "INSERT INTO Users(username, password, is_admin) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.setBoolean(3, entity.isRole());

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
    public Optional<User> delete(Long id) {
        Optional<User> userToDelete = findOne(id);
        String query = "DELETE FROM Users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userToDelete;
    }

    @Override
    public Optional<User> update(User entity) {
        String query = "UPDATE Users SET username = ?, password = ?, is_admin = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.setBoolean(3, entity.isRole());
            statement.setLong(4, entity.getId());

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
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                boolean role = resultSet.getBoolean("is_admin");
                user = new User(username, password, role);
                user.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(user);
    }
}