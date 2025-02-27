package ubb.scs.map.socialnetwork.domain;

import java.time.LocalDateTime;

public class Friend extends User{
    private LocalDateTime friendsFrom;

    Friend(Long id, String firstName, String lastName, String email, LocalDateTime friendsFrom) {
        super(firstName, lastName, email);
        super.setId(id);
        this.friendsFrom = friendsFrom;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

}
