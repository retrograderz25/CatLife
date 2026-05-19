package hust.hedspi.oop.game.utils;

public interface ISubject {
    void addObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyObservers(Object... args);
}
