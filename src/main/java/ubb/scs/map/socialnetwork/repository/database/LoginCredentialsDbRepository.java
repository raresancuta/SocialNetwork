package ubb.scs.map.socialnetwork.repository.database;


import javafx.scene.control.Alert;
import ubb.scs.map.socialnetwork.controller.MessageAlert;
import ubb.scs.map.socialnetwork.domain.LoginCredentials;

import java.sql.*;
import java.util.Optional;

public class LoginCredentialsDbRepository {
    private String url;
    private String username;
    private String password;

    public LoginCredentialsDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Optional<LoginCredentials> findOne(String login_email,String login_password) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement preparedStatement = connection.prepareStatement("select * from login_credentials where email=? and password=?");){
            preparedStatement.setString(1, login_email);
            preparedStatement.setString(2, login_password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Long id = resultSet.getLong("id");
                LoginCredentials loginCredentials = new LoginCredentials(id,login_email,login_password);
                return Optional.of(loginCredentials);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<LoginCredentials> save(LoginCredentials loginCredentials) {
        int rez = -1;
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement preparedStatement = connection.prepareStatement("insert into login_credentials(email,password,id) values(?,?,?)");){
            preparedStatement.setString(1, loginCredentials.getEmail());
            preparedStatement.setString(2, loginCredentials.getPassword());
            preparedStatement.setLong(3, loginCredentials.getId());
            rez = preparedStatement.executeUpdate();
            if(rez > 0){
                return Optional.empty();
            }
        }
        catch(SQLException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Database error", "Eroare la salvarea in baza de date");
        }
        return Optional.of(loginCredentials);
    }

    public Optional<LoginCredentials> update(LoginCredentials loginCredentials) {
        int rez = -1;
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement("update login_credentials set email=?,password=? where id=?");){
            preparedStatement.setString(1, loginCredentials.getEmail());
            preparedStatement.setString(2, loginCredentials.getPassword());
            preparedStatement.setLong(3, loginCredentials.getId());
            rez = preparedStatement.executeUpdate();
            if(rez > 0){
                return Optional.empty();
            }
        }
        catch(SQLException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Database error", "Eroare la salvarea in baza de date");
        }
        return Optional.of(loginCredentials);
    }

    public Optional<LoginCredentials> delete(LoginCredentials loginCredentials) {
        int rez = -1;
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement preparedStatement = connection.prepareStatement("delete from login_credentials where email=?");){
            preparedStatement.setString(1, loginCredentials.getEmail());
            rez = preparedStatement.executeUpdate();
            if(rez > 0){
                return Optional.of(loginCredentials);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
