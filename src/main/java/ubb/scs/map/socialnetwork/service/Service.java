package ubb.scs.map.socialnetwork.service;

import ubb.scs.map.socialnetwork.domain.*;
import ubb.scs.map.socialnetwork.domain.validators.UserValidator;
import ubb.scs.map.socialnetwork.repository.Repository;
import ubb.scs.map.socialnetwork.repository.RepositoryException;
import ubb.scs.map.socialnetwork.repository.database.FriendRequestsDbRepository;
import ubb.scs.map.socialnetwork.repository.database.LoginCredentialsDbRepository;
import ubb.scs.map.socialnetwork.repository.database.MessagesDbRepository;
import ubb.scs.map.socialnetwork.utils.events.ChangeEventType;
import ubb.scs.map.socialnetwork.utils.events.FriendshipEntityChangeEvent;
import ubb.scs.map.socialnetwork.utils.events.UserEntityChangeEvent;
import ubb.scs.map.socialnetwork.utils.observer.Observable;
import ubb.scs.map.socialnetwork.utils.observer.Observer;
import ubb.scs.map.socialnetwork.utils.paging.Page;
import ubb.scs.map.socialnetwork.utils.paging.Pageable;

import java.time.LocalDateTime;
import java.util.*;

/**
 * The {@code Service} class provides operations for managing users and friendships
 * in a social network. It acts as an intermediary between the user interface (UI)
 * and the underlying repositories and network logic.
 */
public class Service implements Observable<UserEntityChangeEvent> {
    private Repository<Long, User> repositoryUsers;
    private List<Observer<UserEntityChangeEvent>> observers = new ArrayList<>();
    private Network network;
    private UserValidator validator;
    private LoginCredentialsDbRepository loginCredentialsRepo;
    private FriendRequestsDbRepository friendRequestsrepo;
    private MessagesDbRepository messagesRepo;

    public Service(Repository<Long, User> repositoryUsers, Network network, UserValidator validator, LoginCredentialsDbRepository loginCredentialsRepo,FriendRequestsDbRepository friendRequestsrepo,MessagesDbRepository messagesRepo) {
        this.repositoryUsers = repositoryUsers;
        this.validator = validator;
        this.network = network;
        this.loginCredentialsRepo = loginCredentialsRepo;
        this.friendRequestsrepo = friendRequestsrepo;
        this.messagesRepo = messagesRepo;
    }

    /**
     * Retrieves all users in the system.
     *
     * @return an iterable collection of users
     */
    public Iterable<User> getUsers() {
        return repositoryUsers.findAll();
    }

    /**
     * Finds and validates a user by their ID.
     *
     * @param user the user to find and validate
     * @throws RepositoryException if the user is not found in the repository
     */
    public Optional<User> findUser(User user) throws RepositoryException {
        validator.validate(user);
        Optional<User> u = repositoryUsers.findOne(user.getId());
        if(!u.isPresent()) {
            throw new RepositoryException("User not found");
        }
        return u;
    }

    /**
     * Adds a new user to the system after validating the user.
     *
     * @param user the user to add
     * @throws RepositoryException if the user already exists or other repository issues occur
     */
    public void addUser(User user) throws RepositoryException {
        validator.validate(user);
        Optional<User> u = repositoryUsers.save(user);
        if(u.isPresent()) {
            throw new RepositoryException("User already exists");
        }
        network.addUser(user.getId());
        notifyObservers(new UserEntityChangeEvent(ChangeEventType.ADD));
    }

    /**
     * Removes an existing user from the system by their ID.
     *
     * @param user entity to be removed
     * @throws RepositoryException if the user is not found
     */
    public void removeUser(User user) throws RepositoryException {
        Optional<User> u = repositoryUsers.findOne(user.getId());
        if(u.isEmpty()) {
            throw new RepositoryException("User not found");
        }
        repositoryUsers.delete(user);
        network.removeUser(user.getId());
        notifyObservers(new UserEntityChangeEvent(ChangeEventType.DELETE));
    }

    /**
     * Updates a user's information after validating the new data.
     *
     * @param user the user to update
     * @throws RepositoryException if the user is not found or validation fails
     */
    public void updateUser(User user) throws RepositoryException {
        validator.validate(user);
        Optional<User> u = repositoryUsers.findOne(user.getId());
        if(u.isEmpty()) {
            throw new RepositoryException("User not found");
        }
        Optional<User> u2 = repositoryUsers.update(user);
        if(u2.isPresent()) {
            throw new RepositoryException("User already exists");
        }
        notifyObservers(new UserEntityChangeEvent(ChangeEventType.UPDATE));
    }


