package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dietnow.app.ucm.fdi.apis.DietNowService;
import com.dietnow.app.ucm.fdi.model.user.User;
import com.dietnow.app.ucm.fdi.utils.BCrypt;
import com.dietnow.app.ucm.fdi.utils.DietNowTokens;
import com.dietnow.app.ucm.fdi.utils.RetrofitResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private Retrofit retrofit;
    private Switch status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        // inicializar Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

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
        status      = findViewById(R.id.editUserStatus);
        password    = findViewById(R.id.editTextPassword); // siempre vacio y solo se guarda si !empty()
        imageUser   = findViewById(R.id.imageUser);


        // se obtiene la info del usuario para rellenar los campos
        FirebaseUser currentUser = auth.getCurrentUser();
        /*
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
        */

        String uidParams = parametros.getString("uid");
        db.child("users").child(uidParams).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().getValue(User.class);
                    Log.d("USER FROM DB", user.toString());

                    date.setText(date.getText().toString() + ": " + user.getStart_date());
                    name.setText(user.getName());
                    lastname.setText(user.getLastname());
                    email.setText(user.getEmail());
                    age.setText(String.valueOf(user.getAge()));
                    status.setChecked(user.getActive());
                    data.put("name", user.getName());
                    data.put("lastname", user.getLastname());
                    data.put("email", user.getEmail());
                    data.put("age", String.valueOf(user.getAge()));
                    data.put("password", user.getPassword());
                    data.put("active", user.getActive() ? "yes" : "no");

                    setProfileImage();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailureUserProfile: ","");
                e.printStackTrace();
            }
        });

        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        // Guardar los campos
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actualUserId = parametros.get("uid").toString();

                for (String key : data.keySet()){
                    switch (key){
                        case "name":
                            if(!name.getText().toString().equals(data.get(key))){
                                db.child("users").child(actualUserId).child("name").setValue(name.getText().toString());
                            }
                            break;
                        case "lastname":
                            if(!lastname.getText().toString().equals(data.get(key))){
                                db.child("users").child(actualUserId).child("lastname").setValue(lastname.getText().toString());
                            }
                            break;
                        case "email":
                            if(!email.getText().toString().equalsIgnoreCase(data.get(key))){
                                updateAdminFields("email", email.getText().toString(), actualUserId);
                            }
                            break;
                        case "age":
                            if(!age.getText().toString().equalsIgnoreCase(data.get(key))){
                                db.child("users").child(actualUserId).child("age").setValue(Integer.valueOf(age.getText().toString()));
                            }
                            break;
                        case "password":
                            String rawPassword = password.getText().toString();
                            if(!rawPassword.isEmpty() && !BCrypt.checkpw(rawPassword, data.get(key))){
                                updateAdminFields("password", password.getText().toString(), actualUserId);
                            }
                            break;
                        case "active":
                            Boolean isActive = status.isChecked(), oldValue = data.get("active") == "yes";
                            if((isActive && !oldValue) || (oldValue && !isActive)){
                                db.child("users").child(actualUserId).child("active").setValue(isActive);
                            }
                            break;
                        default: break;
                    }
                }

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.profile_updated_sucesfully), Toast.LENGTH_SHORT).show();
                finish(); // volver hacia atras finalizando esta Activity
            }
        });
    }

    /**
     *
     * @param fieldKey email/password
     * @param fieldValue valor del email o contaseña
     * @param uid ID del usuario que se va a editar
     */
    private void updateAdminFields(String fieldKey, String fieldValue, String uid){
        FirebaseUser currentUser = auth.getCurrentUser();
        HashMap<String, String> params = new HashMap<>();
        String hashCode = DietNowTokens.generateToken(currentUser.getUid());

        if(!hashCode.isEmpty()){
            params.put("sender", currentUser.getUid());
            params.put(fieldKey, fieldValue);
            params.put("code", hashCode);
            params.put("uid", uid);

            DietNowService api = retrofit.create(DietNowService.class);
            Call<RetrofitResponse> request;
            if(fieldKey.equalsIgnoreCase("email")){
                request = api.editFirebaseuserEmail(params);
            } else{
                request = api.editFirebaseuserPassword(params);
            }
            request.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                    if(response.isSuccessful()){
                        if(fieldKey.equalsIgnoreCase("email")){
                            db.child("users").child(uid).child("email")
                                    .setValue(fieldValue);
                        } else{
                            db.child("users").child(uid).child("password")
                                    .setValue(BCrypt.hashpw(fieldValue, BCrypt.gensalt()));
                        }
                    }
                }

                @Override
                public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                    Log.d("FAILED", "FAILED HTTP REQUEST");
                    t.printStackTrace();
                }
            });
        }
    }

    private void setProfileImage(){
        storageRef = FirebaseStorage.getInstance().getReference(); // crear una instancia a la referencia del almacenamiento
        String fileName = "profile_" + parametros.getString("uid") + ".jpg";
        storageRef.child("images/" + fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
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
}