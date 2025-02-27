package ubb.scs.map.socialnetwork.utils.events;

import ubb.scs.map.socialnetwork.domain.Friendship;
import ubb.scs.map.socialnetwork.domain.User;

public class FriendshipEntityChangeEvent implements Event {
    private ChangeEventType type;
    public FriendshipEntityChangeEvent(ChangeEventType type) {
        this.type = type;
    }
    public ChangeEventType getType() {
        return type;
    }

}