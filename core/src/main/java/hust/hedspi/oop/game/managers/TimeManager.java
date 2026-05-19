package hust.hedspi.oop.game.managers;

import hust.hedspi.oop.game.utils.DayOfWeek;
import hust.hedspi.oop.game.utils.IObserver;
import hust.hedspi.oop.game.utils.ISubject;
import hust.hedspi.oop.game.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class TimeManager implements ISubject {
    private static TimeManager instance;

    private int day;
    private DayOfWeek dayOfWeek;
    private int hour;
    private int minute;

    private float timer;
    private List<IObserver> observers;

    private TimeManager() {
        observers = new ArrayList<>();
        reset();
    }

    public static TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
        }
        return instance;
    }

    public void reset() {
        day = 1;
        dayOfWeek = DayOfWeek.MONDAY;
        hour = Constants.START_HOUR;
        minute = Constants.START_MINUTE;
        timer = 0f;
        notifyObservers();
    }

    public void update(float dt) {
        timer += dt;
        if (timer >= Constants.REAL_SECONDS_PER_IN_GAME_MINUTE) {
            timer -= Constants.REAL_SECONDS_PER_IN_GAME_MINUTE;
            incrementTime();
        }
    }

    private void incrementTime() {
        minute++;
        if (minute >= 60) {
            minute = 0;
            hour++;
            notifyObservers(); // Notify when hour changes (for UI or Map color)
            
            if (hour >= 24) {
                hour = 0;
                day++;
                dayOfWeek = dayOfWeek.next();
            }
        }
    }

    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public int getDay() { return day; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }

    @Override
    public void addObserver(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object... args) {
        for (IObserver observer : observers) {
            observer.onNotify(this);
        }
    }
}
