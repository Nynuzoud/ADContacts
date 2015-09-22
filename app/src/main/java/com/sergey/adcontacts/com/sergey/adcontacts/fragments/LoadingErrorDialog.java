package com.sergey.adcontacts.com.sergey.adcontacts.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.sergey.adcontacts.R;

public class LoadingErrorDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.LoadingErrorText)
                .setTitle(R.string.errorText)
                .setPositiveButton(R.string.okText, null);
        return builder.create();
    }
}
