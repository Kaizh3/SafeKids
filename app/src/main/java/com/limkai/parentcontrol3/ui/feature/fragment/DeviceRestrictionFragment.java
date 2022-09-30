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

import org.jetbrains.annotations.NotNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.limkai.parentcontrol3.R;
import com.limkai.parentcontrol3.data.ApplicationManagement;
import com.limkai.parentcontrol3.data.DeviceRestriction;
import com.limkai.parentcontrol3.data.Relationship;
import com.limkai.parentcontrol3.databinding.FragmentDeviceRestrictionBinding;
import com.limkai.parentcontrol3.ui.MainActivity;

public class DeviceRestrictionFragment extends Fragment implements  CompoundButton.OnCheckedChangeListener{

    private FragmentDeviceRestrictionBinding binding;
    private Relationship selectedRelationship;
    private Boolean disableCamera, disableScreenShot, disableGPS, disableNetwork,
            disableNetworkShare, disableAirPlane, disableBluetooth, disableWifi;

    private SwitchCompat switchDisableCamera, switchDisableScreenshot, switchDisableGPS,switchDisableNetwork,
            switchDisableNetworkShare, switchAirPlane, switchDisableBluetooth, switchDisableWifi;
    private DeviceRestriction deviceRestriction, displayDeviceRestriction, firebaseDeviceRestriction;

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
        binding = FragmentDeviceRestrictionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        deviceRestriction = new DeviceRestriction();

        switchDisableCamera = binding.switchDisableCamera;
        switchDisableScreenshot = binding.switchDisableScreenshot;
        switchDisableGPS = binding.switchDisableGps;
        switchDisableNetwork = binding.switchDisableNetwork;
        switchDisableNetworkShare = binding.switchDisableNetworkShare;
        switchAirPlane = binding.switchAirplaneMode;
        switchDisableBluetooth = binding.switchBluetooth;
        switchDisableWifi = binding.switchWifi;

        selectedRelationship = MainActivity.getSelectedRelationship();

        initEvent();
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

    private void initEvent() {
        switchDisableCamera.setOnCheckedChangeListener(this);
        switchDisableScreenshot.setOnCheckedChangeListener(this);
        switchDisableGPS.setOnCheckedChangeListener(this);
        switchDisableNetwork.setOnCheckedChangeListener(this);
        switchDisableNetworkShare.setOnCheckedChangeListener(this);
        switchAirPlane.setOnCheckedChangeListener(this);
        switchDisableBluetooth.setOnCheckedChangeListener(this);
        switchDisableWifi.setOnCheckedChangeListener(this);
    }

