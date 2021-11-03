package com.dietnow.app.ucm.fdi;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.dietnow.app.ucm.fdi.model.user.User;
import com.dietnow.app.ucm.fdi.service.UserService;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterAdminActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private Button login;
    private Button register;
    private Spinner genres;
    private EditText email;
    private EditText passwd;
    private EditText passwdRepeat;
    private EditText name;
    private EditText lastname;
    private EditText age;
    private UserService userService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register_user);

        // Buscar los componentes de esta actividad por su ID


        userService = new UserService();

        // inicializar los componentes por ID
        email = findViewById(R.id.registerEmail);
        passwd = findViewById(R.id.registerPassword);
        passwdRepeat = findViewById(R.id.registerPasswordRepeat);
        name = findViewById(R.id.registerName);
        lastname = findViewById(R.id.registerLastname);
        age = findViewById(R.id.registerAge);

        // login button action
        login = findViewById(R.id.registerLoginBtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterAdminActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // change genre action
        genres = findViewById(R.id.registerGenre);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterAdminActivity.this, R.array.genres, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genres.setAdapter(adapter);
        genres.setOnItemSelectedListener(this);

        // register button action
        register = findViewById(R.id.registerBtn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isValid = !email.getText().toString().isEmpty();
                isValid = isValid || !passwd.getText().toString().isEmpty();
                isValid = isValid || !passwdRepeat.getText().toString().isEmpty();
                isValid = isValid || !name.getText().toString().isEmpty();
                isValid = isValid || !age.getText().toString().isEmpty();

                if(isValid){
                    if(passwd.getText().toString().equalsIgnoreCase(passwdRepeat.getText().toString())){
                        User.UserGender uGender = User.UserGender.NO_GENRE;
                        if(!genres.getSelectedItem().toString().isEmpty()){
                            if(genres.getSelectedItem().toString().matches("(masculino|male)")){
                                uGender = User.UserGender.MALE;
                            } else if(genres.getSelectedItem().toString().matches("(femenino|female)")){
                                uGender = User.UserGender.FEMALE;
                            } else{
                                uGender = User.UserGender.NO_GENRE;
                            }
                        }

                        // userService.register();
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

    //Para el selector de genero
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
