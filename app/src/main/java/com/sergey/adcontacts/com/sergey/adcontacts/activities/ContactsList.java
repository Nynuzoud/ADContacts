package com.sergey.adcontacts.com.sergey.adcontacts.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sergey.adcontacts.R;
import com.sergey.adcontacts.com.sergey.adcontacts.adapters.ContactsAdapter;
import com.sergey.adcontacts.com.sergey.adcontacts.data.GetData;
import com.sergey.adcontacts.com.sergey.adcontacts.data.ProcessingData;
import com.sergey.adcontacts.com.sergey.adcontacts.fragments.LoadingErrorDialog;
import com.sergey.adcontacts.com.sergey.adcontacts.fragments.NetworkErrorDialog;
import com.sergey.adcontacts.com.sergey.adcontacts.utils.NetworkUtils;
import com.sergey.adcontacts.com.sergey.adcontacts.utils.Preferences;
import com.unboundid.ldap.sdk.LDAPException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ContactsList extends AppCompatActivity {
    //Shared preferences
    private SharedPreferences sharedPreferences;

    //Initialize classes
    private GetData getData = new GetData();
    private NetworkUtils networkUtils = new NetworkUtils();
    private ProcessingData processingData = new ProcessingData();

    //View variables
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView contactsListView;

    private ArrayList<HashMap<String, String>> contactsArrayList;
    private String login;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        //Initialize Views
        progressBar = (ProgressBar) findViewById(R.id.contactsProgressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.contactsSwipeRefreshLayout);
        contactsListView = (ListView) findViewById(R.id.contactsListView);

        //Initialize shared preferences
        sharedPreferences = getSharedPreferences(Preferences.PreferencesEntry.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (!sharedPreferences.getBoolean(Preferences.PreferencesEntry.APP_PREFERENCES_HAS_SETTINGS, false)) {
            //Initialize setup dialog
            setupDialog();
        } else {
            //Initialize contacts
            contactsArrayList = null;
            contacts();
        }
    }

    private void setupDialog() {
        AlertDialog.Builder setupDialog = new AlertDialog.Builder(ContactsList.this);
        LayoutInflater layoutInflater = ContactsList.this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.setup_dialog, null);
        final EditText loginEditText = (EditText) layout.findViewById(R.id.editTextLogin);
        final EditText passwordEditText = (EditText) layout.findViewById(R.id.editTextPassword);
        setupDialog.setTitle(R.string.setupLoginPasswordTitle)
                    .setView(layout)
                    .setPositiveButton(R.string.okText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            login = loginEditText.getText().toString();
                            password = passwordEditText.getText().toString();

                            //Saving login and password
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(Preferences.PreferencesEntry.APP_PREFERENCES_HAS_SETTINGS, true);
                            editor.putString(Preferences.PreferencesEntry.APP_PREFERENCES_LOGIN, login);
                            editor.putString(Preferences.PreferencesEntry.APP_PREFERENCES_PASSWORD, password);
                            editor.apply();

                            //Initialize contacts
                            contactsArrayList = null;
                            contacts();
                        }
                    })
                    .show();
        setupDialog.setCancelable(false);
    }

    private void contacts() {
        processingContactsData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                processingContactsData();
            }
        });

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Getting data for a contact from contactsArrayList
                HashMap<String, String> contactMap = contactsArrayList.get(position);
                String FIO = contactMap.get("FIO");
                String title = contactMap.get("title");
                String phone = contactMap.get("phone");
                String email = contactMap.get("email");
                String company = contactMap.get("company");

                //Creating intent
                Intent contactIntent = new Intent(ContactsList.this, ContactInfo.class);
                contactIntent.putExtra("FIO", FIO);
                contactIntent.putExtra("title", title);
                contactIntent.putExtra("phone", phone);
                contactIntent.putExtra("email", email);
                contactIntent.putExtra("company", company);

                //Starting new activity
                startActivity(contactIntent);
            }
        });
    }

    private void processingContactsData() {
        AsyncTask<Void, Void, Void> getContactsTask = new AsyncTask<Void, Void, Void>() {
            Boolean isError = false;
            Boolean hasPreferences = true;
            Boolean isLoadedFromSQLite = false;

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(!isError) {
                    setDataToAdapter();
                } else if (!networkUtils.isNetworkAvailable(ContactsList.this)){
                    if(isLoadedFromSQLite) {
                        setDataToAdapter();
                        Toast.makeText(ContactsList.this, "Данные загружены из локальной БД", Toast.LENGTH_LONG).show();
                    } else {
                        new NetworkErrorDialog().show(getFragmentManager(), "NetworkError");
                        progressBar.setVisibility(View.GONE);
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                } else if(isLoadedFromSQLite) {
                    setDataToAdapter();
                    Toast.makeText(ContactsList.this, "Данные загружены из локальной БД", Toast.LENGTH_LONG).show();
                } else if (!hasPreferences) {
                    setupDialog();
                } else {
                    new LoadingErrorDialog().show(getFragmentManager(), "LoadingError");
                    progressBar.setVisibility(View.GONE);
                    if(swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                if(networkUtils.isNetworkAvailable(ContactsList.this)) {
                    try {
                        contactsArrayList = getData.getContactsFromAD(ContactsList.this);
                        processingData.updateData(ContactsList.this, contactsArrayList);
                    } catch (LDAPException e) {
                        e.printStackTrace();
                        if(!sharedPreferences.getBoolean(Preferences.PreferencesEntry.APP_PREFERENCES_HAS_SETTINGS, false)) {
                            hasPreferences = false;
                            isError = true;
                        } else {
                            try {
                                contactsArrayList = getData.getContactsFromSQLite(ContactsList.this);
                                if(contactsArrayList != null && contactsArrayList.size() != 0) {
                                    isLoadedFromSQLite = true;
                                    isError = true;
                                }
                            } catch (CursorIndexOutOfBoundsException ce) {
                                ce.printStackTrace();
                                isError = true;
                            }
                        }
                    }
                } else {
                    try {
                        contactsArrayList = getData.getContactsFromSQLite(ContactsList.this);
                        if(contactsArrayList != null && contactsArrayList.size() != 0) {
                            isLoadedFromSQLite = true;
                            isError = true;
                        }
                    } catch (CursorIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        isError = true;
                    }
                }
                return null;
            }
        };
        getContactsTask.execute();
    }

    private void setDataToAdapter() {
        if(contactsArrayList != null && contactsArrayList.size() != 0) {
            Comparator<HashMap<String, String>> comparator = new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                    return lhs.get("FIO").compareTo(rhs.get("FIO"));
                }
            };
            Collections.sort(contactsArrayList, comparator);
            ContactsAdapter contactsAdapter = new ContactsAdapter(ContactsList.this, contactsArrayList, R.layout.contact_row, new String[]{"FIO"}, new int[]{R.id.CONTACTS_NAME_CELL});
            contactsListView.setAdapter(contactsAdapter);
            if(swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(ContactsList.this, Settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
