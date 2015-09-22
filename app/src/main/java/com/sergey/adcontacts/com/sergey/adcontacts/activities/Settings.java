package com.sergey.adcontacts.com.sergey.adcontacts.activities;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sergey.adcontacts.R;
import com.sergey.adcontacts.com.sergey.adcontacts.data.SQLDatabase;
import com.sergey.adcontacts.com.sergey.adcontacts.utils.Preferences;

public class Settings extends AppCompatActivity {

    //Shared preferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Init sharedPreferences
        sharedPreferences = getSharedPreferences(Preferences.PreferencesEntry.APP_PREFERENCES, Context.MODE_PRIVATE);

        //Init views
        Button resetSettingsButton = (Button) findViewById(R.id.buttonResetSettings);

        resetSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Deleting DB
                SQLDatabase sqlDatabase = new SQLDatabase(Settings.this);
                SQLiteDatabase db = sqlDatabase.getWritableDatabase();
                db.execSQL("DELETE FROM " + SQLDatabase.FeedEntry.TABLE_NAME);

                Toast.makeText(Settings.this, R.string.resetSettingsCompletedText, Toast.LENGTH_LONG).show();
            }
        });
    }
}
