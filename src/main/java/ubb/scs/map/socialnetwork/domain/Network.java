package ubb.scs.map.socialnetwork.domain;

import ubb.scs.map.socialnetwork.repository.FriendshipRepository;
import ubb.scs.map.socialnetwork.repository.Repository;
import ubb.scs.map.socialnetwork.repository.RepositoryException;
import ubb.scs.map.socialnetwork.utils.paging.Page;
import ubb.scs.map.socialnetwork.utils.paging.Pageable;

import java.io.OptionalDataException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * The Network class manages users and friendships, maintaining a network graph
 * to represent relationships between users. It provides functionality for
 * adding/removing users and friendships, and analyzing communities within the network.
 */
public class Network {

    private Repository<Long, User> repositoryUsers;
    private FriendshipRepository repositoryFriendships;
    private NetworkGraph networkGraph;

    public Network(Repository<Long, User> users, FriendshipRepository friendships) {
        this.repositoryUsers = users;
        this.repositoryFriendships = friendships;
        this.networkGraph = new NetworkGraph();
        networkGraph.initalizeGraph(repositoryUsers, repositoryFriendships);
    }

    /**
     * Retrieves all friendships in the network.
     *
     * @return an Iterable of Friendship objects
     */
    public Iterable<Friendship> getFriendships() {
        return repositoryFriendships.findAll();
    }

    /**
     * Adds a user to the network graph.
     *
     * @param idUsr the ID of the user to be added to the network
     */
    public void addUser(Long idUsr) {
        networkGraph.addUser(idUsr);
    }

    /**
     * Removes a user and their associated friendships from the network.
     * Deletes the user's friendships from the repository and the graph.
     *
     * @param idUsr the ID of the user to be removed
     * @throws RepositoryException if the user cannot be found
     */
    public void removeUser(Long idUsr) throws RepositoryException {
        Iterable<Friendship> friendships = repositoryFriendships.findAll();
        List<Friendship> friendshipsToRemove = new ArrayList<>();

        for (Friendship friendship : friendships) {
            if (friendship.getId().getE1().equals(idUsr) ||
                    friendship.getId().getE2().equals(idUsr)) {
                friendshipsToRemove.add(friendship);
            }
        }

        for (Friendship friendship : friendshipsToRemove) {
            repositoryFriendships.delete(friendship);
        }
        networkGraph.removeUser(idUsr);
    }

    /**
     * Adds a friendship between two users.
     * Updates both the repository and the internal graph structure.
     *
     * @param idUsr1 the ID of the first user in the friendship
     * @param idUsr2 the ID of the second user in the friendship
     * @throws RepositoryException if any user cannot be found
     */
    public void addFriendship(Long idUsr1, Long idUsr2) throws RepositoryException {
        Optional<User> user1 = repositoryUsers.findOne(idUsr1);
        if(user1.isEmpty()){
            throw new RepositoryException("User 1 was not found");
        }
        Optional<User> user2 = repositoryUsers.findOne(idUsr2);
        if(user2.isEmpty()){
            throw new RepositoryException("User 2 was not found");
        }
        Optional<Friendship> friendshipOptional = repositoryFriendships.findOne(new Tuple<Long,Long>(idUsr1, idUsr2));
        if(friendshipOptional.isEmpty()){
            Friendship friendship = new Friendship(idUsr1,idUsr2, LocalDateTime.now());
            friendshipOptional= repositoryFriendships.save(friendship);
            if(friendshipOptional.isPresent()){
                throw new RepositoryException("Friendship already exists");
            }
            networkGraph.addFriendship(idUsr1, idUsr2);
        }
        else throw new RepositoryException("Friendship already exists");

    }

