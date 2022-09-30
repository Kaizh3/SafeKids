package com.limkai.parentcontrol3.ui.feature.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import com.limkai.parentcontrol3.data.CallSMS;
import com.limkai.parentcontrol3.data.Relationship;
import com.limkai.parentcontrol3.databinding.FragmentApplicationManagementBinding;
import com.limkai.parentcontrol3.databinding.FragmentCallsSmsBinding;
import com.limkai.parentcontrol3.ui.MainActivity;

import org.jetbrains.annotations.NotNull;

public class CallsSMSFragment extends Fragment {

    private FragmentCallsSmsBinding binding;
    private Relationship selectedRelationship;
    private boolean denyCall, denySMS;

    private SwitchCompat switchDenyCalls, switchDenySMS;
    private CallSMS callSMS, displayCallSMS, firebaseCallSMS;

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
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCallsSmsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        callSMS = new CallSMS();

        switchDenyCalls = binding.switchDenyCalls;
        switchDenySMS = binding.switchDenySms;

        selectedRelationship = MainActivity.getSelectedRelationship();

        switchDenyCalls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    denyCall = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Deny calls actions successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    denyCall = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Allow calls actions successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        switchDenySMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    denySMS = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Deny SMS actions successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    denySMS = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Allow SMS actions successfully", Toast.LENGTH_SHORT).show();
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
        switchDenyCalls = binding.switchDenyCalls;
        switchDenySMS = binding.switchDenySms;
        DatabaseReference callSMSRef = FirebaseDatabase.getInstance().getReference("CallSMS");
        if(!(selectedRelationship.getRelationshipId().equals(""))){
            DatabaseReference ref = callSMSRef.child(selectedRelationship.getRelationshipId());
            ref.addChildEventListener( new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                    if(snapshot.exists()){
                        firebaseCallSMS = (CallSMS) snapshot.getValue(CallSMS.class);
                        initialCallSMS();
                        ref.removeEventListener(this);
                    } else {
                        firebaseCallSMS = new CallSMS();
                        firebaseCallSMS.setDenyCalls(false);
                        firebaseCallSMS.setDenySMS(false);
                        initialCallSMS();
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
                    firebaseCallSMS = new CallSMS();
                    firebaseCallSMS.setDenyCalls(false);
                    firebaseCallSMS.setDenySMS(false);
                    initialCallSMS();
                }
            });


        } else {
            firebaseCallSMS = new CallSMS();
            firebaseCallSMS.setDenyCalls(false);
            firebaseCallSMS.setDenySMS(false);
            initialCallSMS();
        }

    }

    public void initialCallSMS() {
        if (firebaseCallSMS != null) {
            displayCallSMS = firebaseCallSMS;
            if (displayCallSMS != null) {
                if (displayCallSMS.isDenyCalls() == true) {
                    switchDenyCalls.setChecked(true);
                } else {
                    switchDenyCalls.setChecked(false);
                }
                if(displayCallSMS.isDenySMS() == true){
                    switchDenySMS.setChecked(true);
                } else {
                    switchDenySMS.setChecked(false);
                }
            }
        }
    }

    public void process(){
        updateCallSMS();
        if (selectedRelationship != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CallSMS");
            if(callSMS != null && selectedRelationship.getRelationshipId() != ""){
                DatabaseReference disableFeatureRef = ref.child(selectedRelationship.getRelationshipId());
                disableFeatureRef.removeValue();
                disableFeatureRef.push().setValue(callSMS);
            }else {
                Log.d("myApp", "Call SMS update successfully");
            }
        }
    }

    public void updateCallSMS(){
        callSMS = new CallSMS(denyCall, denySMS);
    }
}
