package ubb.scs.map.socialnetwork.utils.events;

import ubb.scs.map.socialnetwork.domain.User;

public class UserEntityChangeEvent implements Event {
    private ChangeEventType type;
    public UserEntityChangeEvent(ChangeEventType type) {
        this.type = type;
    }

    public ChangeEventType getType() {
        return type;
    }
}
