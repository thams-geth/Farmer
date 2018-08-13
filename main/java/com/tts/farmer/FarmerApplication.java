package com.tts.farmer;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class FarmerApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();


        /* Picasso */
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
