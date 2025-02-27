package ubb.scs.map.socialnetwork.repository.database;

import ubb.scs.map.socialnetwork.domain.Friendship;
import ubb.scs.map.socialnetwork.domain.Tuple;
import ubb.scs.map.socialnetwork.domain.User;
import ubb.scs.map.socialnetwork.repository.FriendshipRepository;
import ubb.scs.map.socialnetwork.repository.PagingRepository;
import ubb.scs.map.socialnetwork.repository.Repository;
import ubb.scs.map.socialnetwork.repository.memory.InMemoryRepository;
import ubb.scs.map.socialnetwork.utils.paging.Page;
import ubb.scs.map.socialnetwork.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipDBRepository implements FriendshipRepository {
    private String url;
    private String username;
    private String password;

    public FriendshipDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Optional<Friendship> findOne(Tuple<Long, Long> userTuple) {
        Long id_usr1 = userTuple.getE1();
        Long id_usr2 = userTuple.getE2();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("select * from friendships where (first_friend_id=? and second_friend_id=?) or (first_friend_id=? and second_friend_id=?)");)
        {
            preparedStatement.setLong(1,id_usr1);
            preparedStatement.setLong(2,id_usr2);
            preparedStatement.setLong(3,id_usr2);
            preparedStatement.setLong(4,id_usr1);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Friendship friendship = new Friendship(resultSet.getLong("first_friend_id"),resultSet.getLong("second_friend_id"),resultSet.getTimestamp("friends_from_date").toLocalDateTime());
                return Optional.of(friendship);
                }
            }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Iterable<Friendship> findAll() {
        Map<Tuple<Long, Long>, Friendship> friendships = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long firstFriendId = resultSet.getLong("first_friend_id");
                Long secondFriendId = resultSet.getLong("second_friend_id");
                LocalDateTime friendsFromDate = resultSet.getTimestamp("friends_from_date").toLocalDateTime();
                Friendship friendship = new Friendship(firstFriendId,secondFriendId,friendsFromDate);
                Tuple<Long, Long> tuple = new Tuple<Long, Long>(firstFriendId, secondFriendId);
                friendships.put(tuple, friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships.values();
    }

    public Optional<Friendship> save(Friendship friendship) {
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO friendships(first_friend_id,second_friend_id,friends_from_date) VALUES (?,?,?)");
        ) {
            preparedStatement.setLong(1, friendship.getId().getE1());
            preparedStatement.setLong(2, friendship.getId().getE2());
            preparedStatement.setTimestamp(3,Timestamp.valueOf(friendship.getFriendshipDate()));
            rez = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        else return Optional.of(friendship);
    }

    public Optional<Friendship> delete(Friendship friendship) {
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM friendships WHERE first_friend_id = ? AND second_friend_id = ?");
        ) {
            preparedStatement.setLong(1, friendship.getId().getE1());
            preparedStatement.setLong(2, friendship.getId().getE2());
            rez = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        else return Optional.of(friendship);
    }

    private int countAllFriendshipsOfUser(Connection connection,Long userId){
        try(PreparedStatement preparedStatement = connection.prepareStatement("select count(*) from friendships where first_friend_id = ? or second_friend_id =?");
        ){
            preparedStatement.setLong(1,userId);
            preparedStatement.setLong(2,userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("count");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Friendship> findAllFriendsOfUserOnPage(Connection connection,Pageable pageable,User user) {
        List<Friendship> moviesOnPage = new ArrayList<>();
        String sql = "select * from friendships where first_friend_id=? or second_friend_id=? limit ? offset ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            statement.setLong(++paramIndex,user.getId());
            statement.setLong(++paramIndex,user.getId());
            statement.setInt(++paramIndex, pageable.getPageSize());
            statement.setInt(++paramIndex, pageable.getPageSize() * pageable.getPageNumber());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long firstFriendId = resultSet.getLong("first_friend_id");
                    Long secondFriendId = resultSet.getLong("second_friend_id");
                    LocalDateTime friendsFromDate = resultSet.getTimestamp("friends_from_date").toLocalDateTime();
                    Friendship friendship = new Friendship(firstFriendId,secondFriendId,friendsFromDate);
                    moviesOnPage.add(friendship);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return moviesOnPage;
    }

    public Page<Friendship> findAllFriendsOfUserOnPage(Pageable pageable, User user) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            int totalNumberOfMovies = countAllFriendshipsOfUser(connection,user.getId());
            List<Friendship> friendsOnPage;
            if (totalNumberOfMovies > 0) {
                friendsOnPage = findAllFriendsOfUserOnPage(connection, pageable, user);
            } else {
                friendsOnPage = new ArrayList<>();
            }
            return new Page<>(friendsOnPage, totalNumberOfMovies);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<Friendship> findAllOnPage(Pageable pageable) { return findAllFriendsOfUserOnPage(pageable, null); }

    public Optional<Friendship> update(Friendship friendship) {
        return Optional.empty();
    }

}