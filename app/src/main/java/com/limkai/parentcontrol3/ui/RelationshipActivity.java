package com.limkai.parentcontrol3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.limkai.parentcontrol3.R;
import com.limkai.parentcontrol3.data.Relationship;
import com.limkai.parentcontrol3.data.User;

import java.util.ArrayList;

public class RelationshipActivity extends AppCompatActivity{

    private ArrayList<User> users;
    private int initialChild, finalChild;

    private EditText edtChildEmail;
    private Button btnConnect;

    String emailOfChild, usernameOfParent, usernameOfChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship);

        users = new ArrayList<>();

        initialChild = 0;
        finalChild = 0;

        edtChildEmail = findViewById(R.id.edtChildEmail);
        btnConnect = findViewById(R.id.btnConnect);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("user");
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference username = database.child(id).child("username");

        username.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usernameOfParent = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                usernameOfParent = "";
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailOfChild = edtChildEmail.getText().toString();
                getUsers(emailOfChild);
                getUsers(emailOfChild);
                getUsers(emailOfChild);
                if (edtChildEmail.getText().toString().isEmpty()){
                    Toast.makeText(RelationshipActivity.this, "No Email Entered!", Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    String relationshipID = usernameOfParent + usernameOfChild;
                    Log.d("connec","start");
                    // start, start
                    //Log.d("connec",usernameOfChild.toString());
                    Log.d("connec",usernameOfParent.toString());
                    Log.d("connec",emailOfChild.toString());
                    Log.d("connec",relationshipID.toString());
                    if(usernameOfChild != null && usernameOfParent != null && emailOfChild != null){
                        Log.d("connec","start 2");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("relationship/");
                        ref.push().setValue(new Relationship(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString(),
                                emailOfChild, usernameOfParent, usernameOfChild,relationshipID)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d("connec","task complete");
                                    /*
                                    database.child(id).child("hasChild").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            initialChild = Integer.valueOf(snapshot.getValue().toString());
                                            database.removeEventListener(this);
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            initialChild = 0;
                                            database.removeEventListener(this);
                                        }
                                    });
                                     */

                                    finalChild = initialChild + 1;
                                    updateHasChild(finalChild);
                                    //second time clicked then show this
                                    Toast.makeText(RelationshipActivity.this, "Connect your child successfully", Toast.LENGTH_LONG).show();

                                    // try add flags
                                    Intent intent = new Intent(RelationshipActivity.this, MainActivity.class);
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    Log.d("connec","start activity");

                                }else if(task.isComplete()) {
                                    Toast.makeText(RelationshipActivity.this, "Please make sure the child's account exist", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void getUsers(String childEmail){
        //prevent duplicate data
        users.clear();
        FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    users.add(dataSnapshot.getValue(User.class));
                }

                for (User user: users){
                    if(childEmail.equals(user.getEmail())){
                        Log.d("connect","getUsers successfully");
                        usernameOfChild = user.getUsername();
                        initialChild = user.getHasChild();
                        return ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                users = new  ArrayList<>();
                usernameOfChild = "";
                initialChild = 0;
            }
        });
    }

    private void updateHasChild(Integer noOfChild){
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid() + "/hasChild").setValue(noOfChild);
    }
}