    private void initData() {
        disableCamera = false;
        disableScreenShot = false;
        disableGPS = false;
        disableNetwork = false;
        disableNetworkShare = false;
        disableAirPlane = false;
        disableBluetooth = false;
        disableWifi = false;

        switchDisableCamera = binding.switchDisableCamera;
        switchDisableScreenshot = binding.switchDisableScreenshot;
        switchDisableGPS = binding.switchDisableGps;
        switchDisableNetwork = binding.switchDisableNetwork;
        switchDisableNetworkShare = binding.switchDisableNetworkShare;
        switchAirPlane = binding.switchAirplaneMode;
        switchDisableBluetooth = binding.switchBluetooth;
        switchDisableWifi = binding.switchWifi;

        DatabaseReference deviceRestrictionRef = FirebaseDatabase.getInstance().getReference("DeviceRestriction");
        if(!(selectedRelationship.getRelationshipId().equals(""))){
            DatabaseReference ref = deviceRestrictionRef.child(selectedRelationship.getRelationshipId());
            ref.addChildEventListener( new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                    if(snapshot.exists()){
                        firebaseDeviceRestriction = (DeviceRestriction) snapshot.getValue(DeviceRestriction.class);
                        initialDeviceRestriction();
                        ref.removeEventListener(this);
                    } else {
                        firebaseDeviceRestriction = new DeviceRestriction();
                        firebaseDeviceRestriction.setDisableCamera(false);
                        firebaseDeviceRestriction.setDisableScreenShot(false);
                        firebaseDeviceRestriction.setDisableGPS(false);
                        firebaseDeviceRestriction.setDisableNetwork(false);
                        firebaseDeviceRestriction.setDisableNetworkShare(false);
                        firebaseDeviceRestriction.setDisableAirPlane(false);
                        firebaseDeviceRestriction.setDisableBluetooth(false);
                        firebaseDeviceRestriction.setDisableWifi(false);
                        initialDeviceRestriction();
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
                    firebaseDeviceRestriction = new DeviceRestriction();
                    firebaseDeviceRestriction.setDisableCamera(false);
                    firebaseDeviceRestriction.setDisableScreenShot(false);
                    firebaseDeviceRestriction.setDisableGPS(false);
                    firebaseDeviceRestriction.setDisableNetwork(false);
                    firebaseDeviceRestriction.setDisableNetworkShare(false);
                    firebaseDeviceRestriction.setDisableAirPlane(false);
                    firebaseDeviceRestriction.setDisableBluetooth(false);
                    firebaseDeviceRestriction.setDisableWifi(false);
                    initialDeviceRestriction();
                }
            });


        } else {
            firebaseDeviceRestriction = new DeviceRestriction();
            firebaseDeviceRestriction.setDisableCamera(false);
            firebaseDeviceRestriction.setDisableScreenShot(false);
            firebaseDeviceRestriction.setDisableGPS(false);
            firebaseDeviceRestriction.setDisableNetwork(false);
            firebaseDeviceRestriction.setDisableNetworkShare(false);
            firebaseDeviceRestriction.setDisableAirPlane(false);
            firebaseDeviceRestriction.setDisableBluetooth(false);
            firebaseDeviceRestriction.setDisableWifi(false);
            initialDeviceRestriction();
        }

    }

    public void initialDeviceRestriction(){
        if(firebaseDeviceRestriction != null){
            displayDeviceRestriction = firebaseDeviceRestriction;
            if(displayDeviceRestriction != null){
                if(displayDeviceRestriction.getDisableCamera() == true){
                    switchDisableCamera.setChecked(true);
                } else{
                    switchDisableCamera.setChecked(false);
                }

                if(displayDeviceRestriction.getDisableScreenShot() == true){
                    switchDisableScreenshot.setChecked(true);
                } else{
                    switchDisableScreenshot.setChecked(false);
                }

                if(displayDeviceRestriction.getDisableGPS() == true){
                    switchDisableGPS.setChecked(true);
                } else{
                    switchDisableGPS.setChecked(false);
                }

                if(displayDeviceRestriction.getDisableNetwork() == true){
                    switchDisableNetwork.setChecked(true);
                } else{
                    switchDisableNetwork.setChecked(false);
                }

                if(displayDeviceRestriction.getDisableNetworkShare() == true){
                    switchDisableNetworkShare.setChecked(true);
                } else{
                    switchDisableNetworkShare.setChecked(false);
                }


                if(displayDeviceRestriction.getDisableAirPlane() == true){
                    switchAirPlane.setChecked(true);
                } else{
                    switchAirPlane.setChecked(false);
                }

                if(displayDeviceRestriction.getDisableBluetooth() == true){
                    switchDisableBluetooth.setChecked(true);
                } else{
                    switchDisableBluetooth.setChecked(false);
                }

                if(displayDeviceRestriction.getDisableWifi() == true){
                    switchDisableWifi.setChecked(true);
                } else{
                    switchDisableWifi.setChecked(false);
                }
            }
        }
    }

    public void process() {
        updateDisableFeatureState();
        if (selectedRelationship != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DeviceRestriction");
            if(deviceRestriction != null && selectedRelationship.getRelationshipId() != ""){
                DatabaseReference disableFeatureRef = ref.child(selectedRelationship.getRelationshipId());
                disableFeatureRef.removeValue();
                disableFeatureRef.push().setValue(deviceRestriction);
            }else {
                Log.d("myApp", "Device Restriction update unsuccessfully");
            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        switch (compoundButton.getId()) {
            case R.id.switch_disable_camera:
                if(checked){
                    disableCamera = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Disable Camera successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    disableCamera = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Enable Camera successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.switch_disable_screenshot:
                if(checked){
                    disableScreenShot = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Disable Screenshot successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    disableScreenShot = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Enable Screenshot successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.switch_disable_gps:
                if(checked){
                    disableGPS = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Disable GPS successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    disableGPS = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Enable GPS successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.switch_disable_network:
                if(checked){
                    disableNetwork = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Disable Network Data successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    disableNetwork = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Enable Network Data successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.switch_disable_network_share:
                if(checked){
                    disableNetworkShare = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Disable Network Share successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    disableNetworkShare = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Enable Network Share successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.switch_airplane_mode:
                if(checked){
                    disableAirPlane = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Disable Airplane mode successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    disableAirPlane = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Enable Airplane mode successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.switch_bluetooth:
                if(checked){
                    disableBluetooth = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Disable Bluetooth successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    disableBluetooth = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Enable Bluetooth successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.switch_wifi:
                if(checked){
                    disableWifi = true;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Disable Wifi successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    disableWifi = false;
                    if(MainActivity.getContextOfApplication() != null){
                        Toast.makeText(MainActivity.getContextOfApplication(), "Enable Wifi successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public void updateDisableFeatureState(){
        deviceRestriction = new DeviceRestriction(disableCamera, disableScreenShot, disableGPS, disableNetwork, disableNetworkShare,disableAirPlane, disableBluetooth, disableWifi);
        Log.d("DisableFeatureFrag", "running update");

    }
}
