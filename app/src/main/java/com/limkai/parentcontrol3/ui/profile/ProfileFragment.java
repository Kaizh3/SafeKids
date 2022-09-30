package com.limkai.parentcontrol3.ui.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.limkai.parentcontrol3.R;
import com.limkai.parentcontrol3.data.User;
import com.limkai.parentcontrol3.databinding.FragmentProfileBinding;
import com.limkai.parentcontrol3.ui.LoginRegisterActivity;
import com.limkai.parentcontrol3.ui.MainActivity;
import com.limkai.parentcontrol3.ui.RelationshipActivity;

import java.io.IOException;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private Button btnLogOut, btnUpload, btnAddChild;
    private ImageView imgProfile;
    private LinearLayout mainLinearLayout;
    private TextView txtUsername;
    private Uri imagePath;
    private User user;
    private String userDetails, userId;
    private FirebaseUser currentFirebaseUser;

    private ProfileViewModel dashboardViewModel;
    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnLogOut = binding.btnLogOut;
        btnAddChild = binding.btnAddChild;
        imgProfile = binding.profileImg;
        txtUsername = binding.txtUsername;
        mainLinearLayout = getActivity().findViewById(R.id.enable);
        mainLinearLayout.setVisibility(View.GONE);

        user = new User();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userId = currentFirebaseUser.getUid();
        }
        if(userId != null){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("user");
            DatabaseReference userRef = database.child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        user = (User) dataSnapshot.getValue(User.class);
                        userDetails = "\n\n" + "NAME :  " + user.getUsername() + "\n\n" +
                                "ROLE :  " + user.getRole() +  "\n\n" +
                                "EMAIL :  " + user.getEmail();
                        txtUsername.setText(userDetails);
                    } else {
                        user = new User();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    user = new User();
                }
            });

        }

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginRegisterActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        /*
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imagePath != null){
                    uploadImage();
                } else {
                    Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
                }

            }
        });
         */

        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RelationshipActivity.class));
            }
        });

        /*
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery of device
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,1);
            }
        });

         */

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainLinearLayout.setVisibility(View.VISIBLE);
        binding = null;
    }

    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data!=null){
            //data type is uri
            imagePath = data.getData();
            //getImageInImageView();
        }
    }

    private void getImageInImageView() {
        //bitmap image
        Bitmap bitmap = null;
        try {
            //uri to bitmap
            bitmap = MediaStore.Images.Media.getBitmap(MainActivity.getContextOfApplication().getContentResolver(),imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgProfile.setImageBitmap(bitmap);

    }

    private void uploadImage(){

        // show progress
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();


        FirebaseStorage.getInstance().getReference("images/"+ UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                updateProfilePicture(task.getResult().toString());
                            }
                        }
                    });
                    //Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                progressDialog.setMessage(" Uploaded "+(int) progress + "%");
            }
        });
    }

    private void updateProfilePicture(String url){
        FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profilePicture").setValue(url);
    }

     */
}