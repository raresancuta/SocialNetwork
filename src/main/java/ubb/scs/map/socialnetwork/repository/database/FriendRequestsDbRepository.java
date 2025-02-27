package ubb.scs.map.socialnetwork.repository.database;

import ubb.scs.map.socialnetwork.domain.FriendRequest;
import ubb.scs.map.socialnetwork.domain.Tuple;
import ubb.scs.map.socialnetwork.domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestsDbRepository {
        private String url;
        private String username;
        private String password;

    public FriendRequestsDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public List<Tuple<Long,LocalDateTime>> getFriendRequests(User user) {
        List<Tuple<Long,LocalDateTime>> friendRequests = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement("select * from friend_requests where receiver_id=?");){
            preparedStatement.setLong(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                friendRequests.add(new Tuple<Long,LocalDateTime>(resultSet.getLong("sender_id"),resultSet.getTimestamp("request_from_date").toLocalDateTime()));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return friendRequests;
    }

    public void saveFriendRequest(Long senderId,Long receiverId,LocalDateTime requestFromDate) {
        int rez=-1;
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into friend_requests(sender_id,receiver_id,request_from_date) values(?,?,?)");){
            preparedStatement.setLong(1, senderId);
            preparedStatement.setLong(2, receiverId);
            preparedStatement.setTimestamp(3,Timestamp.valueOf(requestFromDate));
            rez = preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteFriendRequest(Long senderId,Long receiverId) {
        int rez=-1;
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement("delete from friend_requests where sender_id=? and receiver_id=?");){
            preparedStatement.setLong(1, senderId);
            preparedStatement.setLong(2, receiverId);
            rez = preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

}
