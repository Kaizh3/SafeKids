package com.limkai.parentcontrol3.data;

public class CallSMS {
    private boolean denyCalls, denySMS;
    private String callNumber, callName, callDuration, callType;

    public CallSMS (){

    }

    public CallSMS (boolean denyCalls, boolean denySMS){
        this.denyCalls = denyCalls;
        this.denySMS = denySMS;
    }

    public CallSMS(String callNumber, String callName, String callDuration, String callType) {
        this.callNumber = callNumber;
        this.callName = callName;
        this.callDuration = callDuration;
        this.callType = callType;
    }

    public boolean isDenyCalls() {
        return denyCalls;
    }

    public void setDenyCalls(boolean denyCalls) {
        this.denyCalls = denyCalls;
    }

    public boolean isDenySMS() {
        return denySMS;
    }

    public void setDenySMS(boolean denySMS) {
        this.denySMS = denySMS;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
}
