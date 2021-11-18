package com.dietnow.app.ucm.fdi;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

import com.dietnow.app.ucm.fdi.model.user.User;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import androidx.appcompat.app.AppCompatActivity;


/*Codigo original que metia valores de manera dinamica
 * https://www.tutorialspoint.com/how-to-dynamically-update-a-listview-in-android
 * */


public class AllUserActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private ListView listView;
    private DataSnapshot userList;
    private ArrayList<User> list = new ArrayList<>();
    private ArrayList<String> listName = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private FirebaseAuth auth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        db       = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        fillList();
        button = findViewById(R.id.btnAdd);
        listView = findViewById(R.id.listView);
        editText = findViewById(R.id.editText);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listName);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //La idea es que una vez se hace clic la linea de abajo filtra y solo contiene
                //a los usuarios cuyos nombres se parezcan a lo introducido
                List<String> filteredList = Lists.newArrayList(Collections2.filter(
                        listName, Predicates.containsPattern(editText.getText().toString())));
                editText.setText("");
                for (String name: filteredList) {
                    Log.d("",name);
                }

                adapter.notifyDataSetChanged();
            }
        };
        button.setOnClickListener(onClickListener);
        listView.setAdapter(adapter);
    }
    private void fillList(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> users= new ArrayList<>();
                ArrayList<String> usernames= new ArrayList<>();
                Log.d("DB", dataSnapshot.child("users").toString());
                for(DataSnapshot ds : dataSnapshot.child("users").getChildren()) {
                    String email = ds.child("email").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);
                    String lastname = ds.child("lastname").getValue(String.class);
                    String password = ds.child("password").getValue(String.class);
                    String gender = ds.child("gender").getValue(String.class);
                    Double height = ds.child("height").getValue(Double.class);
                    String role = ds.child("role").getValue(String.class);
                    Integer age = ds.child("age").getValue(Integer.class);
                    String date = ds.child("date").getValue(String.class);

                    User us = new User(email,name,lastname,password,gender,height,role,age,date);
                    users.add(us);
                    usernames.add(name);
                }
                asignResult(users,usernames);
                //En este punto la lista names contiene los nombres de los usuarios registrados pero no se
                //como sacarla de aqui y mostrarla por pantalla --> es necesario sacarla de aqui y meterla en la linea
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "UserPost:onCancelled", databaseError.toException());
            }
        };
        db.addValueEventListener(postListener);
    }
    private void asignResult (ArrayList<User> query, ArrayList<String> usernames){
        list = query;
        listName = usernames;
    }



}




/*package com.dietnow.app.ucm.fdi;

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
}*/










