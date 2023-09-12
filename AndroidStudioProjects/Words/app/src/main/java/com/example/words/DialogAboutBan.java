package com.example.words;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DialogAboutBan extends DialogFragment {
    private static final String dateUnban = "date";

    public static DialogAboutBan newInstance(String date) {
        DialogAboutBan fragment = new DialogAboutBan();
        Bundle args = new Bundle();
        args.putString(dateUnban, date);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.user_was_banned);
        dialog.show();

        TextView textView = dialog.findViewById(R.id.infoDateUnbanTextView);
        textView.setText("Дата разблокировки: " + getArguments().getString(dateUnban));

        return dialog;
    }
}
