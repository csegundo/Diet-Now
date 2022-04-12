package com.dietnow.app.ucm.fdi.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.StrictMode;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.R;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.NutritionalInfo;
import com.dietnow.app.ucm.fdi.utils.GetAllProductInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DietDocsAdapter extends RecyclerView.Adapter<DietDocsAdapter.ViewHolder> {
    private ArrayList<Pair<String, String>> localDataSet;
    private ArrayList<Pair<String, String>> allDocs;
    private Context context;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private String diet_id;

    public DietDocsAdapter(ArrayList<Pair<String, String>> dataSet, Context context, String diet_id) {
        this.diet_id = diet_id;
        localDataSet = dataSet;
        allDocs = new ArrayList<>();
        allDocs.addAll(localDataSet);
        this.context = context;
        db = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
    }


    @Override
    public DietDocsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.document_view_item, viewGroup, false);
        return new DietDocsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DietDocsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String docUrl = localDataSet.get(position).second;
        holder.docName.setText(localDataSet.get(position).first);
        holder.see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(docUrl));
                context.startActivity(browserIntent);
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                new DownloadFileFromURL().execute(docUrl);
            }
        });

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView docName;
        private final ImageButton see;
        private final ImageButton download;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            docName =  view.findViewById(R.id.docName);
            see =  view.findViewById(R.id.viewDoc);
            download =  view.findViewById(R.id.downlaodDoc);

        }
    }
}
