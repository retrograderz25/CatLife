package hust.hedspi.oop.game.utils;

import hust.hedspi.oop.game.entities.Cat;

public abstract class EndingCondition {
    protected int priority;
    
    public EndingCondition(int priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public abstract boolean isSatisfied(Cat player);
}