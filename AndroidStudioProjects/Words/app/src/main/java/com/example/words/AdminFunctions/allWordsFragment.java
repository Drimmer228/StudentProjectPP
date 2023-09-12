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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.words.ApiPackage.ApiInterface;
import com.example.words.ApiPackage.RequestBuilder;
import com.example.words.DataBaseClasses.CategoryWord;
import com.example.words.DataBaseClasses.Dictionary;
import com.example.words.MenuFragmentClass.CategoryWordAdapter;
import com.example.words.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.Distribution;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class allWordsFragment extends Fragment implements OnWordItemClickListener{
    private ApiInterface apiInterface = RequestBuilder.buildRequest().create(ApiInterface.class);
    private View view;
    private Dictionary dictionary;
    private String nameCategoryWord;

    @Override
    public void onWordItemClick(Dictionary dictionary) {
        EditText editTextNameWord = view.findViewById(R.id.editTextNameWord);
        EditText editTextAmountLetter = view.findViewById(R.id.editTextAmountLetter);
        EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        Spinner spinnerTextCategoryWord = view.findViewById(R.id.editTextCategoryWord);

        this.dictionary = dictionary;

        editTextNameWord.setText(dictionary.getNameWord());
        editTextAmountLetter.setText(String.valueOf(dictionary.getAmountLetter()));
        editTextDescription.setText(dictionary.getDescriptionWord());

        Call<CategoryWord> categoryWordCall = apiInterface.getCategoryWordById(dictionary.getCategoryWordId());
        categoryWordCall.enqueue(new Callback<CategoryWord>() {
            @Override
            public void onResponse(Call<CategoryWord> call, Response<CategoryWord> response) {
                if(response.isSuccessful()){
                    CategoryWord categoryWord = response.body();
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerTextCategoryWord.getAdapter();
                    List<String> values = new ArrayList<>();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        values.add(adapter.getItem(i));
                    }
                    int position = values.indexOf(categoryWord.getNameCategoryWord());
                    spinnerTextCategoryWord.setSelection(position);
                }
            }

            @Override
            public void onFailure(Call<CategoryWord> call, Throwable t) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        getAllWords(view);
        setSpinnerValues(view);
        setClickOnButtons(view);
    }

    private void setSpinnerValues(View view) {
        Spinner spinnerTextCategoryWord = view.findViewById(R.id.editTextCategoryWord);

        spinnerTextCategoryWord.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                nameCategoryWord = (String) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // обработка события, когда ничего не выбрано
            }
        });

        ApiInterface apiInterface = RequestBuilder.buildRequest().create(ApiInterface.class);
        Call<ArrayList<CategoryWord>> getCategoryWordList = apiInterface.getCategoryWord();

        getCategoryWordList.enqueue(new Callback<ArrayList<CategoryWord>>() {
            @Override
            public void onResponse(Call<ArrayList<CategoryWord>> call, Response<ArrayList<CategoryWord>> response) {
                if(response.isSuccessful()){
                    ArrayList<CategoryWord> allCategoryWord = response.body();
                    if (allCategoryWord != null && !allCategoryWord.isEmpty()) {
                        List<String> names = new ArrayList<>();
                        for(CategoryWord categoryWord : allCategoryWord){
                            names.add(categoryWord.getNameCategoryWord());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, names);
                        spinnerTextCategoryWord.setAdapter(adapter);
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

    private void setClickOnButtons(View view) {
        View rootView = requireActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        EditText editTextNameWord = view.findViewById(R.id.editTextNameWord);
        EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        TextView addWord = view.findViewById(R.id.addWord);

        addWord.setOnClickListener(v->{
            Dictionary dictionary = new Dictionary();
            dictionary.setNameWord(editTextNameWord.getText().toString().trim());
            dictionary.setDescriptionWord(editTextDescription.getText().toString().trim());

            Call<CategoryWord> findIdCategoryWord = apiInterface.getIdCategoryWord(nameCategoryWord);
            findIdCategoryWord.enqueue(new Callback<CategoryWord>() {
                @Override
                public void onResponse(Call<CategoryWord> call, Response<CategoryWord> response) {
                    if(response.isSuccessful()){
                        CategoryWord categoryWord = response.body();
                        dictionary.setCategoryWordId(categoryWord.getIdCategoryWord());
                        Call<Dictionary> dictionaryCall = apiInterface.addDictionary(dictionary);
                        dictionaryCall.enqueue(new Callback<Dictionary>() {
                            @Override
                            public void onResponse(Call<Dictionary> call, Response<Dictionary> response) {
                                if (response.isSuccessful()){
                                    Snackbar snackbar = Snackbar.make(rootView, "Новое слово добавлено", Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                    setFragment(new allWordsFragment());
                                }
                            }

                            @Override
                            public void onFailure(Call<Dictionary> call, Throwable t) {

                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<CategoryWord> call, Throwable t) {
                }
            });
        });

        TextView updateWord = view.findViewById(R.id.updateWord);
        updateWord.setOnClickListener(v->{
            if(dictionary != null){
                Dictionary dictionaryUpdate = new Dictionary();
                dictionaryUpdate.setIdDictionary(dictionary.getIdDictionary());
                dictionaryUpdate.setNameWord(editTextNameWord.getText().toString().trim());
                dictionaryUpdate.setDescriptionWord(editTextDescription.getText().toString().trim());
                dictionaryUpdate.setAmountLetter(0);

                Call<CategoryWord> findIdCategoryWord = apiInterface.getIdCategoryWord(nameCategoryWord);
                findIdCategoryWord.enqueue(new Callback<CategoryWord>() {
                    @Override
                    public void onResponse(Call<CategoryWord> call, Response<CategoryWord> response) {
                        if(response.isSuccessful()){
                            CategoryWord categoryWord = response.body();
                            dictionaryUpdate.setCategoryWordId(categoryWord.getIdCategoryWord());
                            Call<Dictionary> dictionaryCall = apiInterface.updateDictionary(dictionaryUpdate.getIdDictionary(), dictionaryUpdate);
                            dictionaryCall.enqueue(new Callback<Dictionary>() {
                                @Override
                                public void onResponse(Call<Dictionary> call, Response<Dictionary> response) {
                                    if (response.isSuccessful()){
                                        Snackbar snackbar = Snackbar.make(rootView, "Слово обновлено", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        setFragment(new allWordsFragment());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Dictionary> call, Throwable t) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryWord> call, Throwable t) {

                    }
                });
            }
        });

        TextView deleteWord = view.findViewById(R.id.deleteWord);
        deleteWord.setOnClickListener(v->{
            Call<Dictionary> dictionaryCall = apiInterface.deleteDictionary(dictionary.getIdDictionary());
            dictionaryCall.enqueue(new Callback<Dictionary>() {
                @Override
                public void onResponse(Call<Dictionary> call, Response<Dictionary> response) {
                    if(response.isSuccessful()){
                        Snackbar snackbar = Snackbar.make(rootView, "Слово удалено", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        setFragment(new allWordsFragment());
                    }
                }

                @Override
                public void onFailure(Call<Dictionary> call, Throwable t) {

                }
            });
        });
    }

    public void setFragment(Fragment fragment){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentLayoutMenu, fragment, null).commit();
    }

    private void getAllWords(View view) {
        Call<ArrayList<Dictionary>> getWordsList = apiInterface.getDictionary();

        getWordsList.enqueue(new Callback<ArrayList<Dictionary>>() {
            @Override
            public void onResponse(Call<ArrayList<Dictionary>> call, Response<ArrayList<Dictionary>> response) {
                if(response.isSuccessful()){
                    ArrayList<Dictionary> allWords = response.body();
                    if (allWords != null && !allWords.isEmpty()) {
                        RecyclerView recyclerView = view.findViewById(R.id.allWordsRecycler);
                        WordAdapter wordAdapter = new WordAdapter(view.getContext(), allWords, allWordsFragment.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                        recyclerView.setAdapter(wordAdapter);
                    } else {
                        Toast.makeText(view.getContext(), "Список слов пуст", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(view.getContext(), "Ошибка получения слов", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Dictionary>> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ImageView goBack = view.findViewById(R.id.goBackToAdminFunc);
        goBack.setOnClickListener(v->{
            setFragment(new AdminFragment());
        });
    }
}