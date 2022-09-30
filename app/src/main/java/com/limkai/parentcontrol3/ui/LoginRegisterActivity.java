package com.limkai.parentcontrol3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.limkai.parentcontrol3.R;
import com.limkai.parentcontrol3.data.User;
import com.limkai.parentcontrol3.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class LoginRegisterActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ArrayList<User> users;
    private int hasChild;
    private String userRole;
    private String parentEmail;

    private EditText edtUsername, edtPassword, edtEmail;
    private Button btnSubmit;
    private TextView txtLoginInfo, txtRole, txtParent, txtChild;
    private CardView cardViewParent, cardViewChild;

    private boolean isSigningUp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);


        users = new ArrayList<>();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);

        btnSubmit = findViewById(R.id.btnSubmit);

        txtRole = findViewById(R.id.txtRole);
        txtLoginInfo = findViewById(R.id.txtLoginInfo);
        txtParent = findViewById(R.id.txtParent);
        txtChild = findViewById(R.id.txtChild);

        cardViewParent = findViewById(R.id.cardViewParent);
        cardViewChild = findViewById(R.id.cardViewChild);

        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            //startActivity(new Intent(MainActivity.this,MainActivity.class));
            finish();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noWhiteSpace = "\\A\\w{4,20}\\z";
                if (isSigningUp && edtUsername.getText().toString().isEmpty()) {
                    edtUsername.setError("Field cannot be empty");
                    //Toast.makeText(LoginRegisterActivity.this, "Please enter username", Toast.LENGTH_SHORT).show();
                } else if(edtUsername.getText().toString().length() >= 15){
                    edtUsername.setError("Username too long");
                } else if(!edtUsername.getText().toString().matches(noWhiteSpace)){
                    edtUsername.setError("White spaces are not allowed");
                } else {
                    edtUsername.setError(null);
                }

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if(edtEmail.getText().toString().isEmpty()){
                    edtEmail.setError("Field cannot be empty");
                    //Toast.makeText(LoginRegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                } else if (!edtEmail.getText().toString().matches(emailPattern)){
                    edtEmail.setError("Email format wrong");
                } else {
                    edtEmail.setError(null);
                }

                String passwordVal = "^" +
                        "(?=.*[0-9])" +         //at least 1 digit
                        "(?=.*[a-z])" +         //at least 1 lower case letter
                        "(?=.*[A-Z])" +         //at least 1 upper case letter
                        "(?=.*[a-zA-Z])" +      //any letter
                        "(?=\\S+$)" +           //no white spaces
                        ".{4,}" +               //at least 4 characters
                        "$";
                if (edtPassword.getText().toString().isEmpty()){
                    edtPassword.setError("Field cannot be empty");
                    //Toast.makeText(LoginRegisterActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                } else if (isSigningUp && !edtPassword.getText().toString().matches(passwordVal)){
                    edtPassword.setError("Password is too weak");
                    Toast.makeText(LoginRegisterActivity.this, "The password must at least 1 digit, 1 lower case, 1 upper case, no white spaces and at least 4 characters", Toast.LENGTH_LONG).show();
                } else {
                    edtEmail.setError(null);
                }

                if (isSigningUp && txtRole.getText().toString() == "Role"){
                    Toast.makeText(LoginRegisterActivity.this, "Please select role by clicking the image above", Toast.LENGTH_LONG).show();
                }

                if (isSigningUp){
                    if(txtRole.getText().toString().equals("Parent") || txtRole.getText().toString().equals("Child")){
                        //verification
                        handleSignUp();
                    } else {
                        Toast.makeText(LoginRegisterActivity.this, "Please select role", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    users.clear();
                    FirebaseDatabase.getInstance().getReference("user").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                users.add(dataSnapshot.getValue(User.class));
                            }

                            for (User user: users){
                                if(user.getEmail().equals(edtEmail.getText().toString())){
                                    if(user.getRole().equals("Child")){
                                        Toast.makeText(LoginRegisterActivity.this, "Please download the child version", Toast.LENGTH_LONG).show();
                                        return;
                                    } else {
                                        userRole = user.getRole();
                                        hasChild = user.getHasChild();
                                        handleLogin(hasChild);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            userRole = "";
                            hasChild = 0;
                        }
                    });
                }
            }
        });

        cardViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtRole.setText("Parent");
                cardViewParent.setElevation(80);
                cardViewChild.setElevation(10);
            }
        });

        cardViewChild.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                txtRole.setText("Child");
                cardViewParent.setElevation(10);
                cardViewChild.setElevation(80);
            }
        });

        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSigningUp){
                    isSigningUp = false;
                    //set vislibility
                    edtUsername.setVisibility(View.GONE);
                    cardViewParent.setVisibility(View.GONE);
                    cardViewChild.setVisibility(View.GONE);
                    txtChild.setVisibility(View.GONE);
                    txtParent.setVisibility(View.GONE);
                    btnSubmit.setText("Log in");
                    txtLoginInfo.setText("Don't have an account? Sign up");
                }else {
                    isSigningUp = true;
                    edtUsername.setVisibility(View.VISIBLE);
                    cardViewParent.setVisibility(View.VISIBLE);
                    cardViewChild.setVisibility(View.VISIBLE);
                    txtChild.setVisibility(View.VISIBLE);
                    txtParent.setVisibility(View.VISIBLE);
                    btnSubmit.setText("Sign up");
                    txtLoginInfo.setText("Already have an account? Log in");
                }
            }
        });
    }

    private void handleLogin(int aldHasChild) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if(userRole.equals("Parent") && aldHasChild == 0 ){
                        Intent intent = new Intent(LoginRegisterActivity.this, RelationshipActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(LoginRegisterActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    } else if(userRole.equals("Parent") && aldHasChild >= 1){
                        Intent intent = new Intent(LoginRegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(LoginRegisterActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginRegisterActivity.this, "Please download child's version", Toast.LENGTH_SHORT).show();
                    }
                }else if(task.isCanceled()){
                    edtEmail.setError("No email found");
                    edtPassword.setError("Wrong password");
                    Toast.makeText(LoginRegisterActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                } else {
                    edtEmail.setError("No email found");
                    edtPassword.setError("Wrong password");
                    Toast.makeText(LoginRegisterActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleSignUp() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(edtUsername.getText().toString(),edtEmail.getText().toString(),txtRole.getText().toString(),"",0));
                    Toast.makeText(LoginRegisterActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                    edtEmail.getText().clear();
                    edtPassword.getText().clear();
                    edtUsername.getText().clear();
                    return;
                }else {
                    Toast.makeText(LoginRegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
