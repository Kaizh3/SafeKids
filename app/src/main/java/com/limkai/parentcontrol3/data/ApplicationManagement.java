package com.limkai.parentcontrol3.data;

public class ApplicationManagement {
    private boolean denyInstall, denyUninstall;

    public ApplicationManagement(){

    }

    public ApplicationManagement(boolean denyInstall, boolean denyUninstall) {
        this.denyInstall = denyInstall;
        this.denyUninstall = denyUninstall;
    }

    public boolean isDenyInstall() {
        return denyInstall;
    }

    public void setDenyInstall(boolean denyInstall) {
        this.denyInstall = denyInstall;
    }

    public boolean isDenyUninstall() {
        return denyUninstall;
    }

    public void setDenyUninstall(boolean denyUninstall) {
        this.denyUninstall = denyUninstall;
    }
}
