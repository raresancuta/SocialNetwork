package ubb.scs.map.socialnetwork.utils.observer;

import ubb.scs.map.socialnetwork.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
