package com.example.campusgeoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.campusgeoquiz.utils.Campus_Geo_Quiz_API;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    Button register;
    Button login;
    EditText email,username,password;
    private FirebaseAuth firebaseAuth;
    private View dialogView;
    private DatabaseReference userDb;
    AlertDialog alertDialog;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    int count = 0;

    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        register = findViewById(R.id.register);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        firebaseAuth = firebaseAuth.getInstance();
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    //user is already logged in...
                } else
                {
                    //No current user
                }
            }
        };

        imageView.setOnTouchListener (new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() {
            }

            public void onSwipeRight() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.banner_homepage);
                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.banner_homepage);
                    textView.setText("Morning");
                    count = 0;
                }
            }

            public void onSwipeLeft() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.banner_night);
                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.banner_night);
                    textView.setText("Morning");
                    count = 0;
                }
            }

            public void onSwipeBottom() {
            }

        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())
                        && !TextUtils.isEmpty(username.getText().toString())){

                    String Email = email.getText().toString().trim();
                    String Password = password.getText().toString().trim();
                    String Username =username.getText().toString().trim();


                    createUserEmailAccount(Email, Password, Username);

                    Email = Email.replaceAll("[@, .]", "_");

                    userDb = FirebaseDatabase.getInstance()
                            .getReference("Invetory_for_" + Email);

                    userDb.child("exp").setValue(0);
                    userDb.child("to_lvl").setValue(100);
                    userDb.child("lvl").setValue(1);

                }
                else {
                    Toast.makeText(RegisterActivity.this, "Complete the fields", Toast.LENGTH_LONG);
                }

                if (!"%_admin_%".equals(username.getText().toString())){
                    startActivity(new Intent(RegisterActivity.this, MapsActivity.class));
                }else{
                    startActivity(new Intent(RegisterActivity.this, UserMapsActivity.class));
                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void createUserEmailAccount(String email, String password, final String username){
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        //go to add reflection activity

                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        final String currentUserId = currentUser.getUid();

                        //create a user map to add to a user collection
                        Map<String, String> userObj = new HashMap<>();
                        userObj.put("userId", currentUserId);
                        userObj.put("username", username);

                        //save to firestore

                        collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (Objects.requireNonNull(task.getResult()).exists()) {
//                                            progressBar.setVisibility(View.INVISIBLE);
                                            String name = task.getResult().getString("username");

                                            Campus_Geo_Quiz_API campusGeoQuizApi = Campus_Geo_Quiz_API.getInstance().getInstance(); //GLOBAL Api
                                            campusGeoQuizApi.setUserId(currentUserId);
                                            campusGeoQuizApi.setUsername(name);



                                            Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
                                            intent.putExtra("username", name);
                                            intent.putExtra("userId", currentUserId);

                                            startActivity(intent);


                                        }else{
//
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }else
                    {
                        Toast.makeText(RegisterActivity.this, "something went wrong", Toast.LENGTH_LONG);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }
        else
        {

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

}
