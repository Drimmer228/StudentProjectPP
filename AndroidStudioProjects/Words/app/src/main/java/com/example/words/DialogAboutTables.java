package com.example.words;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.words.AdminFunctions.allCategoryWordFragment;
import com.example.words.AdminFunctions.allWordsFragment;

public class DialogAboutTables extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_change_dbtable);
        dialog.show();

        TextView textViewWords = dialog.findViewById(R.id.wordsTable);
        textViewWords.setOnClickListener(v->{
            setFragment(new allWordsFragment());
            dialog.dismiss();
        });

        TextView textViewCategoryWords = dialog.findViewById(R.id.categoryWordsTable);
        textViewCategoryWords.setOnClickListener(v->{
            setFragment(new allCategoryWordFragment());
            dialog.dismiss();
        });

        return dialog;
    }

    public void setFragment(Fragment fragment){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentLayoutMenu, fragment, null).commit();
    }
}
