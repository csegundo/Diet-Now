package com.dietnow.app.ucm.fdi.DietNow;

import java.io.FileInputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DietNowApplication {

	protected static final String DATABASE_URL         = "https://diet-now-f650d-default-rtdb.europe-west1.firebasedatabase.app";
    protected static final String SERVICE_ACCOUNT_JSON = "diet-now-f650d-firebase-adminsdk-pgajx-db588d0dba.json";

	public static void main(String[] args) {
		try (FileInputStream serviceAccount = new FileInputStream("./" + DietNowApplication.SERVICE_ACCOUNT_JSON)) {
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl(DietNowApplication.DATABASE_URL).build();
			FirebaseApp.initializeApp(options);
			
			SpringApplication.run(DietNowApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
