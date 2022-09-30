package com.limkai.parentcontrol3.ui.feature.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.limkai.parentcontrol3.data.ApplicationManagement;
import com.limkai.parentcontrol3.data.Relationship;
import com.limkai.parentcontrol3.data.UsageControl;
import com.limkai.parentcontrol3.databinding.FragmentUsageControlBinding;
import com.limkai.parentcontrol3.ui.MainActivity;

import org.jetbrains.annotations.NotNull;

public class UsageControlFragment extends Fragment {
    private FragmentUsageControlBinding binding;
    private Relationship selectedRelationship;
    private int timeLimitInMinutes, timeLimit;
    private boolean lockNow, sleepTime;

    private SeekBar seekBar;
    private TextView txtTime;
    private Button btnApplyTimeLimit, btnLockNow;
    private SwitchCompat switchTimeToLock;
    private UsageControl usageControl, displayUsageControl, firebaseUsageControl;

    Handler handler = new Handler();
    int delay = 5*1000; //Delay for 5 seconds.  One second = 1000 milliseconds.

    private Runnable runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {
            //do something
            selectedRelationship = MainActivity.getSelectedRelationship();
            process();
            while (handler.hasCallbacks(runnable) == true){
                handler.removeCallbacks(runnable);
            }
            handler =  new Handler();
            handler.postDelayed(this, delay);
        }
    };
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsageControlBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        usageControl = new UsageControl();

        seekBar = binding.seekbarTime;
        txtTime = binding.txtTime;
        btnApplyTimeLimit = binding.btnApplyTimeLimit;
        btnLockNow = binding.btnApplyLockNow;
        switchTimeToLock = binding.switchTimeToLock;

        selectedRelationship = MainActivity.getSelectedRelationship();

        seekBar.setMax(144);
        seekBar.setProgress(1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int hours = progress / 6;
                int tenMinutes = progress - ( hours * 6 ) ;
                timeLimitInMinutes = progress * 10 ;
                txtTime.setText(Integer.toString(hours) + " Hours " + Integer.toString(tenMinutes) + "0 Minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnApplyTimeLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeLimitInMinutes != 0){
                    timeLimit = timeLimitInMinutes;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Time Limit applied successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Time Limit applied unsuccessfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnLockNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockNow = true;
                if(MainActivity.getContextOfApplication() != null){
                    Toast.makeText(MainActivity.getContextOfApplication(), "Lock Now applied successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchTimeToLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sleepTime = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Sleep time applied successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    sleepTime = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Sleep time removed successfully", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        selectedRelationship = MainActivity.getSelectedRelationship();
        initData();
        handler = new Handler();
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        while (handler.hasCallbacks(runnable) == true){
            handler.removeCallbacks(runnable);
        }
        binding = null;
    }

    private void initData(){
        btnApplyTimeLimit = binding.btnApplyTimeLimit;
        btnLockNow = binding.btnApplyLockNow;
        switchTimeToLock = binding.switchTimeToLock;

        DatabaseReference usageControlRef = FirebaseDatabase.getInstance().getReference("UsageControl");
        if(!(selectedRelationship.getRelationshipId().equals(""))){
            DatabaseReference ref = usageControlRef.child(selectedRelationship.getRelationshipId());
            ref.addChildEventListener( new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                    if(snapshot.exists()){
                        firebaseUsageControl = (UsageControl) snapshot.getValue(UsageControl.class);
                        initialUsageControl();
                        ref.removeEventListener(this);
                    } else {
                        firebaseUsageControl = new UsageControl();
                        firebaseUsageControl.setSleepTime(false);
                        firebaseUsageControl.setLockNow(false);
                        firebaseUsageControl.setTimeLimit(0);
                        initialUsageControl();
                    }
                }

                @Override
                public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    firebaseUsageControl = new UsageControl();
                    firebaseUsageControl.setSleepTime(false);
                    firebaseUsageControl.setLockNow(false);
                    firebaseUsageControl.setTimeLimit(0);
                    initialUsageControl();
                }
            });


        } else {
            firebaseUsageControl = new UsageControl();
            firebaseUsageControl.setSleepTime(false);
            firebaseUsageControl.setLockNow(false);
            firebaseUsageControl.setTimeLimit(0);
            initialUsageControl();
        }
    }

    public void initialUsageControl() {
        if(firebaseUsageControl != null){
            displayUsageControl = firebaseUsageControl;
            if (displayUsageControl != null) {
                if (displayUsageControl.getSleepTime() == true) {
                    switchTimeToLock.setChecked(true);
                } else {
                    switchTimeToLock.setChecked(false);
                }
                if(displayUsageControl.getTimeLimit() > 0){
                    timeLimitInMinutes =  displayUsageControl.getTimeLimit();
                    timeLimit = timeLimitInMinutes;
                    int progress = timeLimitInMinutes /10 ;
                    seekBar.setProgress(progress);
                }
                if(displayUsageControl.isLockNow() == true){
                    lockNow = true;
                } else {
                    lockNow = false;
                }
            }
        }
    }

    public void process(){
        updateUsageControl();
        if (selectedRelationship != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UsageControl");
            if(usageControl!= null && selectedRelationship.getRelationshipId() != ""){
                DatabaseReference deviceRestrictionRef = ref.child(selectedRelationship.getRelationshipId());
                deviceRestrictionRef.removeValue();
                deviceRestrictionRef.push().setValue(usageControl);
            }else {
                Log.d("myApp", "Wifi State update unsuccessfully");
            }
        }
    }

    public void updateUsageControl(){
        usageControl = new UsageControl(timeLimit, lockNow, sleepTime );
    }

}
