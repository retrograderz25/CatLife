package hust.hedspi.oop.game.managers;

import hust.hedspi.oop.game.utils.DayOfWeek;
import hust.hedspi.oop.game.utils.IObserver;
import hust.hedspi.oop.game.utils.ISubject;
import hust.hedspi.oop.game.utils.Constants;
import hust.hedspi.oop.game.utils.Phase;

import java.util.ArrayList;
import java.util.List;

public class TimeManager implements ISubject {
    private static TimeManager instance;

    private float timer;
    private int inGameHour;
    private int inGameMinute;
    private DayOfWeek currentDayOfWeek;
    private Phase currentPhase;
    
    private List<IObserver> observers;

    private TimeManager() {
        observers = new ArrayList<>();
        resetTime();
    }

    public static TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
        }
        return instance;
    }

    public void resetTime() {
        currentDayOfWeek = DayOfWeek.MONDAY;
        inGameHour = Constants.START_HOUR;
        inGameMinute = Constants.START_MINUTE;
        currentPhase = Phase.CHILDHOOD;
        timer = 0f;
        notifyObservers();
    }

    public void skipToNextMorning() {
        inGameHour = Constants.START_HOUR;
        inGameMinute = Constants.START_MINUTE;
        currentDayOfWeek = currentDayOfWeek.next();
        timer = 0f;
        notifyObservers();
    }

    public void update(float deltaTime) {
        timer += deltaTime;
        // Cập nhật mỗi 10 phút trong game (10 * 0.5s = 5s ngoài đời thực)
        if (timer >= Constants.REAL_SECONDS_PER_IN_GAME_MINUTE * 10) {
            timer -= Constants.REAL_SECONDS_PER_IN_GAME_MINUTE * 10;
            incrementTime();
        }
    }

    private void incrementTime() {
        inGameMinute += 10;
        if (inGameMinute >= 60) {
            inGameMinute = 0;
            inGameHour++;
            
            if (inGameHour >= 24) {
                inGameHour = 0;
                currentDayOfWeek = currentDayOfWeek.next();
            }
        }
        // Gọi notifyObservers() ở đây để HUD cập nhật mỗi lần nhảy 10 phút
        notifyObservers();
    }

    public int getInGameHour() { return inGameHour; }
    public int getInGameMinute() { return inGameMinute; }
    public DayOfWeek getCurrentDayOfWeek() { return currentDayOfWeek; }
    public Phase getCurrentPhase() { return currentPhase; }

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
