package com.techpakka.whatsappstickerspack.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

public class MessageDialogFragment extends DialogFragment {
    private static final String ARG_TITLE_ID = "title_id";
    private static final String ARG_MESSAGE = "message";

    public static DialogFragment newInstance(@StringRes int titleId, String message) {
        DialogFragment fragment = new MessageDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_TITLE_ID, titleId);
        arguments.putString(ARG_MESSAGE, message);
        fragment.setArguments(arguments);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @StringRes final int title = getArguments().getInt(ARG_TITLE_ID);
        String message = getArguments().getString(ARG_MESSAGE);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dismiss());

        if (title != 0) {
            dialogBuilder.setTitle(title);
        }
        return dialogBuilder.create();
    }
}
