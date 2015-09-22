package com.sergey.adcontacts.com.sergey.adcontacts.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sergey.adcontacts.com.sergey.adcontacts.utils.Preferences;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import java.util.ArrayList;
import java.util.HashMap;

public class GetData {

    //Log tag
    private static String TAG = GetData.class.getSimpleName();

    public ArrayList<HashMap<String, String>> getContactsFromAD(Context context) throws LDAPException{
        //Initialize shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(Preferences.PreferencesEntry.APP_PREFERENCES, Context.MODE_PRIVATE);

        ArrayList<HashMap<String, String>> contactsArrayList;

        String login = sharedPreferences.getString(Preferences.PreferencesEntry.APP_PREFERENCES_LOGIN, "");
        String password = sharedPreferences.getString(Preferences.PreferencesEntry.APP_PREFERENCES_PASSWORD, "");

        //Connecting to LDAP server
        LDAPConnection ldapConnection = new LDAPConnection("192.168.173.1", 389, login + "@winserver.info", password);
        //Creating filter
        Filter filter = Filter.createNOTFilter(Filter.createEqualityFilter("title", ""));
        //Creating request
        SearchRequest searchRequest = new SearchRequest("OU=COMPANY,DC=winserver,DC=info", SearchScope.SUB, filter, "givenName", "sn",
                "mail", "company", "mobile", "title");

        //Getting result and creating ArrayList
        SearchResult searchResult = ldapConnection.search(searchRequest);
        contactsArrayList = new ArrayList<>();
        HashMap<String, String> contactMap;
        for (SearchResultEntry entry : searchResult.getSearchEntries()) {
            if (entry.hasAttribute("sn") && entry.hasAttribute("mobile")) {
                //Reseting HashMap
                contactMap = new HashMap<>();
                String firstName = entry.getAttributeValue("givenName");
                String secondName = entry.getAttributeValue("sn");
                String title = entry.getAttributeValue("title");
                String company = entry.getAttributeValue("company");
                String mobile = entry.getAttributeValue("mobile");
                String mail = entry.getAttributeValue("mail");

                Log.w(TAG, firstName + " " + secondName + "\nдолжность: " + title + "\nтелефон: " + mobile +  "\nэл.почта: " +
                        mail + "\nкомпания: " + company);

                //Putting to HashMap
                contactMap.put("phone", mobile);
                contactMap.put("email", mail);
                contactMap.put("FIO", secondName + " " + firstName);
                contactMap.put("company", company);
                contactMap.put("title", title);
                contactsArrayList.add(contactMap);
            }
        }

        return contactsArrayList;
    }

    public ArrayList<HashMap<String, String>> getContactsFromSQLite(Context context) throws CursorIndexOutOfBoundsException {
        SQLDatabase sqlDatabase = new SQLDatabase(context);
        SQLiteDatabase db = sqlDatabase.getReadableDatabase();

        ArrayList<HashMap<String, String>> contactsArrayList = new ArrayList<>();
        HashMap<String, String> contactMap;
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                SQLDatabase.FeedEntry.COLUMN_NAME_FIO,
                SQLDatabase.FeedEntry.COLUMN_NAME_EMAIL,
                SQLDatabase.FeedEntry.COLUMN_NAME_TITLE,
                SQLDatabase.FeedEntry.COLUMN_NAME_COMPANY,
                SQLDatabase.FeedEntry.COLUMN_NAME_PHONE
        };

        Cursor cursor = db.query(SQLDatabase.FeedEntry.TABLE_NAME, projection, null, null, null, null, null);
        cursor.moveToFirst();

        //Defining columns' indexes
        int FIOIndex = cursor.getColumnIndex(SQLDatabase.FeedEntry.COLUMN_NAME_FIO);
        int emailIndex = cursor.getColumnIndex(SQLDatabase.FeedEntry.COLUMN_NAME_EMAIL);
        int titleIndex = cursor.getColumnIndex(SQLDatabase.FeedEntry.COLUMN_NAME_TITLE);
        int companyIndex = cursor.getColumnIndex(SQLDatabase.FeedEntry.COLUMN_NAME_COMPANY);
        int phoneIndex = cursor.getColumnIndex(SQLDatabase.FeedEntry.COLUMN_NAME_PHONE);

        do {
            //Reseting HashMap
            contactMap = new HashMap<>();
            String FIO = cursor.getString(FIOIndex);
            String email = cursor.getString(emailIndex);
            String title = cursor.getString(titleIndex);
            String company = cursor.getString(companyIndex);
            String phone = cursor.getString(phoneIndex);

            contactMap.put("FIO", FIO);
            contactMap.put("email", email);
            contactMap.put("title", title);
            contactMap.put("company", company);
            contactMap.put("phone", phone);
            contactsArrayList.add(contactMap);
        } while (cursor.moveToNext());

        cursor.close();

        return contactsArrayList;
    }
}
