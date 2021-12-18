package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.dietnow.app.ucm.fdi.model.user.User;
import com.dietnow.app.ucm.fdi.service.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button login;
    private Button register;
    private Spinner genres;
    private EditText email;
    private EditText passwd;
    private EditText passwdRepeat;
    private EditText name;
    private EditText lastname;
    private EditText age;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // incilizar Google Firebase
        auth         = FirebaseAuth.getInstance();
        mDatabase    = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();

        // inicializar los componentes por ID
        email        = findViewById(R.id.registerEmail);
        passwd       = findViewById(R.id.registerPassword);
        passwdRepeat = findViewById(R.id.registerPasswordRepeat);
        name         = findViewById(R.id.registerName);
        lastname     = findViewById(R.id.registerLastname);
        age          = findViewById(R.id.registerAge);
        progressBar  = findViewById(R.id.progressBarAdmin);

        // login button action
        login = findViewById(R.id.registerLoginBtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // change genre action
        genres = findViewById(R.id.registerGenre);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.genres, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genres.setAdapter(adapter);
        genres.setOnItemSelectedListener(this);

        // register button action
        register = findViewById(R.id.registerBtn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                Boolean isValid = !email.getText().toString().isEmpty();
                isValid = isValid || !passwd.getText().toString().isEmpty();
                isValid = isValid || !passwdRepeat.getText().toString().isEmpty();
                isValid = isValid || !name.getText().toString().isEmpty();
                isValid = isValid || !age.getText().toString().isEmpty();

                if(isValid){
                    if(passwd.getText().toString().equalsIgnoreCase(passwdRepeat.getText().toString())){
                        User.UserGender uGender = User.UserGender.NO_GENRE;
                        if(!genres.getSelectedItem().toString().isEmpty()){
                            if(genres.getSelectedItem().toString().equalsIgnoreCase("masculino")){
                                uGender = User.UserGender.MALE;
                            } else if(genres.getSelectedItem().toString().equalsIgnoreCase("femenino")){
                                uGender = User.UserGender.FEMALE;
                            } else{
                                uGender = User.UserGender.NO_GENRE;
                            }
                        }

                        User user = UserService.getInstance().register(email.getText().toString(),
                                name.getText().toString(),
                                lastname.getText().toString(),
                                passwd.getText().toString(),
                                uGender,
                               0.0,
                                Integer.parseInt(age.getText().toString()));

                        register(user, passwd.getText().toString());
                    } else{
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.register_check_paaswords),
                                Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.register_check_fields),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void register(User user, String rawPassword){
        auth.createUserWithEmailAndPassword(user.getEmail(), rawPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    mDatabase.child("users").child(firebaseUser.getUid()).setValue(user);
                    updateUI(true);
                } else{
                    updateUI(false);
                }
            }
        });
    }

    // redirige al login o muestra error de registro
    private void updateUI(Boolean success){
        if(success){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.register_succesful),
                    Toast.LENGTH_SHORT).show();

            progressBar.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(RegisterActivity.this, UserPageActivity.class);
            startActivity(intent);
        } else{
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.register_failed),
                    Toast.LENGTH_LONG).show();
        }
    }

    // estos 2 metodos son de "AdapterView.OnItemSelectedListener" para el selector del genero
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String genre = parent.getItemAtPosition(position).toString();

        // ahora solo muestra mensaje, luego quitarlo
        // Toast.makeText(parent.getContext(), "Género: " + genre, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}