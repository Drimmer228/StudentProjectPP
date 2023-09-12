package com.example.words.MenuFragmentClass;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.words.ApiPackage.ApiInterface;
import com.example.words.ApiPackage.RequestBuilder;
import com.example.words.DataBaseClasses.CategoryWord;
import com.example.words.R;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeLevelsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_levels, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button goBackInMenu = view.findViewById(R.id.btBackInMenu);
        goBackInMenu.setOnClickListener(v -> {
            setHeaderSupportBar("Главное меню");
            setFragment(new MenuWithButtons());
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerWithCategory);

        ApiInterface apiInterface = RequestBuilder.buildRequest().create(ApiInterface.class);
        Call<ArrayList<CategoryWord>> getCategoryWordList = apiInterface.getCategoryWord();

        getCategoryWordList.enqueue(new Callback<ArrayList<CategoryWord>>() {
            @Override
            public void onResponse(Call<ArrayList<CategoryWord>> call, Response<ArrayList<CategoryWord>> response) {
                if(response.isSuccessful()){
                    ArrayList<CategoryWord> allCategoryWord = response.body();
                    if (allCategoryWord != null && !allCategoryWord.isEmpty()) {
                        CategoryWordAdapter categoryWordAdapter = new CategoryWordAdapter(allCategoryWord);
                        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                        recyclerView.setAdapter(categoryWordAdapter);
                    } else {
                        Toast.makeText(view.getContext(), "Список категорий пуст", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(view.getContext(), "Ошибка получения категорий", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CategoryWord>> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
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