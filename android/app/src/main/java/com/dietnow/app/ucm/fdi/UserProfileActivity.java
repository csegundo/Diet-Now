package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
//import com.anychart.sample.R;
import com.dietnow.app.ucm.fdi.adapters.AlimentViewOnlyAdapter;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.dietnow.app.ucm.fdi.service.DietService;
import com.dietnow.app.ucm.fdi.service.StepsService;
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
import com.google.type.DateTime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private Button change;
    private String uid;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageRef, imagesRef;
    private AnyChartView steps;
    private AnyChartView weights;
    private Button addStepsBtn;
    private CompoundButton selectorStepsWeight;

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
        addStepsBtn= findViewById(R.id.addStepsBtn);
        selectorStepsWeight= findViewById(R.id.selectorStepsWeight);

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
                uid= currentUser.getUid();
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

        // Acciones del perfil
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                builder.setTitle(R.string.profile_settings)
                        .setItems(R.array.profile_settings_options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0: logout(); break;
                                    case 1: editProfile(); break;
                                    case 2: deleteProfile(); break;
                                    default: break;
                                }
                            }
                        })
                        .setNegativeButton(R.string.delete_alert_no_opt, null).show();
            }
        });

        addStepsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                final NumberPicker numberPicker = new NumberPicker(UserProfileActivity.this);
                numberPicker.setMaxValue(100000);
                numberPicker.setMinValue(0);
                builder.setTitle(R.string.add_steps);
                builder.setMessage("Inserta el numero de pasos");
                builder.setView(numberPicker);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int num = numberPicker.getValue();
                        Date now = Calendar.getInstance().getTime();
                        FirebaseUser currentUser = auth.getCurrentUser();
                        //añadir en base de datos
                        Steps toCreate = StepsService.getInstance().parseSteps(num);
                        uploadStepsToFirebase(toCreate, currentUser.getUid());
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();

            }
        });

        // Graficas
        //generateWeightsChart();
        generateStepsChart();
        APIlib.getInstance().setActiveAnyChartView(steps);


        selectorStepsWeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked) {
                    generateStepsChart();
                   // weights.setVisibility(View.GONE);
                    //steps.setVisibility(View.VISIBLE);
                    APIlib.getInstance().setActiveAnyChartView(steps);

                    //generateStepsChart();
                }
                else{
                    generateWeightsChart();
                    //.setVisibility(View.GONE);
                    //weights.setVisibility(View.VISIBLE);
                    APIlib.getInstance().setActiveAnyChartView(weights);

                    //generateWeightsChart();
                }
            }
        });
    }
    private void uploadStepsToFirebase(Steps toCreate, String UserId){
        FirebaseUser currentUser = auth.getCurrentUser();
        String autoId = UserId ;

        // guardar los pasos
        db.child("pasos").child(autoId).child(toCreate.getDate()).setValue(toCreate.getSteps());


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
                // proceso explicativo https://www.youtube.com/watch?v=ZmgncLHk_s4
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

    private void deleteProfile() {
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

    private void logout(){
        auth.signOut();
        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void editProfile(){
        Intent intent = new Intent(UserProfileActivity.this, UserProfileEditActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }

    private void generateStepsChart(){
        steps      = findViewById(R.id.stepsChart);
        //APIlib.getInstance().setActiveAnyChartView(steps);
        db.child("pasos").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Cartesian cartesian = AnyChart.line();
                cartesian.animation(true);

                //cartesian.padding(10d, 20d, 5d, 20d);

                cartesian.crosshair().enabled(true);
                cartesian.crosshair()
                        .yLabel(true)
                        // TODO ystroke
                        .yStroke((Stroke) null, null, null, (String) null, (String) null);

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

                cartesian.title("Mis pasos");

                cartesian.yAxis(0).title("Numero de pasos");
                cartesian.xAxis(0).title("Dia");
                cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

                List<DataEntry> seriesData = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()){
                    String pasos = ds.getValue().toString();

                    String fecha = ds.getKey();
                    seriesData.add(new CustomDataEntry(fecha, Integer.valueOf(pasos)));
                }

                Set set = Set.instantiate();
                set.data(seriesData);
                Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

                Line series1 = cartesian.line(series1Mapping);
                series1.name("Progreso");
                series1.hovered().markers().enabled(true);
                series1.hovered().markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series1.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

                cartesian.legend().enabled(true);
                cartesian.legend().fontSize(13d);
                cartesian.legend().padding(0d, 0d, 10d, 0d);

                steps.setChart(cartesian);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void generateWeightsChart(){
        weights    = findViewById(R.id.weightsChart);

        //Descomentando la linea de abajo se mostrará la grafica de pesos pero se oculará la de pasos
        //APIlib.getInstance().setActiveAnyChartView(weights);
        //APIlib.getInstance().setActiveAnyChartView(weights);

        db.child("weights").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Cartesian cartesianWeight = AnyChart.line();
                cartesianWeight.animation(true);

                //cartesian.padding(10d, 20d, 5d, 20d);

                cartesianWeight.crosshair().enabled(true);
                cartesianWeight.crosshair()
                        .yLabel(true)
                        // TODO ystroke
                        .yStroke((Stroke) null, null, null, (String) null, (String) null);

                cartesianWeight.tooltip().positionMode(TooltipPositionMode.POINT);

                cartesianWeight.title("Mis pesos");

                cartesianWeight.yAxis(0).title("Peso en kg");
                cartesianWeight.xAxis(0).title("Dia");
                cartesianWeight.xAxis(0).labels().padding(2d, 2d, 2d, 2d);

                List<DataEntry> weightData = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()){
                    String peso = ds.getValue().toString();
                    String fecha = ds.getKey();
                    weightData.add(new CustomDataEntry(fecha, Integer.valueOf(peso)));
                }

                Set set = Set.instantiate();
                set.data(weightData);
                Mapping series = set.mapAs("{ x: 'x', value: 'value' }");

                Line series1 = cartesianWeight.line(series);
                series1.name("Progreso");
                series1.hovered().markers().enabled(true);
                series1.hovered().markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series1.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

                cartesianWeight.legend().enabled(true);
                cartesianWeight.legend().fontSize(13d);
                cartesianWeight.legend().padding(0d, 0d, 10d, 0d);

                weights.setChart(cartesianWeight);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value) {
            super(x, value);
        }
    }

}