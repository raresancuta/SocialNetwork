package ubb.scs.map.socialnetwork.domain;

import ubb.scs.map.socialnetwork.repository.Repository;

import java.util.*;

/**
 * The {@code NetworkGraph} class represents a social network as a graph using an adjacency list.
 * Each user is a node, and each friendship between users is an edge.
 * This class provides operations for managing users, friendships, and analyzing communities in the network.
 */
public class NetworkGraph {
    private Map<Long, Set<Long>> adjacencyList;

    public NetworkGraph() {
        adjacencyList = new HashMap<Long, Set<Long>>();
    }

    /**
     * Initializes the graph with users and friendships from the given repositories.
     * It adds all users as nodes and friendships as edges in the graph.
     *
     * @param users the repository containing the users
     * @param friendships the repository containing the friendships
     */
    public void initalizeGraph(Repository<Long, User> users, Repository<Tuple<Long, Long>, Friendship> friendships) {
        Iterable<User> userIterable = users.findAll();
        userIterable.forEach(user->adjacencyList.put(user.getId(),new HashSet<Long>()));
        Iterable<Friendship> friendshipIterable = friendships.findAll();

        friendshipIterable.forEach(friendship->addFriendship(friendship.getId().getE1(), friendship.getId().getE2()));
    }

    /**
     * Adds a user to the graph.
     * If the user already exists, no changes are made.
     *
     * @param idUsr the ID of the user to be added
     */
    public void addUser(Long idUsr) {
        adjacencyList.putIfAbsent(idUsr, new HashSet<>());
    }

    /**
     * Removes a user from the graph.
     * It also removes the user from the friend lists of all their friends.
     *
     * @param idUsr the ID of the user to be removed
     */
    public void removeUser(Long idUsr) {
        if (!adjacencyList.containsKey(idUsr))
            return;

//        for (Long friendId : adjacencyList.get(idUsr)) {
//            adjacencyList.get(friendId).remove(idUsr);
//        }
        adjacencyList.get(idUsr).forEach(friendId->adjacencyList.get(friendId).remove(idUsr));
        adjacencyList.remove(idUsr);
    }

    /**
     * Adds a friendship (edge) between two users in the graph.
     *
     * @param idUsr1 the ID of the first user
     * @param idUsr2 the ID of the second user
     */
    public void addFriendship(Long idUsr1, Long idUsr2) {
        adjacencyList.get(idUsr1).add(idUsr2);
        adjacencyList.get(idUsr2).add(idUsr1);
    }

    /**
     * Removes a friendship (edge) between two users in the graph.
     *
     * @param idUsr1 the ID of the first user
     * @param idUsr2 the ID of the second user
     */
    public void removeFriendship(Long idUsr1, Long idUsr2) {
        adjacencyList.get(idUsr1).remove(idUsr2);
        adjacencyList.get(idUsr2).remove(idUsr1);
    }

    /**
     * Finds the most social community, defined as the largest connected component of users.
     *
     * @return a list of user IDs representing the most social community
     */
    public List<Long> mostSocialCommunity() {
        Set<Long> visited = new HashSet<>();
        List<Long> largestCommunity = new ArrayList<>();

        for (Long idUsr : adjacencyList.keySet()) {
            if (!visited.contains(idUsr)) {
                List<Long> currentCommunity = bfsCollectCommunity(idUsr, visited);
                if (currentCommunity.size() > largestCommunity.size()) {
                    largestCommunity = currentCommunity;
                }
            }
        }


        return largestCommunity;
    }

    /**
     * Counts the number of communities (connected components) in the graph.
     *
     * @return the number of communities
     */
    public Long communitiesNumber() {
        Set<Long> visited = new HashSet<>();
        Long communityNumber = 0L;

        for (Long idUsr : adjacencyList.keySet()) {
            if (!visited.contains(idUsr)) {
                List<Long> currentCommunity = bfsCollectCommunity(idUsr, visited);
                if (currentCommunity.size() > 0) {
                    communityNumber++;
                }
            }
        }
        return communityNumber;
    }

    /**
     * Uses Breadth-First Search (BFS) to collect all users in the same community (connected component)
     * starting from a given user.
     *
     * @param idUsr the starting user ID
     * @param visited the set of users that have already been visited
     * @return a list of user IDs representing the community
     */
    private List<Long> bfsCollectCommunity(Long idUsr, Set<Long> visited) {
        List<Long> community = new ArrayList<>();
        Queue<Long> queue = new LinkedList<>();

        queue.add(idUsr);
        visited.add(idUsr);
        community.add(idUsr);

        while (!queue.isEmpty()) {
            Long userId = queue.poll();

            for (Long friendId : adjacencyList.get(userId)) {
                if (!visited.contains(friendId)) {
                    queue.add(friendId);
                    visited.add(friendId);
                    community.add(friendId);
                }
            }
        }
        return community;
    }
}
