package com.limkai.parentcontrol3.data;

public class WifiState {
    public String   s_dns1 ;
    public String   s_dns2;
    public String   s_gateway;
    public String   s_ipAddress;
    public String   s_leaseDuration;
    public String   s_netmask;
    public String   s_serverAddress;

    public WifiState(){

    }

    public WifiState(String s_dns1, String s_dns2, String s_gateway, String s_ipAddress, String s_leaseDuration, String s_netmask, String s_serverAddress) {
        this.s_dns1 = s_dns1;
        this.s_dns2 = s_dns2;
        this.s_gateway = s_gateway;
        this.s_ipAddress = s_ipAddress;
        this.s_leaseDuration = s_leaseDuration;
        this.s_netmask = s_netmask;
        this.s_serverAddress = s_serverAddress;
    }

    @Override
    public String toString() {
        return s_ipAddress + '\n' +
                s_gateway + '\n' +
                "Prefix Length: 24 " + '\n' +
                "DNS1: " + "185.228.168.168" + '\n' +
                "DNS2: " + "185.228.169.168" + '\n' ;
    }

    public String getS_dns1() {
        return s_dns1;
    }

    public void setS_dns1(String s_dns1) {
        this.s_dns1 = s_dns1;
    }

    public String getS_dns2() {
        return s_dns2;
    }

    public void setS_dns2(String s_dns2) {
        this.s_dns2 = s_dns2;
    }

    public String getS_gateway() {
        return s_gateway;
    }

    public void setS_gateway(String s_gateway) {
        this.s_gateway = s_gateway;
    }

    public String getS_ipAddress() {
        return s_ipAddress;
    }

    public void setS_ipAddress(String s_ipAddress) {
        this.s_ipAddress = s_ipAddress;
    }

    public String getS_leaseDuration() {
        return s_leaseDuration;
    }

    public void setS_leaseDuration(String s_leaseDuration) {
        this.s_leaseDuration = s_leaseDuration;
    }

    public String getS_netmask() {
        return s_netmask;
    }

    public void setS_netmask(String s_netmask) {
        this.s_netmask = s_netmask;
    }

    public String getS_serverAddress() {
        return s_serverAddress;
    }

    public void setS_serverAddress(String s_serverAddress) {
        this.s_serverAddress = s_serverAddress;
    }
}
