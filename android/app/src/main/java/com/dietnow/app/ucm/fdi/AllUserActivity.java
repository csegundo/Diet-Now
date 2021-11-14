package com.dietnow.app.ucm.fdi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class AllUserActivity extends AppCompatActivity {
    private ImageButton search;
    private FirebaseAuth auth;
    private TextInputEditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        searchText=findViewById(R.id.userSearchText);
        search=findViewById(R.id.userSearchButton);

        //prueba
        TextView tv = new TextView(this);
        tv.setText("my text");
        tv.setTextSize(30);

    }
}