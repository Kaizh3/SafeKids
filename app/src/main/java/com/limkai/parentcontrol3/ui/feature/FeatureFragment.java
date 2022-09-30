package com.limkai.parentcontrol3.ui.feature;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.limkai.parentcontrol3.R;
import com.limkai.parentcontrol3.databinding.FragmentFeatureBinding;
import com.limkai.parentcontrol3.ui.feature.fragment.ApplicationManagementFragment;
import com.limkai.parentcontrol3.ui.feature.fragment.CallsSMSFragment;
import com.limkai.parentcontrol3.ui.feature.fragment.ContentFilteringFragment;
import com.limkai.parentcontrol3.ui.feature.fragment.DeviceRestrictionFragment;
import com.limkai.parentcontrol3.ui.feature.fragment.UsageControlFragment;

public class FeatureFragment extends Fragment {

    private FeatureViewModel featureViewModel;
    private FragmentFeatureBinding binding;

    private Button btnCallSMS, btnApplicationManagement, btnContentFiltering, btnDeviceRestriction, btnUsageControl;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        featureViewModel =
                new ViewModelProvider(this).get(FeatureViewModel.class);

        binding = FragmentFeatureBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnUsageControl = binding.btnUsageControl;
        btnApplicationManagement = binding.btnApplicationManagement;
        btnContentFiltering = binding.btnContentFiltering;
        btnDeviceRestriction = binding.btnDisableFeature;
        btnCallSMS = binding.btnCallsSmsLog;

        btnUsageControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_activity_main, new UsageControlFragment(), "findApplicationManagementFragment");
                ft.addToBackStack(UsageControlFragment.class.getName());
                ft.setReorderingAllowed(true);
                ft.commit();
            }
        });

        btnApplicationManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationManagementFragment applicationManagementFrag = new ApplicationManagementFragment();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_activity_main, applicationManagementFrag, "findApplicationManagementFragment");
                ft.addToBackStack(null); // you can use a string here, using the class name is just convenient
                ft.setReorderingAllowed(true);
                ft.commit();
            }
        });

        btnContentFiltering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentFilteringFragment contentFilteringFrag = new ContentFilteringFragment();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_activity_main, contentFilteringFrag, "findContentFilteringFragment");
                ft.addToBackStack(null); // you can use a string here, using the class name is just convenient
                ft.setReorderingAllowed(true);
                ft.commit();
            }
        });

        btnDeviceRestriction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.getContextOfApplication(), ContentFilteringActivity.class));
                DeviceRestrictionFragment deviceRestrictionFrag = new DeviceRestrictionFragment();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_activity_main, deviceRestrictionFrag, "findDisableFeatureFragment");
                ft.addToBackStack(null); // you can use a string here, using the class name is just convenient
                ft.setReorderingAllowed(true);
                ft.commit();
            }
        });

        btnCallSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.getContextOfApplication(), ContentFilteringActivity.class));
                CallsSMSFragment callsSMSFrag = new CallsSMSFragment();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_activity_main, callsSMSFrag, "findDisableFeatureFragment");
                ft.addToBackStack(null); // you can use a string here, using the class name is just convenient
                ft.setReorderingAllowed(true);
                ft.commit();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}