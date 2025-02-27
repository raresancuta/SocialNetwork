package ubb.scs.map.socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.LocalDateTime;

/**
 * The Friendship class represents a friendship relationship between two users.
 * It extends the Entity class and contains information about the users involved
 * in the friendship as well as the date when the friendship was created.
 */
public class Friendship extends Entity<Tuple<Long, Long>> {

    private LocalDateTime friendsFrom;

    public Friendship(Long idUser1, Long idUser2, LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
        this.setId(new Tuple<Long, Long>(idUser1, idUser2));
    }

    public LocalDateTime getFriendshipDate() {
        return friendsFrom;
    }
}

