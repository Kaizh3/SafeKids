package com.limkai.parentcontrol3.data;

public class Emergency {

    private boolean emergencyClicked;

    public Emergency(){

    }

    public Emergency(boolean emergencyClicked) {
        this.emergencyClicked = emergencyClicked;
    }

    public boolean isEmergencyClicked() {
        return emergencyClicked;
    }

    public void setEmergencyClicked(boolean emergencyClicked) {
        this.emergencyClicked = emergencyClicked;
    }
}
