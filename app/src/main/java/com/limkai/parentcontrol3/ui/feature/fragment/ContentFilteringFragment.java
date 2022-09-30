package com.limkai.parentcontrol3.ui.feature.fragment;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.limkai.parentcontrol3.R;
import com.limkai.parentcontrol3.data.AppItem;
import com.limkai.parentcontrol3.data.ApplicationManagement;
import com.limkai.parentcontrol3.data.Relationship;
import com.limkai.parentcontrol3.data.WifiState;
import com.limkai.parentcontrol3.databinding.FragmentContentFilteringBinding;
import com.limkai.parentcontrol3.ui.MainActivity;

import org.jetbrains.annotations.NotNull;

//Blocks access to adult content, proxy and VPNs, phishing and malicious domains. It enforces Safe Search on Google, Bing and YouTube.
public class ContentFilteringFragment extends Fragment {

    private FragmentContentFilteringBinding binding;
    private Relationship selectedRelationship;
    private WifiState wifiState, displayWifiState;
    private TextView txtDnsState,txtWifiState;
    private ScrollView scrollView;

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
        binding = FragmentContentFilteringBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        scrollView = binding.scrollView;
        txtDnsState = binding.txtDNSFilteringState;

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Content Filtering");
        alert.setMessage("Device below Android 9 please use method 1, otherwise please choose either one.");
        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"Please follow the steps", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        txtWifiState = binding.txtWifiState;
        displayWifiState = new WifiState("","","","","","","");
        selectedRelationship = MainActivity.getSelectedRelationship();
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

    public void process() {
        DatabaseReference wifiStateRef = FirebaseDatabase.getInstance().getReference("WifiState");
        if(!(selectedRelationship.getRelationshipId().equals(""))){
            DatabaseReference ref = wifiStateRef.child(selectedRelationship.getRelationshipId());
            ref.addChildEventListener( new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                    if(snapshot.exists()){
                        wifiState = (WifiState) snapshot.getValue(WifiState.class);
                        updateWifiState();
                        setTxtWifiState();
                        //ref.removeEventListener(this);
                    } else {
                        wifiState = new WifiState();
                        wifiState.setS_dns1("");
                        wifiState.setS_dns2("");
                        wifiState.setS_ipAddress("");
                        wifiState.setS_gateway("");
                        wifiState.setS_leaseDuration("");
                        wifiState.setS_serverAddress("");
                        wifiState.setS_netmask("");
                        updateWifiState();
                        setTxtWifiState();
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
                    wifiState = new WifiState();
                    wifiState.setS_dns1("");
                    wifiState.setS_dns2("");
                    wifiState.setS_ipAddress("");
                    wifiState.setS_gateway("");
                    wifiState.setS_leaseDuration("");
                    wifiState.setS_serverAddress("");
                    wifiState.setS_netmask("");
                    updateWifiState();
                    setTxtWifiState();
                }
            });
        } else {
            wifiState = new WifiState();
            wifiState.setS_dns1("");
            wifiState.setS_dns2("");
            wifiState.setS_ipAddress("");
            wifiState.setS_gateway("");
            wifiState.setS_leaseDuration("");
            wifiState.setS_serverAddress("");
            wifiState.setS_netmask("");
            updateWifiState();
            setTxtWifiState();
        }
    }

    public void setTxtWifiState(){
        updateWifiState();
        if(displayWifiState!= null){
            if(displayWifiState.getS_ipAddress().equals("")){
                txtWifiState.setText("Failed to get Wifi State");
            } else{
                txtWifiState.setText(wifiState.toString());
            }

            if (displayWifiState.s_dns1.equals("DNS 1: 185.228.168.168") && displayWifiState.s_dns2.equals("DNS 2: 185.228.169.168")) {
                txtDnsState.setText("ON");
            } else {
                txtDnsState.setText("OFF");
            }
        } else {
            txtWifiState.setText("Failed to get Wifi State");
            txtDnsState.setText("OFF");
        }
    }

    public void updateWifiState(){
        displayWifiState = wifiState;
    }
}
