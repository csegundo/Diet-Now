package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.dietnow.app.ucm.fdi.model.user.*;

import java.io.InputStream;
import java.util.Map;

/**
 * UserProfileActivity - Establece el perfil del usuario en la aplicación
 */
public class UserProfileActivity extends AppCompatActivity {

    // Necesario para saber cuando el usuario ya ha elegido una imagen de la galeria
    private static final Integer PICK_IMAGE = 1;

    private TextView name, age;
    private Button settings;
    private ImageView image;
    private Button delete, change;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // inicializar los elementos
        settings  = findViewById(R.id.settings);
        name      = findViewById(R.id.profileName);
        age       = findViewById(R.id.profileAge);
        image     = (ImageView) findViewById(R.id.profileImage);
        change    = findViewById(R.id.profileChangeImg);

        // inicializar Google Firebase
        auth      = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();

        // llamada a Firebase para obtener la info del usuario logueado
        FirebaseUser currentUser = auth.getCurrentUser();
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("users").child(currentUser.getUid()).getValue(User.class);

                name.setText(user.getName().trim() + " " + user.getLastname().trim());
                age.setText(user.getAge() + " " + age.getText().toString());

                // TODO cambiar para que en el usuario se almacene la imagen de su perfil
                Boolean userHasImage = false;
                if(!userHasImage){
                    image.setImageResource(R.drawable.ic_person_128_black);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("UserProfile", "UserProfile:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(profileListener);

        // Cambiar la imagen del usuario: gallery/docs
        // https://stackoverflow.com/questions/5309190/android-pick-images-from-gallery
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromPhone();
            }
        });

        // Acciones de los componentes
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Acciones de los componentes
        delete = findViewById(R.id.deleteProfile);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
    }

    // this function is triggered when user selects the image from the imageChooser
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Log.d("IMAGEEEEEEEEEEEEEEEEN", String.valueOf(requestCode) + " " + String.valueOf(resultCode));

            if(data == null){
                return;
            }

            try{
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                // TODO convertir inputStream en Image y guardarlo en (?) https://stackoverflow.com/questions/5729236/whats-the-best-way-to-store-images-in-android
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void getImageFromPhone(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("return-data", true);
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(intent, getResources().getString(R.string.select_image));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    private void updateUser() {
        FirebaseUser user = auth.getCurrentUser();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // borrar de auth
                FirebaseUser userAuth = FirebaseAuth.getInstance().getCurrentUser();

                if (userAuth != null) {
                    Toast.makeText(getApplicationContext(),
                            "Usuario borrado correctamente",
                            Toast.LENGTH_LONG).show();

                    // borrar de la base de datos
                    User user = dataSnapshot.child("users").child(userAuth.getUid()).getValue(User.class);
                    user.setActive(false);
                    Map<String, Object> userValues = user.toMap();
                    mDatabase.child("users").child(userAuth.getUid()).updateChildren(userValues);

                    // redirige a la página principal
                    // auth.signOut();
                    Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                    startActivity(intent);


                } else {
                    Toast.makeText(getApplicationContext(),
                            "El usuario no se ha borrado correctamente",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "UserPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);
    }

}