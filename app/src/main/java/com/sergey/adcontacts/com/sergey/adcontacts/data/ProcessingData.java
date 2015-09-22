package com.sergey.adcontacts.com.sergey.adcontacts.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcessingData {

    public void updateData(Context context, ArrayList<HashMap<String, String>> contactsArrayList) {
        SQLDatabase sqlDatabase = new SQLDatabase(context);
        SQLiteDatabase db = sqlDatabase.getWritableDatabase();

        //Deleting DB
        db.execSQL("DROP TABLE IF EXISTS " + SQLDatabase.FeedEntry.TABLE_NAME);

        //Creating DB
        sqlDatabase.onCreate(db);

        //Writing values to DB
        if (contactsArrayList != null && contactsArrayList.size() != 0) {
            for (HashMap<String, String> mapEntry : contactsArrayList) {
                String phone = mapEntry.get("phone");
                String email = mapEntry.get("email");
                String FIO = mapEntry.get("FIO");
                String company = mapEntry.get("company");
                String title = mapEntry.get("title");

                ContentValues values = new ContentValues();
                values.put(SQLDatabase.FeedEntry.COLUMN_NAME_PHONE, phone);
                values.put(SQLDatabase.FeedEntry.COLUMN_NAME_EMAIL, email);
                values.put(SQLDatabase.FeedEntry.COLUMN_NAME_FIO, FIO);
                values.put(SQLDatabase.FeedEntry.COLUMN_NAME_COMPANY, company);
                values.put(SQLDatabase.FeedEntry.COLUMN_NAME_TITLE, title);

                //Inserting processed data to DB
                db.insert(SQLDatabase.FeedEntry.TABLE_NAME, null, values);
            }
        }
    }
}
