package com.limkai.parentcontrol3.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.limkai.parentcontrol3.GlideApp;
import com.limkai.parentcontrol3.R;
import com.limkai.parentcontrol3.data.AppItem;
import com.limkai.parentcontrol3.data.DataManager;
import com.limkai.parentcontrol3.data.Relationship;
import com.limkai.parentcontrol3.data.User;
import com.limkai.parentcontrol3.data.UserLocation;
import com.limkai.parentcontrol3.databinding.FragmentHomeBinding;
import com.limkai.parentcontrol3.ui.MainActivity;
import com.limkai.parentcontrol3.util.AppUtil;
import com.limkai.parentcontrol3.util.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;
    LocationListener locationListener;

    private UserLocation childLocation;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private SwipeRefreshLayout mSwipe;
    private long mTotal;
    private ArrayList<AppItem> mData;
    private AppItemAdapter appItemAdapter, newAppItemAdapter;
    private RecyclerView mList;
    private Relationship selectedRelationship;
    private TextView txtTotalUsageTime;
    private int counter;
    private long snapshotLength;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5*1000; //Delay for 10 seconds.  One second = 1000 milliseconds.

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = (MapView) binding.map;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //SupportMapFragment mapFragment = (SupportMapFragment) getParentFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this::onMapReady);

        mSwipe = binding.swipeRefresh;
        mData = new ArrayList<>();

        appItemAdapter = new AppItemAdapter(mData, MainActivity.getContextOfApplication());
        txtTotalUsageTime = binding.txtTotalUsageTime;

        mList = binding.list;
        mList.setLayoutManager(new LinearLayoutManager(MainActivity.getContextOfApplication()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mList.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, MainActivity.getContextOfApplication().getTheme()));
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(appItemAdapter);

        initEvents();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        selectedRelationship = MainActivity.getSelectedRelationship();
        process();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        process();
        //selectedRelationship = MainActivity.getSelectedRelationship();
        //process();

        /*
            handler.postDelayed( runnable = new Runnable() {
                public void run() {
                    //do something
                    process();
                    while (handler.hasCallbacks(runnable) == true){
                        handler.removeCallbacks(runnable);
                    }
                    handler.postDelayed(runnable, delay);
                }
            }, delay);


         */

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mList.setAdapter(null);
        mapView.onDestroy();
        binding = null;
    }

    private void initEvents() {
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                process();
            }
        });
    }

    private void process() {
        //get App Usage Data
        mData = new ArrayList<>();
        //appItemAdapter.getData().clear();
        mData.clear();
        mData.clear();
        mData.clear();
        selectedRelationship = MainActivity.getSelectedRelationship();
        ChildEventListener listener;
        counter = 0;
        if (selectedRelationship != null) {
            //receive app usage data using firebase realtime database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AppItem/" + selectedRelationship.getRelationshipId());
            listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                    if(counter == 0){
                        mData.clear();
                        Log.d("home","onchildadded clear mData");
                    }
                    if(snapshot.exists()){
                        mData.add(snapshot.getValue(AppItem.class));
                        Log.d("home", Long.toString(counter));
                        counter = counter + 1;
                    }
                    if(counter >= snapshotLength){
                        Log.d("home","onchildadded update data");
                        appItemAdapter.updateData(mData);
                        calTotalUsageTime();
                        counter = 0;
                        ref.removeEventListener(this);
                    }
                    //mData.clear();
                    //Log.d("home","onchildadded clear mdata");
                }

                @Override
                public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                    /*
                    if(counter == 0){
                        mData.clear();
                        mData.clear();
                        mData.clear();
                        Log.d("home","onchildRemoved clear mData");
                    }

                     */
                }

                @Override
                public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    mData.clear();
                    calTotalUsageTime();
                    appItemAdapter.updateData(mData);
                }
            };

            ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    snapshotLength = dataSnapshot.getChildrenCount();
                    Log.d("home", Long.toString(snapshotLength));
                    ref.addChildEventListener(listener);
                }
            });



            //ref.removeEventListener(listener);
        }

        //get location
        childLocation = new UserLocation();
        if (selectedRelationship != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Location").child(selectedRelationship.getRelationshipId());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        childLocation = (UserLocation) snapshot.getValue(UserLocation.class);
                        /*
                        double latitude = 0.0;
                        double longitude =  0.0;
                        for(DataSnapshot snapshot1: snapshot.getChildren()){
                            latitude = snapshot1.child("latitude").getValue(Double.class);
                            longitude = snapshot1.child("longitude").getValue(Double.class);
                        }
                        childLocation = new UserLocation(latitude, longitude);

                         */
                    } else {
                        childLocation = new UserLocation(0, 0);
                    }
                    onMapReady(mMap);
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    childLocation = new UserLocation(0, 0);
                }
            });

        } else {
            childLocation = new UserLocation(0, 0);
        }

        mSwipe.setRefreshing(false);
    }

    public void calTotalUsageTime(){
        mTotal = 0;
        for (int counter = 0; counter < mData.size(); counter++) {
            if (mData.get(counter).mUsageTime <= 0) continue;
            mTotal += mData.get(counter).mUsageTime;
        }
        if(mTotal != 0){
            if(MainActivity.getContextOfApplication() != null){
                txtTotalUsageTime.setText("Total Usage Time - " + String.format(getResources().getString(R.string.total), AppUtil.formatMilliSeconds(mTotal)));
            }
        }
    }


    public class AppItemAdapter extends RecyclerView.Adapter<AppItemAdapter.AppItemViewHolder> {

        private ArrayList<AppItem> adapterData;
        private Context context;

        public AppItemAdapter(ArrayList<AppItem> mData, Context context){
            this.adapterData = mData;
            this.context = context;
        }

        ArrayList<AppItem> getData(){
            return adapterData;
        }

        void updateData(ArrayList<AppItem> data) {
            adapterData = data;
            notifyDataSetChanged();
        }

        AppItem getItemInfoByPosition(int position) {
            if (adapterData.size() > position) {
                return adapterData.get(position);
            }
            return null;
        }

        @NonNull
        @Override
        public AppItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            return new AppItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AppItemViewHolder holder, int position) {
            AppItem item = getItemInfoByPosition(position);
            holder.mName.setText(item.mName);
            holder.mUsage.setText(AppUtil.formatMilliSeconds(item.mUsageTime));
            holder.mTime.setText(String.format(Locale.getDefault(),
                    "%s · %d %s",
                    new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(item.mEventTime)),
                    item.mCount,
                    getResources().getString(R.string.times_only))
            );
            if (mTotal > 0) {
                holder.mProgress.setProgress((int) (item.mUsageTime * 100 / mTotal));
            } else {
                holder.mProgress.setProgress(0);
            }
            GlideApp.with(MainActivity.getContextOfApplication())
                    .load(AppUtil.getPackageIcon(MainActivity.getContextOfApplication(), item.mPackageName))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(holder.mIcon);
        }

        @Override
        public int getItemCount() {
            return adapterData.size();
        }

        class AppItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

            private TextView mName;
            private TextView mUsage;
            private TextView mTime;
            private ImageView mIcon;
            private ProgressBar mProgress;

            public AppItemViewHolder(View itemView) {
                super(itemView);
                mName = itemView.findViewById(R.id.app_name);
                mUsage = itemView.findViewById(R.id.app_usage);
                mTime = itemView.findViewById(R.id.app_time);
                mIcon = itemView.findViewById(R.id.app_image);
                mProgress = itemView.findViewById(R.id.progressBar);
                itemView.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //MapsInitializer.initialize(this.getActivity());
        mMap = googleMap;

        mMap.clear();
        if(childLocation.getLatitude() != 0 && childLocation.getLongitude() != 0){
            LatLng latLng = new LatLng(childLocation.getLatitude(), childLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Here");
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
            mMap.addMarker(markerOptions);
            float zoomLevel = 12.0f;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
            Log.d("myApp","Show location successfully");
        } else {
            mMap.clear();
            Log.d("myApp","childLocation retrieve unsuccessfully");
        }
    }


}