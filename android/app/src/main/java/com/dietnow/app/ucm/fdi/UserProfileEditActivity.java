package com.dietnow.app.ucm.fdi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dietnow.app.ucm.fdi.model.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Desde la vista ADMIN de todos los usuarios, en la tabla hay acciones sobre los usuarios como
 * eliminar o editar. La accion de editar se realiza sobre esta pagina
 *
 * Tambien sirve para que un usuario edite su propio perfil
 */
public class UserProfileEditActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference db;
    private EditText name, lastname, email, password, age;
    private TextView date;
    private Button save;
    // almacena los datos del usuario para saber si ha cambiado alguno y guardar solo lo que ha cambiado
    private HashMap<String, String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        // incilizar Google Firebase
        auth        = FirebaseAuth.getInstance();
        db          = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();

        // inicializar los componentes
        data        = new HashMap<>();
        save        = findViewById(R.id.editProfileSave);
        date        = findViewById(R.id.textViewCreated);
        name        = findViewById(R.id.editTextName);
        lastname    = findViewById(R.id.editTextLastname);
        email       = findViewById(R.id.editTextEmail);
        age         = findViewById(R.id.editTextAge);
        password    = findViewById(R.id.editTextPassword); // siempre vacio y solo se guarda si !empty()

        // se obtiene la info del usuario para rellenar los campos
        FirebaseUser currentUser = auth.getCurrentUser();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("users").child(currentUser.getUid()).getValue(User.class);
                Log.d("USER FROM DB", user.toString());

                date.setText(date.getText().toString() + ": " + user.getStart_date());
                name.setText(user.getName());
                lastname.setText(user.getLastname());
                email.setText(user.getEmail());
                age.setText(String.valueOf(user.getAge()));
                data.put("name", user.getName());
                data.put("lastname", user.getLastname());
                data.put("email", user.getEmail());
                data.put("age", String.valueOf(user.getAge()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "UserPost:onCancelled", databaseError.toException());
            }
        };
        db.addValueEventListener(postListener);

        // Guardar los campos
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String key : data.keySet()){
                    Log.d("DATA KEY", key + " - " + data.get(key));
                    // si data.{key} ha cambiado con el valor que hay en la vista ->
                    // llamar a Firebase para editar el campo {key} de currentUser.getUid()
                }
            }
        });
    }
}