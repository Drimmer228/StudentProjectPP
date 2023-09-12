package com.example.words.AdminFunctions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.words.DataBaseClasses.Dictionary;
import com.example.words.GameResource.ActiveGameActivity;
import com.example.words.DataBaseClasses.CategoryWord;
import com.example.words.MenuActivity;
import com.example.words.MenuFragmentClass.CategoryWordAdapter;
import com.example.words.R;

import java.util.ArrayList;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordAdapterViewHolder> {
    Context context;
    private ArrayList<Dictionary> dictionaries;

    private OnWordItemClickListener listener;

    public WordAdapter(Context context, ArrayList<Dictionary> dictionaries, OnWordItemClickListener listener){
        this.context = context;
        this.dictionaries = dictionaries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WordAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.word_item, parent, false);
        return new WordAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordAdapterViewHolder holder, int position) {
        holder.bind(dictionaries.get(position));
    }

    @Override
    public int getItemCount() {
        return dictionaries.size();
    }

    class WordAdapterViewHolder extends RecyclerView.ViewHolder{

        public WordAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(view -> {
            });
        }

        public void bind(Dictionary dictionary){
            TextView idWord = itemView.findViewById(R.id.idWord);
            TextView nameWord = itemView.findViewById(R.id.nameWord);

            idWord.setText(String.valueOf(dictionary.getIdDictionary()));
            nameWord.setText(dictionary.getNameWord());

            itemView.setOnClickListener(view -> {
                listener.onWordItemClick(dictionary);
            });
        }
    }
}
