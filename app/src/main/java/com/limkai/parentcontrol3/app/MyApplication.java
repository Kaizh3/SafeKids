package com.limkai.parentcontrol3.app;

import android.app.Application;
import android.content.Intent;

import com.limkai.parentcontrol3.AppConst;
import com.limkai.parentcontrol3.data.DataManager;
import com.limkai.parentcontrol3.util.CrashHandler;
import com.limkai.parentcontrol3.util.PreferenceManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.init(this);
        //getApplicationContext().startService(new Intent(getApplicationContext(), AppService.class));
        //DbIgnoreExecutor.init(getApplicationContext());
        //DbHistoryExecutor.init(getApplicationContext());
        DataManager.init();
        //addDefaultIgnoreAppsToDB();
        if (AppConst.CRASH_TO_FILE) CrashHandler.getInstance().init();
    }

}
