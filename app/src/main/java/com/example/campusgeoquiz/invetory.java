package com.example.campusgeoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Quiz;

public class invetory extends AppCompatActivity {

    public static String cEmail;
    private List<Product> productList;
    private DatabaseReference userDb;
    private QuizRecycleAdapter quizRecycleAdapter;
    private RecyclerView recyclerView;
    private String image;

    public static  int exp = 0;
    public static  int j = 100;
    public static int lvl = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        productList = new ArrayList<>();
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {

                //get the email of the current user and replace the special characters with "_".
                cEmail = profile.getEmail();
                cEmail = cEmail.replaceAll("[@, .]", "_");

            }
        }


    }

    @Override
    protected void onStart(){
        super.onStart();

        userDb = FirebaseDatabase.getInstance()
                .getReference("Invetory_for_" + cEmail);


        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    for (DataSnapshot snap: snapshot.getChildren()){
                        Log.d("hello", " " + snap.getValue());
                        Product item = new Product();

                        item.setImage(String.valueOf(snap.getValue()));
                        productList.add(item);

                    }
                }
                quizRecycleAdapter = new QuizRecycleAdapter(invetory.this, productList);
                recyclerView.setAdapter(quizRecycleAdapter);
                quizRecycleAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
