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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.limkai.parentcontrol3.data.ApplicationManagement;
import com.limkai.parentcontrol3.data.Relationship;
import com.limkai.parentcontrol3.data.UsageControl;
import com.limkai.parentcontrol3.databinding.FragmentApplicationManagementBinding;
import com.limkai.parentcontrol3.ui.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EventListener;

public class ApplicationManagementFragment extends Fragment {

    private FragmentApplicationManagementBinding binding;
    private Relationship selectedRelationship;
    private boolean denyInstall, denyUninstall;

    private SwitchCompat switchDenyInstall, switchDenyUninstall;
    private ApplicationManagement applicationManagement, displayApplicationManagement, firebaseApplicationManagement;

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
        binding = FragmentApplicationManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        applicationManagement = new ApplicationManagement();

        switchDenyInstall = binding.switchDenyInstallation;
        switchDenyUninstall = binding.switchDenyUninstallation;

        selectedRelationship = MainActivity.getSelectedRelationship();

        switchDenyInstall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    denyInstall = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Deny installation successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    denyInstall = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Allow installation successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        switchDenyUninstall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    denyUninstall = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Deny uninstallation successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    denyUninstall = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Allow uninstallation successfully", Toast.LENGTH_SHORT).show();
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
        switchDenyInstall = binding.switchDenyInstallation;
        switchDenyUninstall = binding.switchDenyUninstallation;

        DatabaseReference applicationManagementRef = FirebaseDatabase.getInstance().getReference("ApplicationManagement");
        if(!(selectedRelationship.getRelationshipId().equals(""))){
            DatabaseReference ref =applicationManagementRef.child(selectedRelationship.getRelationshipId());
            ref.addChildEventListener( new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                    if(snapshot.exists()){
                        firebaseApplicationManagement = (ApplicationManagement) snapshot.getValue(ApplicationManagement.class);
                        initialApplicationManagement();
                        ref.removeEventListener(this);
                    } else {
                        firebaseApplicationManagement = new ApplicationManagement();
                        firebaseApplicationManagement.setDenyInstall(false);
                        firebaseApplicationManagement.setDenyUninstall(false);
                        initialApplicationManagement();
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
                    firebaseApplicationManagement = new ApplicationManagement();
                    firebaseApplicationManagement.setDenyInstall(false);
                    firebaseApplicationManagement.setDenyUninstall(false);
                    initialApplicationManagement();
                }
            });
        } else {
            firebaseApplicationManagement = new ApplicationManagement();
            firebaseApplicationManagement.setDenyInstall(false);
            firebaseApplicationManagement.setDenyUninstall(false);
            initialApplicationManagement();
        }
    }

    public void initialApplicationManagement() {
        if(firebaseApplicationManagement != null){
            displayApplicationManagement = firebaseApplicationManagement;
            if (displayApplicationManagement != null) {
                if (displayApplicationManagement.isDenyInstall() == true) {
                    switchDenyInstall.setChecked(true);
                } else {
                    switchDenyInstall.setChecked(false);
                }
                if(displayApplicationManagement.isDenyUninstall() == true){
                    switchDenyUninstall.setChecked(true);
                } else {
                    switchDenyUninstall.setChecked(false);
                }
            }
        }
    }

    public void process(){
        updateApplicationManagement();
        if (selectedRelationship != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ApplicationManagement");
            if(applicationManagement!= null && selectedRelationship.getRelationshipId() != ""){
                DatabaseReference disableFeatureRef = ref.child(selectedRelationship.getRelationshipId());
                disableFeatureRef.removeValue();
                disableFeatureRef.push().setValue(applicationManagement);
            }else {
                Log.d("myApp", "Application Management update unsuccessfully");
            }
        }
    }

    public void updateApplicationManagement(){
        applicationManagement = new ApplicationManagement(denyInstall, denyUninstall);
    }

}
