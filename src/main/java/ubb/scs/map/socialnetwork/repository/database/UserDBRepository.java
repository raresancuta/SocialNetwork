package ubb.scs.map.socialnetwork.repository.database;

import ubb.scs.map.socialnetwork.domain.User;
import ubb.scs.map.socialnetwork.repository.PagingRepository;
import ubb.scs.map.socialnetwork.repository.Repository;

import java.sql.*;
import java.util.*;

public class UserDBRepository implements Repository<Long,User> {
    private String url;
    private String username;
    private String password;

    public UserDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Iterable<User> findAll() {
        Map<Long,User> users = new HashMap<Long,User>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");

                User user = new User(firstName, lastName, email);
                user.setId(id);
                users.put(id,user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users.values();
    }

    public Optional<User> findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
        ) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long userId = resultSet.getLong("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String email = resultSet.getString("email");
                    User user = new User(firstName, lastName, email);
                    user.setId(userId);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public Optional<User> save(User user) {
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users(id,first_name,last_name,email) VALUES(?,?,?,?)");
        ) {
            preparedStatement.setLong(1, user.getId());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getLastName());
            preparedStatement.setString(4, user.getEmail());
            rez = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return Optional.of(user);
        }
        if (rez > 0)
            return Optional.empty();
        else return Optional.of(user);
    }

    public Optional<User> delete(User user) {
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE id = ?");
        ) {
            preparedStatement.setLong(1, user.getId());
            rez = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return Optional.empty();
        }
        if (rez > 0)
            return Optional.of(user);
        else return Optional.empty();
    }

    public Optional<User> update(User user) {
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET first_name = ?, last_name = ?, email= ? WHERE id = ?");
        ) {
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setLong(4, user.getId());
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            rez = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return Optional.of(user);
        }
        if (rez > 0)
            return Optional.empty();
        else return Optional.of(user);
    }

    public Optional<User> findByEmail(String email) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement preparedStatement = connection.prepareStatement("select * from users where email=?");) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String e_mail = resultSet.getString("email");
                User user = new User(firstName, lastName, e_mail);
                user.setId(id);
                return Optional.of(user);
            }
        }
        catch(SQLException e){
            return Optional.empty();
        }
        return Optional.empty();
    }

}