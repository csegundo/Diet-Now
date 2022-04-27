package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dietnow.app.ucm.fdi.adapters.AlimentsAdapter;
import com.dietnow.app.ucm.fdi.adapters.AllUsersAdapter;
import com.dietnow.app.ucm.fdi.adapters.DietDocsAdapter;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.dietnow.app.ucm.fdi.model.user.User;
import com.dietnow.app.ucm.fdi.service.DietService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class CreateDietActivity extends AppCompatActivity {

    // Necesario para saber cuando el usuario ya ha elegido una imagen de la galeria
    private static final Integer PICK_DOC = 1;
    private Uri filePath;

    private EditText title, description;
    private Button create, upload;
    private ProgressBar progress;
    private String actualDiet;
    private FloatingActionButton addFood;
    private TextView alimentsLabel, uDietId ,documents_label;
    private Boolean description_inserted = false;
    private Boolean title_inserted = false;

    private FirebaseAuth auth;
    private DatabaseReference db;
    private StorageReference storageRef, docsRef;

    //adapters
    private AlimentsAdapter AlimentsAdapter;
    private RecyclerView RecyclerView;
    private RecyclerView DocsRecyclerView;
    private ArrayList<Aliment> alimentList;
    private ArrayList<Pair<String, String>> docList;
    private DietDocsAdapter docsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diet);

        actualDiet = null;
        if(getIntent().getExtras() != null){
            actualDiet = getIntent().getExtras().getString("did");
        }

        // Inicializar componentes de Firebase
        auth        = FirebaseAuth.getInstance();
        db          = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        alimentList = new ArrayList<Aliment> ();
        storageRef = FirebaseStorage.getInstance().getReference();
        docsRef    = storageRef.child("diets");

        // Inicializar los componentes de la vista
        title       = findViewById(R.id.createDietTitle);
        create      = findViewById(R.id.createDietBtn);
        progress    = findViewById(R.id.createDietProgress);
        description = findViewById(R.id.createDietDescription);
        addFood     = findViewById(R.id.addFood);
        alimentsLabel = findViewById(R.id.dietAlimentsLabel);
        documents_label = findViewById(R.id.documents_label);
        RecyclerView    = findViewById(R.id.AllAlimentsRecycler);
        DocsRecyclerView    = findViewById(R.id.AllDocsRecycler);
        upload      = findViewById(R.id.btnUploadDoc);
        uDietId     = findViewById(R.id.uDietId);

        docList = new ArrayList<Pair<String, String>>();
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DocsRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        if(actualDiet == null){
            upload.setVisibility(View.GONE);
            addFood.setVisibility(View.GONE);
            alimentsLabel.setVisibility(View.GONE);
            DocsRecyclerView.setVisibility(View.GONE);
            documents_label.setVisibility(View.GONE);
        } else{
            getDiet();
            upload.setVisibility(View.VISIBLE);
            DocsRecyclerView.setVisibility(View.VISIBLE);
            docsRef = storageRef.child("diets").child(actualDiet); // referencia exclusivamente para docs de dietas (nivel mas bajo)
        }

        isEditOrCreateDiet(actualDiet);



        // Acciones de los componentes
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);

                Diet toCreate = DietService.getInstance().parseDiet(
                    title.getText().toString(),
                    description.getText().toString(),
                    new HashMap<String, Boolean>(), new HashMap<String, Boolean>(),
                    0.0, true, false
                );
                uploadDietToFirebase(toCreate, actualDiet);
            }
        });

        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateDietActivity.this);
                builder.setTitle(R.string.add_food_message)
                    .setItems(R.array.add_food_options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(CreateDietActivity.this,
                                    which == 0 ? CameraActivity.class : AddManualFood.class);
                            intent.putExtra("did", actualDiet);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.delete_alert_no_opt, null).show();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDocFromAndroid();
            }
        });
    }


    private void getDiet(){
        db.child("diets").child(actualDiet).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alimentList.clear();
                Diet d = snapshot.getValue(Diet.class);
                title.setText(d.getTitle());
                description.setText(d.getDescription());
                for(DataSnapshot ds : snapshot.child("aliments").getChildren() ){
                    Aliment toAdd = ds.getValue(Aliment.class);
                    toAdd.setId(ds.getKey());
                    alimentList.add(toAdd);
                }
                AlimentsAdapter = new AlimentsAdapter(alimentList,CreateDietActivity.this,actualDiet);
                RecyclerView.setAdapter(AlimentsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void isEditOrCreateDiet(String dietId){
        if(dietId != null){
            db.child("diets").child(dietId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    Diet actual = task.getResult().getValue(Diet.class);
                    title.setText(actual.getTitle());
                    description.setText(actual.getDescription());
                    uDietId.setText(actual.getUser());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("OnFailureCreateDiet: ","");
                    e.printStackTrace();
                }
            });

            storageRef.child("diets/" + this.actualDiet).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference item : listResult.getItems()) {
                        String docUrl = item.toString().replace("gs://", "");
                        docUrl = "https://firebasestorage.googleapis.com/v0/b/" + item.getBucket();
                        docUrl += "/o/diets%2F" + actualDiet + "%2F" + item.getName(); // la ruta [/diets/<id>/<name>.pdf] encodea las '/' == '%2F'
                        docUrl += "?alt=media"; // añadir este parametro para que se visualice el PDF
                        // si se necesita permisos para leer (en nuestras reglas no es el caso) habria que poner otro parametro "token"
                        Pair<String, String> doc = new Pair<>(item.getName(), docUrl);
                        docList.add(doc);
                    }
                    docsAdapter = new DietDocsAdapter(docList, CreateDietActivity.this, actualDiet, true);
                    DocsRecyclerView.setAdapter(docsAdapter);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("OnFailureListDocs: ","");
                    e.printStackTrace();
                }
            });
        }
    }

    // guardar la dieta en dieta y usuarios (dentro del callback para que si falla no haya que borrar la dieta)
    private void uploadDietToFirebase(Diet toCreate, String dietId){
        FirebaseUser currentUser = auth.getCurrentUser();
        System.out.println(toCreate.getTitle().length());
        System.out.println(toCreate.getDescription().length());
        System.out.println(toCreate.toString());
        if(toCreate.getTitle().length()==0 && toCreate.getTitle().isEmpty() && toCreate.getDescription().length()==0 && toCreate.getDescription().isEmpty()){
            Toast.makeText(getApplicationContext(), getResources().getString(com.dietnow.app.ucm.fdi.R.string.create_diet_empty_values), Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
            return;
        }else{
            String autoId = dietId != null ? dietId : db.child("diets").push().getKey();
            String uId = dietId == null ? currentUser.getUid() : uDietId.getText().toString();
            toCreate.setId(autoId);
            toCreate.setUser(uId);

        // crear dieta
            if(actualDiet == null){
                db.child("diets").child(autoId).setValue(toCreate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
            } else{
                // editar dieta
                progress.setVisibility(View.GONE);
                db.child("diets").child(autoId).child("title").setValue(title.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        db.child("diets").child(autoId).child("description").setValue(description.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                update_and_refresh();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("OnFailureCreateDiet: ","");
                                e.printStackTrace();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("OnFailureCreateDiet: ","");
                        e.printStackTrace();
                    }
                });

            }
        }
    }

    private void update_and_refresh(){
        finish();
    }


    // this function is triggered when user selects the image from the imageChooser
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_DOC && resultCode == Activity.RESULT_OK) {
            if(data == null){
                return;
            }
            try{
                filePath = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadDocument(); // subir la imagen a Google Firebase Storage
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }



    private void uploadDocument(){
        if(filePath != null){
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(getResources().getString(R.string.uploading));
            dialog.show();

            String fileHash = "";
            try {
                fileHash = filePath.toString();
                MessageDigest md5Digest = MessageDigest.getInstance("MD5");
                md5Digest.update(fileHash.getBytes());
                byte[] digest = md5Digest.digest();
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < digest.length; i++) {
                    hexString.append(Integer.toHexString(0xFF & digest[i]));
                }
                fileHash = hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            String fileName = fileHash + ".pdf";
            StorageReference dietDoc = docsRef.child(fileName);

            dietDoc.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.uploaded_final_doc), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.select_doc_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void getDocFromAndroid(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.putExtra("crop", "true");
        intent.putExtra("return-data", true);

        Intent chooserIntent = Intent.createChooser(intent, getResources().getString(R.string.select_image));

        startActivityForResult(chooserIntent, PICK_DOC);
    }
}