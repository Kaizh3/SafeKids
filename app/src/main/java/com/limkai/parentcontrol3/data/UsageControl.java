package com.limkai.parentcontrol3.data;

public class UsageControl {
    private int timeLimit;
    private boolean lockNow,sleepTime;

    public UsageControl(){

    }

    public UsageControl(int timeLimit, boolean lockNow, boolean sleepTime) {
        this.timeLimit = timeLimit;
        this.lockNow = lockNow;
        this.sleepTime = sleepTime;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean isLockNow() {
        return lockNow;
    }

    public void setLockNow(boolean lockNow) {
        this.lockNow = lockNow;
    }

    public boolean getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(boolean sleepTime) {
        this.sleepTime = sleepTime;
    }
}
