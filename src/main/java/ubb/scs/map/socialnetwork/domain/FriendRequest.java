package ubb.scs.map.socialnetwork.domain;

import java.time.LocalDateTime;

public class FriendRequest extends User{
    private LocalDateTime requestFrom;

    public FriendRequest(User user, LocalDateTime requestFrom) {
        super(user);
        this.requestFrom = requestFrom;
    }

    public LocalDateTime getRequestFrom() {
        return requestFrom;
    }
}
