package com.example.words.AdminFunctions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.words.DialogAboutTables;
import com.example.words.MenuFragmentClass.SettingFragment;
import com.example.words.R;

import java.util.Objects;

public class AdminFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tableDb = view.findViewById(R.id.btTables);
        tableDb.setOnClickListener(v->{
            setHeaderSupportBar("Таблицы");
            DialogAboutTables dialog = new DialogAboutTables();
            dialog.show(getChildFragmentManager(), "my_dialog");
        });

        TextView settings = view.findViewById(R.id.btSettings);
        settings.setOnClickListener(v ->{
            setHeaderSupportBar("Настройки");
            setFragment(new SettingFragment());
        });

        TextView usersList = view.findViewById(R.id.btUsers);
        usersList.setOnClickListener(v->{
            setHeaderSupportBar("Пользователи");
            setFragment(new UsersFragment());
        });
    }

    private void setHeaderSupportBar(String title){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle(title);
    }

    public void setFragment(Fragment fragment){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentLayoutMenu, fragment, null).commit();
    }
}