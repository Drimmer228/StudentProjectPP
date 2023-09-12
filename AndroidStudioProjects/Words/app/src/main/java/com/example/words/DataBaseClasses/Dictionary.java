package com.example.words.DataBaseClasses;

public class Dictionary {
    private int idDictionary;
    private String nameWord;
    private int amountLetter;
    private int categoryWordId;
    private String descriptionWord;

    public int getIdDictionary() {
        return idDictionary;
    }

    public void setIdDictionary(int idDictionary) {
        this.idDictionary = idDictionary;
    }

    public String getNameWord() {
        return nameWord;
    }

    public void setNameWord(String nameWord) {
        this.nameWord = nameWord;
    }

    public int getAmountLetter() {
        return amountLetter;
    }

    public void setAmountLetter(int amountLetter) {
        this.amountLetter = amountLetter;
    }

    public int getCategoryWordId() {
        return categoryWordId;
    }

    public void setCategoryWordId(int categoryWordId) {
        this.categoryWordId = categoryWordId;
    }

    public String getDescriptionWord() {
        return descriptionWord;
    }

    public void setDescriptionWord(String descriptionWord) {
        this.descriptionWord = descriptionWord;
    }
}
