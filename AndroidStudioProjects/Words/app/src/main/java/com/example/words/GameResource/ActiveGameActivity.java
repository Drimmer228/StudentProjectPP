package com.example.words.GameResource;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.words.ApiPackage.ApiInterface;
import com.example.words.ApiPackage.RequestBuilder;
import com.example.words.DataBaseClasses.CategoryWord;
import com.example.words.DataBaseClasses.Dictionary;
import com.example.words.MenuActivity;
import com.example.words.R;
import com.example.words.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveGameActivity extends AppCompatActivity implements ComponentCallbacks{
    CategoryWord categoryWord;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("users");
    String userIdInFirebase;
    AlphabetData alphabet = new AlphabetData();
    Random random = new Random();
    int CountLetter = -1;
    String wordOnLevel;
    ArrayList<EditText> lettersOnLayout = new ArrayList<EditText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_game);

        Window window = getWindow();
        window.setStatusBarColor(R.drawable.rounded_child_background);

        Intent intent = getIntent();
        categoryWord = (CategoryWord) intent.getSerializableExtra("CategoryWord");
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                "Слово на тему: " + categoryWord.getNameCategoryWord());

        setClickOnTextView();
        loadLevelData();
        CreateKeyBoard();
    }

    private void loadLevelData() {
        ApiInterface apiInterface = RequestBuilder.buildRequest().create(ApiInterface.class);
        Call<ArrayList<Dictionary>> getDictionaryWordList = apiInterface.getDictionary();

        getDictionaryWordList.enqueue(new Callback<ArrayList<Dictionary>>() {
            @Override
            public void onResponse(Call<ArrayList<Dictionary>> call, Response<ArrayList<Dictionary>> response) {
                if(response.isSuccessful()){
                    ArrayList<Dictionary> dictionaryList = response.body();
                    ArrayList<Dictionary> sortList = new ArrayList<Dictionary>();
                    for(int i = 0; i< dictionaryList.size(); i++){
                        if(dictionaryList.get(i).getCategoryWordId() == categoryWord.getIdCategoryWord())
                        {
                            sortList.add(dictionaryList.get(i));
                        }
                    }
                    int temp = 0;
                    while (temp == 0) temp = random.nextInt(sortList.size());

                    wordOnLevel = sortList.get(temp).getNameWord();
                    CountLetter = sortList.get(temp).getAmountLetter();
                    String description = sortList.get(temp).getDescriptionWord();

                    TextView tvDescription = findViewById(R.id.descriptionTextView);
                    tvDescription.setText(description);
                    CreateTextView(sortList.get(temp).getAmountLetter());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Dictionary>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setClickOnTextView() {
        TextView exit = findViewById(R.id.btExitTheLevel);
        TextView eraseLetter = findViewById(R.id.btEraseLetter);
        TextView eraseWord = findViewById(R.id.btEraseWord);

        exit.setOnClickListener(view -> finish());
        eraseLetter.setOnClickListener(view -> {
            for (int i = lettersOnLayout.size(); i != 0; i--){
                if(!lettersOnLayout.get(i-1).getText().toString().equals("")){
                    lettersOnLayout.get(i-1).setText("");
                    break;
                }
            }
        });
        eraseWord.setOnClickListener(view -> {
            for (int i = 0; i != lettersOnLayout.size(); i++) lettersOnLayout.get(i).setText("");
        });
    }

    private void CreateKeyBoard() {
        LinearLayout keyboardLayout1 = findViewById(R.id.keyBoardLayoutStroke1);
        LinearLayout keyboardLayout2 = findViewById(R.id.keyBoardLayoutStroke2);
        LinearLayout keyboardLayout3 = findViewById(R.id.keyBoardLayoutStroke3);

        LinearLayout[] keyboardStrokes = {keyboardLayout1, keyboardLayout2, keyboardLayout3};
        String[][] alphabetLetters = {
                alphabet.AlphabetStroke1,
                alphabet.AlphabetStroke2,
                alphabet.AlphabetStroke3};

        for (int j = 0; j!=3; j++){
            for (int i = 0; i!=alphabetLetters[j].length; i++){
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(4, 0, 4, 10);

                TextView letter = new TextView(this);
                letter.setTextSize(30);
                letter.setText(alphabetLetters[j][i]);
                letter.setPadding(10,2,10,2);
                letter.setBackgroundResource(R.drawable.rounded_parent_background);
                letter.setOnClickListener(v-> letterPressed((TextView)v));
                letter.setLayoutParams(layoutParams);
                keyboardStrokes[j].addView(letter);
            }
        }
    }

    private void letterPressed(TextView letter) {
        for (int i = 0; i < lettersOnLayout.size(); i++){
            if(lettersOnLayout.get(i).getText().toString().isEmpty()){
                lettersOnLayout.get(i).setText(letter.getText());
                Context context = letter.getContext();
                if(i+1==lettersOnLayout.size()){
                    String checkWordAtUser = "";
                    for (int j = 0; j < lettersOnLayout.size(); j++){
                        checkWordAtUser += lettersOnLayout.get(j).getText();
                    }
                    if(checkWordAtUser.trim().equalsIgnoreCase(wordOnLevel)){
                        showSnackBar("Правильно! Уровень пройден");

                        userRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String emailFromFireBase = document.getString("email");
                                    if(emailFromFireBase.equals(firebaseAuth.getCurrentUser().getEmail()))
                                    {
                                        userIdInFirebase = document.getId();

                                        String passwordFromFireBase = document.getString("password");
                                        String nickNameFromFireBase = document.getString("nickName");
                                        Object ratingFromFireBase = document.get("rating");
                                        Object is_deletedFromFireBase = document.get("is_deleted");
                                        String nextDate = document.getString("dateUpdate");
                                        String role = document.getString("role");
                                        String dateReg = document.getString("dateRegistration");
                                        long amountAtt = (long)document.get("amountAttempts");
                                        if((int)amountAtt != 0) amountAtt--;

                                        int rating = Integer.parseInt(ratingFromFireBase.toString());
                                        rating+=CountLetter;

                                        boolean is_deleted = (boolean)is_deletedFromFireBase;

                                        Object is_bannedFromFireBase = document.get("is_banned");
                                        boolean is_banned = (boolean)is_bannedFromFireBase;
                                        String dateUnban = document.getString("dateUnban");
                                        User user = new User(
                                                nickNameFromFireBase,
                                                emailFromFireBase,
                                                passwordFromFireBase,
                                                rating,
                                                is_deleted,
                                                nextDate,
                                                (int)amountAtt,
                                                role,
                                                dateReg,
                                                is_banned,
                                                dateUnban);
                                        userRef.document(userIdInFirebase).set(user);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(context, MenuActivity.class);
                                                intent.putExtra("roleUser", role);
                                                startActivity(intent);
                                            }
                                        }, 1500);
                                    }
                                }
                            }
                        });
                    }else{
                        showSnackBar("Вы не угадали слово :)");
                    }
                }
                break;
            }
        }
    }

    private void showSnackBar(String message) {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void CreateTextView(int countLetter) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        LinearLayout lettersLayout = findViewById(R.id.letterLayout);

        for (int i = 0; i!=countLetter; i++){
            int letterWidth = screenWidth / CountLetter;
            EditText letterView = new EditText(this);
            letterView.setKeyListener(null);
            letterView.setTextSize(TypedValue.COMPLEX_UNIT_PX, letterWidth);
            lettersOnLayout.add(letterView);
            lettersLayout.addView(letterView);
        }
    }
}