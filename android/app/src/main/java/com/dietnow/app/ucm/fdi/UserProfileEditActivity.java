package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dietnow.app.ucm.fdi.model.user.User;
import com.dietnow.app.ucm.fdi.utils.BCrypt;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Desde la vista ADMIN de todos los usuarios, en la tabla hay acciones sobre los usuarios como
 * eliminar o editar. La accion de editar se realiza sobre esta pagina
 *
 * Tambien sirve para que un usuario edite su propio perfil
 */
public class UserProfileEditActivity extends AppCompatActivity {

    private ImageView imageUser;
    private FirebaseAuth auth;
    private DatabaseReference db;
    private EditText name, lastname, email, password, age;
    private TextView date;
    private Button save;
    private Bundle parametros;
    // almacena los datos del usuario para saber si ha cambiado alguno y guardar solo lo que ha cambiado
    private HashMap<String, String> data;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        // parametros intent
        parametros  = getIntent().getExtras();

        // incializar Google Firebase
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
        imageUser   = findViewById(R.id.imageUser);


        // se obtiene la info del usuario para rellenar los campos
        FirebaseUser currentUser = auth.getCurrentUser();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uidParams = parametros.getString("uid");
                User user = dataSnapshot.child("users").child(uidParams).getValue(User.class);
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
                data.put("password", user.getPassword());

                storageRef = FirebaseStorage.getInstance().getReference(); // crear una instancia a la referencia del almacenamiento
                String fileName = "profile_" + uidParams + ".jpg";
                storageRef.child("images/" + fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();
                        Executor executor = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    InputStream in = new java.net.URL(uri.toString()).openStream();
                                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                                    handler.post(new Runnable() { // making changes in UI
                                        @Override
                                        public void run() {
                                            imageUser.setImageBitmap(bitmap);
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    imageUser.setImageResource(R.drawable.ic_person_128_black); // imagen predeterminada
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        imageUser.setImageResource(R.drawable.ic_person_128_black); // imagen predeterminada
                    }
                });
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
                String actualUserId = parametros.get("uid").toString();

                for (String key : data.keySet()){
                    // si data.{key} ha cambiado con el valor que hay en la vista ->
                    // llamar a Firebase para editar el campo {key} del usuario en cuestion
                    switch (key){
                        case "name":
                            if(!name.getText().toString().equalsIgnoreCase(data.get(key))){
                                db.child("users").child(actualUserId).child("name")
                                        .setValue(name.getText().toString());
                            }
                            break;
                        case "lastname":
                            if(!lastname.getText().toString().equalsIgnoreCase(data.get(key))){
                                db.child("users").child(actualUserId).child("lastname")
                                        .setValue(lastname.getText().toString());
                            }
                            break;
                        case "email":
                            // TODO se deberia cambiar tambien en Firebase Auth ???
                            if(!email.getText().toString().equalsIgnoreCase(data.get(key))){
                                db.child("users").child(actualUserId).child("email")
                                        .setValue(email.getText().toString());
                            }
                            break;
                        case "age":
                            if(!age.getText().toString().equalsIgnoreCase(data.get(key))){
                                db.child("users").child(actualUserId).child("age")
                                        .setValue(Integer.valueOf(age.getText().toString()));
                            }
                            break;
                        case "password":
                            // TODO igual que el email
                            String rawPassword = password.getText().toString();
                            if(!rawPassword.isEmpty() && !BCrypt.checkpw(rawPassword, data.get(key))){
                                db.child("users").child(actualUserId).child("password")
                                        .setValue(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
                            }
                            break;
                        default: break;
                    }
                }
            }
        });
    }
}