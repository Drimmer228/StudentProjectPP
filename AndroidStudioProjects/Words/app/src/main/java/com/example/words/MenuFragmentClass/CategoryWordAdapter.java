package com.example.words.MenuFragmentClass;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.words.GameResource.ActiveGameActivity;
import com.example.words.DataBaseClasses.CategoryWord;
import com.example.words.R;

import java.util.ArrayList;

public class CategoryWordAdapter extends RecyclerView.Adapter<CategoryWordAdapter.CategoryWordViewHolder>{
    private ArrayList<CategoryWord> categoryWords;

    public CategoryWordAdapter(ArrayList<CategoryWord> categoryWords){
        this.categoryWords = categoryWords;
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
            TextView nameCategory = itemView.findViewById(R.id.name_category_word);
            TextView idCategory = itemView.findViewById(R.id.id_category_word);

            nameCategory.setText(categoryWords.getNameCategoryWord());
            int idTheme = categoryWords.getIdCategoryWord();
            idCategory.setText(String.valueOf(idTheme));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, ActiveGameActivity.class);
                    intent.putExtra("CategoryWord", categoryWords);
                    context.startActivity(intent);
                }
            });
        }
    }
}
