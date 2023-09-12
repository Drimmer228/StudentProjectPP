package com.example.words.DataBaseClasses;

import java.io.Serializable;
import java.nio.file.SecureDirectoryStream;

public class CategoryWord implements Serializable {
    private int idCategoryWord;
    private String nameCategoryWord;

    public CategoryWord (String nameCategoryWord){
        this.nameCategoryWord = nameCategoryWord;
    }
    public int getIdCategoryWord() {
        return idCategoryWord;
    }

    public void setIdCategoryWord(int idCategoryWord) {
        this.idCategoryWord = idCategoryWord;
    }

    public String getNameCategoryWord() {
        return nameCategoryWord;
    }

    public void setNameCategoryWord(String nameCategoryWord) {
        this.nameCategoryWord = nameCategoryWord;
    }
}