    /**
     * Retrieves all friendships in the social network.
     *
     * @return an iterable collection of friendships
     * @throws RepositoryException if there is an issue accessing the network's friendships
     */
    public Iterable<Friendship> getFriendships() throws RepositoryException {
        return network.getFriendships();
    }

    public Iterable<Friend> getUserFriends(User user) throws RepositoryException{
        return network.getUserFriends(user);
    }

    /**
     * Adds a friendship between two users by their IDs.
     *
     * @param id1 the ID of the first user
     * @param id2 the ID of the second user
     * @throws RepositoryException if adding the friendship fails
     */
    public void addFriendship(Long id1, Long id2) throws RepositoryException {
        network.addFriendship(id1, id2);
        notifyObservers(new UserEntityChangeEvent(ChangeEventType.ADD));
    }

    /**
     * Removes a friendship between two users by their IDs.
     *
     * @param id1 the ID of the first user
     * @param id2 the ID of the second user
     * @throws RepositoryException if removing the friendship fails
     */
    public void removeFriendship(Long id1, Long id2) throws RepositoryException {
        network.removeFriendship(id1, id2);
        notifyObservers(new UserEntityChangeEvent(ChangeEventType.DELETE));
    }

    /**
     * Returns the number of communities in the social network.
     * A community is defined as a connected component of users.
     *
     * @return the number of communities
     */
    public Long communitiesNumber() {
        return network.communityNumber();
    }

    /**
     * Returns the most social community, defined as the community with
     * the longest path or the most connections.
     *
     * @return an iterable collection of users in the most social community
     */
    public Iterable<User> mostSocialCommunity() {
        return network.mostSocialCommunity();
    }

    public Optional<User> verifyLoginCredentials(String email,String password) {
        Optional<LoginCredentials> loginCredentialsOptional = loginCredentialsRepo.findOne(email,password);
        if(loginCredentialsOptional.isPresent()){
            return repositoryUsers.findOne(loginCredentialsOptional.get().getId());
        }
        else return Optional.empty();

    }

    public void saveLoginCredetials(LoginCredentials loginCredentials) throws RepositoryException {
        if(loginCredentialsRepo.save(loginCredentials).isPresent()){
            throw new RepositoryException("Login credentials already exists");
        }
    }

    public void updateLoginCredentials(LoginCredentials loginCredentials){
        loginCredentialsRepo.update(loginCredentials);
    }

    public boolean verifyNewFriendRequests(User user){
        List<Tuple<Long,LocalDateTime>> friendRequests = friendRequestsrepo.getFriendRequests(user);
        if(friendRequests.isEmpty()){
            return false;
        }
        return true;
    }

    public List<FriendRequest> getUserFriendRequests(User user){
        List<Tuple<Long,LocalDateTime>> friendRequests = friendRequestsrepo.getFriendRequests(user);
        List<FriendRequest> friendRequestList = new ArrayList<>();
        for(Tuple<Long,LocalDateTime> friendRequest : friendRequests){
            Optional<User> userOptional = repositoryUsers.findOne(friendRequest.getE1());
            if(userOptional.isPresent()){
                friendRequestList.add(new FriendRequest(userOptional.get(), friendRequest.getE2()));
            }
        }
        return friendRequestList;
    }

    public void saveFriendRequest(Long senderId, Long receiverId,LocalDateTime requestFrom){
        friendRequestsrepo.saveFriendRequest(senderId, receiverId, requestFrom);
    }

    public void deleteFriendRequest(Long senderId,Long receiverId){
        friendRequestsrepo.deleteFriendRequest(senderId,receiverId);
    }

    public void addObserver(Observer<UserEntityChangeEvent> observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer<UserEntityChangeEvent> observer) {
        observers.remove(observer);
    }

    public void notifyObservers(UserEntityChangeEvent event) {
        observers.forEach(observer -> observer.update(event));
    }

    public List<Message> findAllMessagesForUser(User user){
        return messagesRepo.findAllMessagesForUser(user);
    }

    public void saveMessage(Message message){
        messagesRepo.save(message);
        notifyObservers(new UserEntityChangeEvent(ChangeEventType.ADD));
    }

    public Page<Friend> findAllOnPage(Pageable pageable,User user){
        return network.findAllOnPage(pageable,user);
    }

}

