package com.limkai.parentcontrol3.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.limkai.parentcontrol3.R;
import com.limkai.parentcontrol3.data.Emergency;
import com.limkai.parentcontrol3.data.Relationship;
import com.limkai.parentcontrol3.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static Context contextOfApplication;
    private ActivityMainBinding binding;

    private ArrayAdapter<Relationship> adapter;
    private ArrayList<Relationship> relationships;
    private ArrayList<Relationship> children;
    private String parentEmail;
    private TextView txtChildName;
    private Spinner spinner;
    private Emergency emergency;
    private static Relationship selectedRelationship;

    private static final String CHANNEL_ID = "emergency_clicked";

    Handler handler = new Handler();
    int delay = 10 *1000; //Delay for 10 seconds.  One second = 1000 milliseconds.

    private Runnable runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {
            //do something
            getEmergency();
            while (handler.hasCallbacks(runnable) == true){
                handler.removeCallbacks(runnable);
            }
            handler =  new Handler();
            handler.postDelayed(this, delay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextOfApplication = getApplicationContext();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        relationships = new ArrayList<>();
        children = new ArrayList<>();
        emergency = new Emergency();

        initSpinner();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_feature, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public void onStart() {
        super.onStart();
        parentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        handler =  new Handler();
        handler.postDelayed(runnable, delay);
    }


    @Override
    protected void onResume() {
        super.onResume();
            parentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
            getChild(parentEmail);
            getEmergency();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        while (handler.hasCallbacks(runnable) == true){
            handler.removeCallbacks(runnable);
        }
        FirebaseAuth.getInstance().signOut();
    }

    private void handleNestedFragmentsBackStack() {
        FragmentManager navHostChildFragmentManager = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main).getChildFragmentManager();

        if (navHostChildFragmentManager.getBackStackEntryCount() > 1) {
            navHostChildFragmentManager.popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
       handleNestedFragmentsBackStack();
    }


    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    public static String getCurrentUserID () { return FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();}

    private void initSpinner() {
        spinner = binding.spinnerSelectChild;
        adapter = new ArrayAdapter<Relationship>(this, android.R.layout.simple_spinner_item, children);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

    }


    public static Relationship getSelectedRelationship(){
        return selectedRelationship;
    }

    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            Log.d("select spinner", "called!");
            selectedRelationship = (Relationship) parent.getItemAtPosition(pos);
            Toast.makeText(MainActivity.getContextOfApplication(),"You selected " + selectedRelationship.getChild() , Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

    private void getChild(String parentEmail){
        //prevent duplicate data

        relationships.clear();
        FirebaseDatabase.getInstance().getReference("relationship").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    relationships.add(dataSnapshot.getValue(Relationship.class));
                }

                children.clear();
                for (Relationship relationship: relationships){
                    if(parentEmail.equals(relationship.getParentEmail())){
                        children.add(relationship);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getEmergency(){
        if (selectedRelationship != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Emergency");
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(selectedRelationship.getRelationshipId())){
                        DatabaseReference emergencyRef = ref.child(selectedRelationship.getRelationshipId());
                        ChildEventListener childEventListener = new ChildEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                                if(snapshot.exists()){
                                    Log.d("Main", "Emergency");
                                    emergency = (Emergency) snapshot.getValue(Emergency.class);
                                    if(emergency.isEmergencyClicked() == true){
                                        Log.d("Main", "Emergency set noti, removeListener");
                                        setUpNotification();
                                    }
                                } else {
                                    emergency = new Emergency();
                                    emergency.setEmergencyClicked(false);
                                }
                                emergencyRef.removeEventListener(this);
                                ref.removeEventListener(this);
                                emergencyRef.removeEventListener(this);
                                ref.removeEventListener(this);
                                emergencyRef.removeEventListener(this);
                                ref.removeEventListener(this);
                            }

                            @Override
                            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                                Log.d("Main", "OnChildChanged");
                            }

                            @Override
                            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                                Log.d("Main", "OnChildRemoved");
                            }

                            @Override
                            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                                Log.d("Main", "OnChildMoved");
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                Log.d("Main", "OnCancelled");
                                emergency.setEmergencyClicked(false);
                            }
                        };
                        emergencyRef.removeEventListener(childEventListener);
                        emergencyRef.addChildEventListener(childEventListener);
                    } else {
                        emergency.setEmergencyClicked(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            };

            ref.removeEventListener(valueEventListener);
            ref.addValueEventListener(valueEventListener);

        } else {

        }
    }

    private void uploadEmergency(){
        if (selectedRelationship != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Emergency");
            if(emergency!= null && selectedRelationship.getRelationshipId() != ""){
                DatabaseReference emergencyRef = ref.child(selectedRelationship.getRelationshipId());
                emergencyRef.removeValue();
                emergencyRef.push().setValue(emergency);
                Log.d("MainActivity", "uplaod emergency to false");
            }else {
                Log.d("MainActivity", "No relationship");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setUpNotification(){
        if(emergency.isEmergencyClicked()){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Emergency Button Clicked", NotificationManager.IMPORTANCE_HIGH);

            Intent intent = new Intent(getContextOfApplication(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContextOfApplication(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Notification.Builder builder = new Notification.Builder(getContextOfApplication())
                    .setContentTitle(selectedRelationship.getChild() + " need help !!")
                    .setContentText("Your child " + selectedRelationship.getChild() + " clicked the emergency button.")
                    .setSmallIcon(R.drawable.ic_baseline_adb_24)
                    .setOngoing(true)
                    .setChannelId(CHANNEL_ID);

            NotificationManager notificationManager = (NotificationManager)  this.getSystemService( this.NOTIFICATION_SERVICE );
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(1, builder.build());
            emergency.setEmergencyClicked(false);
            uploadEmergency();
        }
    }
}