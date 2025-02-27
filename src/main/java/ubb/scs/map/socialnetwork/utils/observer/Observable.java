package ubb.scs.map.socialnetwork.utils.observer;

import ubb.scs.map.socialnetwork.utils.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> observer);
    void removeObserver(Observer<E> observer);
    void notifyObservers(E t);
}
