package com.example.words.AdminFunctions;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.words.DataBaseClasses.Dictionary;
import com.example.words.GameResource.ActiveGameActivity;
import com.example.words.DataBaseClasses.CategoryWord;
import com.example.words.R;

import java.util.ArrayList;

public class CategoryWordAdapterAll extends RecyclerView.Adapter<CategoryWordAdapterAll.CategoryWordViewHolder>{
    Context context;
    private ArrayList<CategoryWord> categoryWords;
    private OnCategoryWordItemClickListener listener;

    public CategoryWordAdapterAll(Context context, ArrayList<CategoryWord> categoryWords, OnCategoryWordItemClickListener listener){
        this.context = context;
        this.categoryWords = categoryWords;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryWordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.category_word_item, parent, false);
        return new CategoryWordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryWordViewHolder holder, int position) {
        holder.bind(categoryWords.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryWords.size();
    }

    class CategoryWordViewHolder extends RecyclerView.ViewHolder{

        public CategoryWordViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(view -> {
            });
        }

        public void bind(CategoryWord categoryWords){
            TextView nameCategoryWord = itemView.findViewById(R.id.name_category_word);
            nameCategoryWord.setText(categoryWords.getNameCategoryWord());

            itemView.setOnClickListener(view -> {
                listener.OnCategoryWordItemClick(categoryWords);
            });
        }
    }
}
