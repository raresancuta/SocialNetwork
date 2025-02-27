package ubb.scs.map.socialnetwork.repository.database;

import ubb.scs.map.socialnetwork.domain.Message;
import ubb.scs.map.socialnetwork.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessagesDbRepository {

    private Connection connection;

    public MessagesDbRepository(String url, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url,username,password);
    }

    public Optional<User> findUserbyId(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                User u = new User(resultSet.getString("first_name"),resultSet.getString("last_name"),resultSet.getString("email"));
                u.setId(id);
                return Optional.of(u);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Message> findAllMessagesForUser(User user) {
        String mysql = """
                SELECT m.id AS id,m.sender_id AS sender,m.content as text,m.reply_to as reply,mr.receiver_id as receiver, m.date as date from messages m
                          join message_receivers mr on m.id=mr.message_id
                          where m.sender_id=? or mr.receiver_id =?
                          order by date desc""";

        List<Message> messages = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(mysql);
            ){
            statement.setLong(1, user.getId());
            statement.setLong(2,user.getId());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                User sender = findUserbyId(resultSet.getLong("sender")).get();
                Message reply = null;
                if (resultSet.getObject("reply")!= null) {
                    reply = findMessageById(resultSet.getLong("id"));
                }
                User receiver = findUserbyId(resultSet.getLong("receiver")).get();
                Message message = new Message(resultSet.getLong("id"),sender,List.of(receiver),resultSet.getString("text"),resultSet.getTimestamp("date").toLocalDateTime(),reply);
                messages.add(message);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public Message findMessageById(Long id) throws SQLException {
        String mysql = """
                select m.id as id,m.content as text,m.date as date,m.reply_to as reply,m.sender_id as sender,mr.receiver_id as receiver
                from messages m
                join message_receivers mr on m.id=mr.message_id
                where m.id=? 
                limit 1
                """;
        try(PreparedStatement statement = connection.prepareStatement(mysql);){
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery() ;
            if(resultSet.next()){
                User sender = findUserbyId(resultSet.getLong("sender")).get();
                Message reply = null;
                //if (resultSet.getObject("reply")!= null) {
                //    reply = findMessageById(resultSet.getLong("id"));
                //}
                User receiver = findUserbyId(resultSet.getLong("receiver")).get();
                return new Message(resultSet.getLong("id"),sender,List.of(receiver),resultSet.getString("text"),resultSet.getTimestamp("date").toLocalDateTime(),reply);
            }
        }
        return null;
    }
    public void save(Message message) {
        String insertMessage = "INSERT INTO messages (sender_id, content, date, reply_to) VALUES (?, ?, ?, ?)";
        String insertReceivers = "INSERT INTO message_receivers (message_id, receiver_id) VALUES (?, ?)";

        try (PreparedStatement stmtMessage = connection.prepareStatement(insertMessage, Statement.RETURN_GENERATED_KEYS)) {
            stmtMessage.setLong(1, message.getSender().getId());
            stmtMessage.setString(2, message.getContent());
            stmtMessage.setTimestamp(3, Timestamp.valueOf(message.getDate()));
            stmtMessage.setObject(4, message.getReply() != null ? message.getReply().getId() : null);

            stmtMessage.executeUpdate();
            ResultSet generatedKeys = stmtMessage.getGeneratedKeys();
            if (generatedKeys.next()) {
                long messageId = generatedKeys.getLong(1);

                if (message.getReceivers() != null) {
                    for (User receiver : message.getReceivers()) {
                        try (PreparedStatement stmtReceivers = connection.prepareStatement(insertReceivers)) {
                            stmtReceivers.setLong(1, messageId);
                            stmtReceivers.setLong(2, receiver.getId());
                            stmtReceivers.executeUpdate();
                        }
                    }
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }


}
