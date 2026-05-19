package hust.hedspi.oop.game.utils;

public enum DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    public DayOfWeek next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
