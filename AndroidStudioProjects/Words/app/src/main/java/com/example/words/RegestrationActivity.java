package com.example.words;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class RegestrationActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    EditText etNickname;
    EditText etEmail;
    EditText etPassword;
    EditText etRepeatPassword;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference userRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regestration);
        Objects.requireNonNull(getSupportActionBar()).hide();

        etNickname = findViewById(R.id.etNickname);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);

        TextView goToAuthorization = findViewById(R.id.tvGoToAuthorization);
        goToAuthorization.setOnClickListener(view ->{
            Intent intent = new Intent(this, AuthorizationActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        TextView btSignUp = findViewById(R.id.btSignUp);
        btSignUp.setOnClickListener(view -> {
            validData(etNickname.getText().toString(), etEmail.getText().toString().trim(), etPassword.getText().toString().trim(), etRepeatPassword.getText().toString().trim());
        });
    }

    private void validData(String nickname, String email, String password, String repeatPassword) {
        firebaseAuth = FirebaseAuth.getInstance();
        Context context = getApplicationContext();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Неверный формат почты");
        }else if(TextUtils.isEmpty(password)){
            etPassword.setError("Введите пароль!");
        }else if(password.length() < 6){
            etPassword.setError("Пароль не может быть меньше 6 символов!");
        }else if(!Objects.equals(repeatPassword, password)) {
            etRepeatPassword.setError("Пароли не совпадают!");
        }else{
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            View focusedView = getCurrentFocus();
            if (focusedView != null) {
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                            Snackbar snackbar = Snackbar.make(rootView, "Успешная регистрация! Пожалуйста, подождите...", Snackbar.LENGTH_SHORT);
                            snackbar.show();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            String tomorrowDate = dateFormat.format(calendar.getTime());

                            Date currentDate = new Date();
                            String tempDate = dateFormat.format(currentDate);

                            User newUser = new User(email, password, nickname, 5, tomorrowDate, tempDate);
                            userRef.add(newUser);

                            new Handler().postDelayed(() -> {
                                firebaseAuth.signInWithEmailAndPassword(email, password)
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                View focusedView = getCurrentFocus();
                                                if (focusedView != null) {
                                                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                                                }

                                                SharedPreferences settings = context.getSharedPreferences("AuthData", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = settings.edit();
                                                editor.putString("emailUser", email);
                                                editor.putString("passwordUser", password);
                                                editor.putString("role", "user");
                                                editor.apply();

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(context, MenuActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }, 1500);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }, 1500);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Регистрация невозможна, проверьте данные", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}