package com.limkai.parentcontrol3.data;

public class DeviceRestriction {

    private Boolean disableCamera, disableScreenShot, disableGPS, disableNetwork, disableNetworkShare, disableAirPlane, disableBluetooth, disableWifi;

    public DeviceRestriction(){

    }

    public DeviceRestriction(Boolean disableCamera, Boolean disableScreenShot, Boolean disableGPS, Boolean disableNetwork, Boolean disableNetworkShare, Boolean disableAirPlane, Boolean disableBluetooth, Boolean disableWifi) {
        this.disableCamera = disableCamera;
        this.disableScreenShot = disableScreenShot;
        this.disableGPS = disableGPS;
        this.disableNetwork = disableNetwork;
        this.disableNetworkShare = disableNetworkShare;
        this.disableAirPlane = disableAirPlane;
        this.disableBluetooth = disableBluetooth;
        this.disableWifi = disableWifi;
    }

    public Boolean getDisableCamera() {
        return disableCamera;
    }

    public void setDisableCamera(Boolean disableCamera) {
        this.disableCamera = disableCamera;
    }

    public Boolean getDisableScreenShot() {
        return disableScreenShot;
    }

    public void setDisableScreenShot(Boolean disableScreenShot) {
        this.disableScreenShot = disableScreenShot;
    }

    public Boolean getDisableGPS() {
        return disableGPS;
    }

    public void setDisableGPS(Boolean disableGPS) {
        this.disableGPS = disableGPS;
    }

    public Boolean getDisableNetwork() {
        return disableNetwork;
    }

    public void setDisableNetwork(Boolean disableNetwork) {
        this.disableNetwork = disableNetwork;
    }

    public Boolean getDisableNetworkShare() {
        return disableNetworkShare;
    }

    public void setDisableNetworkShare(Boolean disableNetworkShare) {
        this.disableNetworkShare = disableNetworkShare;
    }

    public Boolean getDisableAirPlane() {
        return disableAirPlane;
    }

    public void setDisableAirPlane(Boolean disableAirPlane) {
        this.disableAirPlane = disableAirPlane;
    }

    public Boolean getDisableBluetooth() {
        return disableBluetooth;
    }

    public void setDisableBluetooth(Boolean disableBluetooth) {
        this.disableBluetooth = disableBluetooth;
    }

    public Boolean getDisableWifi() {
        return disableWifi;
    }

    public void setDisableWifi(Boolean disableWifi) {
        this.disableWifi = disableWifi;
    }

}
