package com.example.words.ApiPackage;

import java.util.ArrayList;
import com.example.words.DataBaseClasses.Dictionary;
import com.example.words.DataBaseClasses.CategoryWord;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("Dictionaries")
    Call<ArrayList<Dictionary>> getDictionary();
    @GET("Dictionaries/GetMaxAmountLetter")
    Call<Integer> getMaxAmountLetter();
    @POST("Dictionaries")
    Call<Dictionary> addDictionary(@Body Dictionary dictionary);
    @PUT("Dictionaries/{id}")
    Call<Dictionary> updateDictionary(@Path("id") int dictionaryId, @Body Dictionary dictionary);
    @DELETE("Dictionaries/{id}")
    Call<Dictionary> deleteDictionary(@Path("id") int dictionaryId);

    @GET("CategoryWords/")
    Call<ArrayList<CategoryWord>> getCategoryWord();
    @GET("CategoryWords/{id}")
    Call<CategoryWord> getCategoryWordById(@Path("id") int CategoryWordId);
    @POST("CategoryWords/getName/{name}")
    Call<CategoryWord> getIdCategoryWord(@Path("name") String nameCategoryWord);
    @POST("CategoryWords")
    Call<CategoryWord> addCategoryWords(@Body CategoryWord categoryWord);
    @PUT("CategoryWords/{id}")
    Call<CategoryWord> updateCategoryWords(@Path("id") int categoryWordId, @Body CategoryWord categoryWord);
    @DELETE("CategoryWords/{id}")
    Call<CategoryWord> deleteCategoryWords(@Path("id") int categoryWordId);

}
