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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.dietnow.app.ucm.fdi.service.StepsService;
import com.dietnow.app.ucm.fdi.service.WeightService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.dietnow.app.ucm.fdi.model.user.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

    private TextView name, age, email, date;
    private FloatingActionButton addProfile;
    private ImageView image;
    private Uri filePath;
    private Button change, current_diet_graphic;
    private String uid;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageRef, imagesRef;
    private AnyChartView steps;
    private AnyChartView weights;

    private CompoundButton selectorStepsWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // inicializar los elementos
        addProfile = findViewById(R.id.addProfile);
        name       = findViewById(R.id.profileName);
        age        = findViewById(R.id.profileAge);
        email      = findViewById(R.id.profileEmail);
        date       = findViewById(R.id.profileDate);
        image      = (ImageView) findViewById(R.id.profileImage);
        change     = findViewById(R.id.profileChangeImg);
        selectorStepsWeight  = findViewById(R.id.selectorStepsWeight);
        current_diet_graphic = findViewById(R.id.current_diet_history);

        // inicializar Google Firebase
        auth       = FirebaseAuth.getInstance();
        db         = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        imagesRef  = storageRef.child("images");

        FirebaseUser currentUser = auth.getCurrentUser();
        this.uid = currentUser.getUid();

        // Cambiar la imagen del usuario: gallery/docs
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromPhone();
            }
        });

        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                builder.setTitle(R.string.profile_add)
                    .setItems(R.array.profile_settings_add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0: AddStep(); break;
                                case 1: AddWeight(); break;
                                default: break;
                            }
                        }
                    })
                    .setNegativeButton(R.string.delete_alert_no_opt, null).show();
            }
        });

        selectorStepsWeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    generateStepsChart();
                    APIlib.getInstance().setActiveAnyChartView(steps);
                } else{
                    generateWeightsChart();
                    APIlib.getInstance().setActiveAnyChartView(weights);
                }
            }
        });
        selectorStepsWeight.setChecked(true);

        current_diet_graphic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, CurrentDietGraphic.class);
                startActivity(intent);
            }
        });

        /**
         * Se ejecuta el onDataChange():
         *      cuando se entra a la vista
         *      cuando un admin edita el perfil del usuario logeado
         */
        db.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setProfileImage();
                getSupportActionBar().setTitle(R.string.label_user_view_profile);

                User u = snapshot.getValue(User.class);
                name.setText(u.getName() + " " + u.getLastname());
                email.setText(u.getEmail());
                age.setText(u.getAge().toString() + " " + getResources().getString(R.string.years));
                date.setText(getResources().getString(R.string.createdAccount) + " " + u.getStart_date());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OnFailureUserProfile: ",error.toString());
            }
        });
    }

    private void setProfileImage(){
        storageRef = FirebaseStorage.getInstance().getReference(); // crear una instancia a la referencia del almacenamiento
        String fileName = "profile_" + uid + ".jpg";
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

    private void uploadStepsToFirebase(Steps toCreate, String UserId){
        String autoId = UserId ;

        // guardar los pasos
        db.child("pasos").child(autoId).child(toCreate.getDate()).setValue(toCreate.getSteps());
    }

    private void uploadWeightToFirebase(Weight toCreate, String UserId){
        String autoId = UserId ;

        // guardar los pasos
        db.child("weights").child(autoId).child(toCreate.getDate()).setValue(toCreate.getWeight());
    }

    /* START: Acciones del perfil */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.DietHistory:
                DietHistory();
                break;
            case R.id.logoutMenu:
                logout();
                break;
            case R.id.editProfileMenu:
                editProfile();
                break;
            case R.id.deleteProfileMenu:
                deleteProfile();
                break;
        }
        return true;
    }

    private void DietHistory(){
        Intent intent = new Intent(UserProfileActivity.this, DietHistory.class);
        ArrayList<Diet> array = new ArrayList<>();
        db.child("diets").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot ds2 : task.getResult().getChildren()) {
                    String titulo = ds2.child("title").getValue().toString();
                    HashMap<String, Boolean> visit = ds2.child("visits").getValue(new GenericTypeIndicator<HashMap<String, Boolean>>(){});
                    HashMap<String, Boolean> rating = ds2.child("rating").getValue(new GenericTypeIndicator<HashMap<String, Boolean>>(){});
                    Boolean active = ds2.child("active").getValue(Boolean.class);
                    String descripcion = ds2.child("description").getValue().toString();
                    boolean published = ds2.child("published").getValue(Boolean.class);
                    String id = ds2.getKey();
                    Diet us = new Diet(descripcion, titulo, visit, rating);
                    us.setId(ds2.child("id").getValue().toString());
                    array.add(us);
                }

                intent.putExtra("Dietas", array);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailureUserProfile: ","");
                e.printStackTrace();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return true;
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
                filePath = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
                uploadImage();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

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
        FirebaseUser authUser = auth.getCurrentUser();

        if(authUser == null){
            Toast.makeText(getApplicationContext(),
                    "El usuario no se ha borrado correctamente",
                    Toast.LENGTH_LONG).show();
        } else{
            db.child("users").child(authUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    User user = task.getResult().getValue(User.class);
                    user.setActive(false);
                    Map<String, Object> userValues = user.toMap();
                    db.child("users").child(authUser.getUid()).updateChildren(userValues);

                    // cierra sesion y vuelve al login
                    logout();
                }
            });
        }
    }

    private void logout(){
        auth.signOut();
        /**
         * Eliminar todas las actividades anteriores y empezar una nueva
         * https://localcoder.org/android-kill-all-activities-on-logout
         */
        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void editProfile(){
        Intent intent = new Intent(UserProfileActivity.this, UserProfileEditActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }

    private void AddStep() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-M-y");
        String today = dateFormat.format(new Date());
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        final NumberPicker numberPicker = new NumberPicker(UserProfileActivity.this);
        numberPicker.setMaxValue(100000);
        numberPicker.setMinValue(0);
        builder.setTitle(R.string.add_steps);
        builder.setMessage(getResources().getString(R.string.add_steps_label) + " " + today);
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

    private void AddWeight() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-M-y");
        String today = dateFormat.format(new Date());
        LinearLayout LL = new LinearLayout(UserProfileActivity.this);

        final NumberPicker integerWeight = new NumberPicker(UserProfileActivity.this);
        integerWeight.setMaxValue(200);
        integerWeight.setMinValue(0);

        final NumberPicker decimalWeight = new NumberPicker(UserProfileActivity.this);
        decimalWeight.setMaxValue(99);
        decimalWeight.setMinValue(0);

        final TextView separator =new TextView(UserProfileActivity.this);
        separator.setText(R.string.local_separator);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(60, 60);
        params.gravity= Gravity.CENTER;


        LinearLayout.LayoutParams numPicerParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.weight = 50;


        LinearLayout.LayoutParams qPicerParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        qPicerParams.weight = 50;

        final TextView meassure =new TextView(UserProfileActivity.this);
        meassure.setText("Kg");


        LL.setLayoutParams(params);
        LL.addView(integerWeight,numPicerParams);
        LL.addView(separator,params);
        LL.addView(decimalWeight,qPicerParams);
        LL.addView(meassure,params);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle(getResources().getString(R.string.add_weight));
        builder.setMessage(getResources().getString(R.string.add_weight_label) + " " + today);
        builder.setView(LL);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                double num = Double.parseDouble(""+integerWeight.getValue() + "." + decimalWeight.getValue());
                Date now = Calendar.getInstance().getTime();
                FirebaseUser currentUser = auth.getCurrentUser();
                //añadir en base de datos
                Weight toCreate = WeightService.getInstance().parseWeight(num);
                uploadWeightToFirebase(toCreate, currentUser.getUid());
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

    private void generateStepsChart(){
        steps = findViewById(R.id.dietchart);
        db.child("pasos").child(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Cartesian cartesian = AnyChart.line();
                cartesian.animation(true);

                cartesian.crosshair().enabled(true);
                cartesian.crosshair()
                        .yLabel(true)
                        // TODO ystroke
                        .yStroke((Stroke) null, null, null, (String) null, (String) null);

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

                cartesian.title(getResources().getString(R.string.chart_my_steps));

                cartesian.yAxis(0).title(getResources().getString(R.string.steps));
                cartesian.xAxis(0).title(getResources().getString(R.string.day));
                cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

                List<DataEntry> seriesData = new ArrayList<>();

                for(DataSnapshot ds : task.getResult().getChildren()){
                    String pasos = ds.getValue().toString();

                    String fecha = ds.getKey();
                    seriesData.add(new CustomDataEntry(fecha, Integer.valueOf(pasos)));
                }

                Set set = Set.instantiate();
                set.data(seriesData);
                Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

                Line series1 = cartesian.line(series1Mapping);
                series1.name(getResources().getString(R.string.progress));
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
                steps.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailureUserProfile: ","");
                e.printStackTrace();
            }
        });

    }

    private void generateWeightsChart(){
        weights = findViewById(R.id.weightsChart);
        db.child("weights").child(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Cartesian cartesianWeight = AnyChart.line();
                cartesianWeight.animation(true);

                cartesianWeight.crosshair().enabled(true);
                cartesianWeight.crosshair()
                        .yLabel(true)
                        // TODO ystroke
                        .yStroke((Stroke) null, null, null, (String) null, (String) null);

                cartesianWeight.tooltip().positionMode(TooltipPositionMode.POINT);

                cartesianWeight.title(getResources().getString(R.string.chart_my_weights));

                cartesianWeight.yAxis(0).title(getResources().getString(R.string.weights) + " (kg)");
                cartesianWeight.xAxis(0).title(getResources().getString(R.string.day));
                cartesianWeight.xAxis(0).labels().padding(2d, 2d, 2d, 2d);

                List<DataEntry> weightData = new ArrayList<>();

                for(DataSnapshot ds : task.getResult().getChildren()){
                    String peso = ds.getValue().toString();
                    String fecha = ds.getKey();
                    weightData.add(new CustomDataEntry(fecha, Double.parseDouble(peso)));
                }

                Set set = Set.instantiate();
                set.data(weightData);
                Mapping series = set.mapAs("{ x: 'x', value: 'value' }");

                Line series1 = cartesianWeight.line(series);
                series1.name(getResources().getString(R.string.progress));
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
                weights.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailureUserProfileW: ","");
                e.printStackTrace();
            }
        });

    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value) {
            super(x, value);
        }
    }

}