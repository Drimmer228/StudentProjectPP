package com.example.words.AdminFunctions;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.words.ApiPackage.ApiInterface;
import com.example.words.ApiPackage.RequestBuilder;
import com.example.words.DataBaseClasses.CategoryWord;
import com.example.words.DataBaseClasses.Dictionary;
import com.example.words.MenuFragmentClass.CategoryWordAdapter;
import com.example.words.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class allCategoryWordFragment extends Fragment implements OnCategoryWordItemClickListener{
    private ApiInterface apiInterface = RequestBuilder.buildRequest().create(ApiInterface.class);
    private View view;
    private CategoryWord categoryWord;

    @Override
    public void OnCategoryWordItemClick(CategoryWord categoryWord) {
        this.categoryWord = categoryWord;

        EditText editTextNameCategory = view.findViewById(R.id.editTextNameCategoryWord);
        editTextNameCategory.setText(categoryWord.getNameCategoryWord());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_category_word, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        getAllCategoryWord(view);
        setClickButtons(view);

        ImageView goBack = view.findViewById(R.id.goBackToAdminFunc);
        goBack.setOnClickListener(v-> setFragment(new AdminFragment()));
    }

    private void setClickButtons(View view) {
        View rootView = requireActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        TextView addCategory = view.findViewById(R.id.addCategoryWord);
        addCategory.setOnClickListener(v->{
            EditText editTextNameCategory = view.findViewById(R.id.editTextNameCategoryWord);
            String enterWord = editTextNameCategory.getText().toString().trim();
            CategoryWord addbleCategory = new CategoryWord(enterWord);
            addbleCategory.setNameCategoryWord(enterWord);
            Call<CategoryWord> call = apiInterface.addCategoryWords(addbleCategory);
            call.enqueue(new Callback<CategoryWord>() {
                @Override
                public void onResponse(Call<CategoryWord> call, Response<CategoryWord> response) {
                    if(response.isSuccessful()) {
                        Snackbar snackbar = Snackbar.make(rootView, "Новая категория добавлена", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        setFragment(new allCategoryWordFragment());
                    }
                }

                @Override
                public void onFailure(Call<CategoryWord> call, Throwable t) {
                }
            });
        });

        TextView updateCategory = view.findViewById(R.id.updateCategoryWord);
        updateCategory.setOnClickListener(v->{
            EditText editTextNameCategory = view.findViewById(R.id.editTextNameCategoryWord);
            String enterWord = editTextNameCategory.getText().toString().trim();
            CategoryWord updateCategoryWord = new CategoryWord(enterWord);
            updateCategoryWord.setIdCategoryWord(categoryWord.getIdCategoryWord());
            updateCategoryWord.setNameCategoryWord(enterWord);

            Call<CategoryWord> call = apiInterface.updateCategoryWords(categoryWord.getIdCategoryWord(), updateCategoryWord);
            call.enqueue(new Callback<CategoryWord>() {
                @Override
                public void onResponse(Call<CategoryWord> call, Response<CategoryWord> response) {
                    if(response.isSuccessful()) {
                        Snackbar snackbar = Snackbar.make(rootView, "Категория изменена", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        setFragment(new allCategoryWordFragment());
                    }
                }

                @Override
                public void onFailure(Call<CategoryWord> call, Throwable t) {
                }
            });
        });

        TextView deleteCategory = view.findViewById(R.id.deleteCategoryWord);
        deleteCategory.setOnClickListener(v->{
            Call<CategoryWord> call = apiInterface.deleteCategoryWords(categoryWord.getIdCategoryWord());
            call.enqueue(new Callback<CategoryWord>() {
                @Override
                public void onResponse(Call<CategoryWord> call, Response<CategoryWord> response) {
                    if(response.isSuccessful()) {
                        Snackbar snackbar = Snackbar.make(rootView, "Категория удалена", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        setFragment(new allCategoryWordFragment());
                    }
                }

                @Override
                public void onFailure(Call<CategoryWord> call, Throwable t) {
                }
            });
        });
    }

    private void getAllCategoryWord(View view) {
        ApiInterface apiInterface = RequestBuilder.buildRequest().create(ApiInterface.class);
        Call<ArrayList<CategoryWord>> getCategoryWordList = apiInterface.getCategoryWord();

        getCategoryWordList.enqueue(new Callback<ArrayList<CategoryWord>>() {
            @Override
            public void onResponse(Call<ArrayList<CategoryWord>> call, Response<ArrayList<CategoryWord>> response) {
                if(response.isSuccessful()){
                    ArrayList<CategoryWord> allCategoryWord = response.body();
                    if (allCategoryWord != null && !allCategoryWord.isEmpty()) {
                        RecyclerView recyclerView = view.findViewById(R.id.allCategoryWordsRecycler);
                        CategoryWordAdapterAll categoryWordAdapter = new CategoryWordAdapterAll(view.getContext(), allCategoryWord, allCategoryWordFragment.this);
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

    public void setFragment(Fragment fragment){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentLayoutMenu, fragment, null).commit();
    }
}