    /**
     * Removes a friendship between two users.
     * Deletes the friendship from both the repository and the internal graph.
     *
     * @param idUsr1 the ID of the first user in the friendship
     * @param idUsr2 the ID of the second user in the friendship
     * @throws RepositoryException if any user or the friendship cannot be found
     */
    public void removeFriendship(Long idUsr1, Long idUsr2) throws RepositoryException {
        Optional<User> user1 = repositoryUsers.findOne(idUsr1);
        if(user1.isEmpty()){
            throw new RepositoryException("User 1 was not found");
        }
        Optional<User> user2 = repositoryUsers.findOne(idUsr2);
        if(user2.isEmpty()){
            throw new RepositoryException("User 2 was not found");
        }
        Optional<Friendship> friendshipOptional;
        Friendship friendship;
        friendshipOptional = repositoryFriendships.findOne(new Tuple<Long, Long>(idUsr1, idUsr2));
        if(friendshipOptional.isEmpty()){
                throw new RepositoryException("Friendship not found");
        }
        else friendship = friendshipOptional.get();
        repositoryFriendships.delete(friendship);
        networkGraph.removeFriendship(idUsr1, idUsr2);
    }

    /**
     * Calculates and returns the number of distinct communities (connected components)
     * in the network.
     *
     * @return the number of communities in the network
     */
    public Long communityNumber() {
        return networkGraph.communitiesNumber();
    }

    /**
     * Finds and returns the most social community (the largest connected component)
     * in the network as a collection of User entities.
     *
     * @return an Iterable of User objects representing the most social community
     */
    public Iterable<User> mostSocialCommunity() {
        Map<Long, User> mostSocialCommunity = new HashMap<>();
        List<Long> communityIDs = networkGraph.mostSocialCommunity();
        for (Long communityID : communityIDs) {
            Optional<User> usr = repositoryUsers.findOne(communityID);
            if(usr.isPresent()){
                mostSocialCommunity.put(communityID, usr.get());
            }
        }
        return mostSocialCommunity.values();
    }

    public Iterable<Friend> getUserFriends(User user) throws RepositoryException {
        Map<Long,Friend> friends = new HashMap<>();
        Iterable<Friendship> allFriendships = getFriendships();
        for(Friendship friendship : allFriendships) {
            if( friendship.getId().getE1().equals(user.getId())){
                Optional<User> optionalfriend = repositoryUsers.findOne(friendship.getId().getE2());
                if(optionalfriend.isPresent()) {
                    User user2 = optionalfriend.get();
                    friends.put(friendship.getId().getE2(),new Friend(user2.getId(),user2.getFirstName(), user2.getLastName(), user2.getEmail(),friendship.getFriendshipDate()));
                }
            }
            else if( friendship.getId().getE2().equals(user.getId())){
                Optional<User> optionalfriend = repositoryUsers.findOne(friendship.getId().getE1());
                if(optionalfriend.isPresent()) {
                    User user2 = optionalfriend.get();
                    friends.put(friendship.getId().getE1(),new Friend(user2.getId(),user2.getFirstName(), user2.getLastName(), user2.getEmail(),friendship.getFriendshipDate()));
                }
            }
        }
        return friends.values();
    }

    public Page<Friend> findAllOnPage(Pageable pageable, User user) {
        Page<Friendship> friendshipsPage = repositoryFriendships.findAllFriendsOfUserOnPage(pageable, user);
        List<Friend> friends = new ArrayList<>();
        for(Friendship friendship : friendshipsPage.getElementsOnPage()) {
            if( friendship.getId().getE1().equals(user.getId())){
                Optional<User> optionalfriend = repositoryUsers.findOne(friendship.getId().getE2());
                if(optionalfriend.isPresent()) {
                    User user2 = optionalfriend.get();
                    friends.add(new Friend(user2.getId(),user2.getFirstName(), user2.getLastName(), user2.getEmail(),friendship.getFriendshipDate()));
                }
            }
            else if( friendship.getId().getE2().equals(user.getId())){
                Optional<User> optionalfriend = repositoryUsers.findOne(friendship.getId().getE1());
                if(optionalfriend.isPresent()) {
                    User user2 = optionalfriend.get();
                    friends.add(new Friend(user2.getId(),user2.getFirstName(), user2.getLastName(), user2.getEmail(),friendship.getFriendshipDate()));
                }
            }
        }
        return new Page<Friend>(friends,friendshipsPage.getTotalNumberOfElements());
    }

}

