package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.dietnow.app.ucm.fdi.model.user.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * UserProfileActivity - Establece el perfil del usuario en la aplicación
 */
public class UserProfileActivity extends AppCompatActivity {

    // Necesario para saber cuando el usuario ya ha elegido una imagen de la galeria
    private static final Integer PICK_IMAGE = 1;

    private TextView name, age;
    private Button settings;
    private ImageView image;
    private Uri filePath;
    private Button delete, change, editProfile;

    private DatabaseReference db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageRef, imagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // inicializar los elementos
        settings   = findViewById(R.id.settings);
        name       = findViewById(R.id.profileName);
        age        = findViewById(R.id.profileAge);
        image      = (ImageView) findViewById(R.id.profileImage);
        change     = findViewById(R.id.profileChangeImg);
        editProfile= findViewById(R.id.profileEditProfile);

        // inicializar Google Firebase
        auth       = FirebaseAuth.getInstance();
        db         = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();

        /*
         * Para acceder a imagenes es con imagesRef.child("fileName")
         * Propiedades de las referencias: getPath(), getName() y getBucket()
         */
        storageRef = FirebaseStorage.getInstance().getReference(); // crear una instancia a la referencia del almacenamiento
        imagesRef  = storageRef.child("images"); // referencia exclusivamente para imagenes (nivel mas bajo)

        // llamada a Firebase para obtener la info del usuario logueado y actualizar la vista
        FirebaseUser currentUser = auth.getCurrentUser();
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("users").child(currentUser.getUid()).getValue(User.class);

                name.setText(user.getName().trim() + " " + user.getLastname().trim());
                age.setText(user.getAge() + " " + age.getText().toString());

                String fileName = "profile_" + currentUser.getUid() + ".jpg";
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
                                            image.setImageBitmap(bitmap);
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    image.setImageResource(R.drawable.ic_person_128_black); // imagen predeterminada
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        image.setImageResource(R.drawable.ic_person_128_black); // imagen predeterminada
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("UserProfile", "UserProfile:onCancelled", databaseError.toException());
            }
        };
        db.addValueEventListener(profileListener);

        // Cambiar la imagen del usuario: gallery/docs
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromPhone();
            }
        });

        // editar el perfil del usuario
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, UserProfileEditActivity.class);
                startActivity(intent);
            }
        });

        // Acciones del perfil
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Borrar perfil del usuario
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
            if(data == null){
                return;
            }
            try{
                // TODO proceso explicativo https://www.youtube.com/watch?v=ZmgncLHk_s4
                filePath = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap); // actualizar la imagen directamente a la vista
                uploadImage(); // subir la imagen a Google Firebase Storage
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /*
    * Sube una imagen a Google Firebase Storage
    * Si ya tiene subida una imagen y se sube otra se sobreescribe
    */
    private void uploadImage(){
        if(filePath != null){
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(getResources().getString(R.string.uploading));
            dialog.show();

            FirebaseUser currentUser = auth.getCurrentUser();

            // Se crea una referencia a la ruta de acceso completa del archivo
            String fileName = "profile_" + currentUser.getUid() + ".jpg";
            StorageReference userProfile = storageRef.child("images/" + fileName);

            // Se sube la imagen
            userProfile.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.uploaded_final), Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    // trackear el proceso de subida de la imagen
                    Double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    dialog.setMessage(progress.toString() + "% " + getResources().getString(R.string.uploaded_progress));
                }
            });
        } else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.select_image_error), Toast.LENGTH_SHORT).show();
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
                    db.child("users").child(userAuth.getUid()).updateChildren(userValues);

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
        db.addValueEventListener(postListener);
    }

}