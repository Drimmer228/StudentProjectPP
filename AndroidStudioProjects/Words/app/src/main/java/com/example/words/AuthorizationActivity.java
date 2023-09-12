package com.example.words;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AuthorizationActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        Objects.requireNonNull(getSupportActionBar()).hide();
        EditText etAuthEmail = findViewById(R.id.etAuthEmail);
        EditText etAuthPass = findViewById(R.id.etAuthPass);

        firebaseAuth = FirebaseAuth.getInstance();

        SharedPreferences settings = getSharedPreferences("AuthData", MODE_PRIVATE);
        String checkEmail = settings.getString("emailUser", "null");
        String checkPassword = settings.getString("passwordUser", "null");
        if(!checkEmail.equals("null") & !checkPassword.equals("null")){
            authUser(etAuthEmail, checkEmail, checkPassword, getApplicationContext());
        }

        TextView goToRegestration = findViewById(R.id.tvGoToRegistration);
        goToRegestration.setOnClickListener(view ->{
            Intent intent = new Intent(this, RegestrationActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


        TextView btSignIn = findViewById(R.id.btSignIn);
        btSignIn.setOnClickListener(view -> {
            authUser(etAuthEmail, etAuthEmail.getText().toString().trim(), etAuthPass.getText().toString().trim(), getApplicationContext());
        });
    }

    private void setColorForViews(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("colorSettings", MODE_PRIVATE);
        int colorParent = sharedPreferences.getInt("colorParent", 0);
        if(colorParent != 0) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.rounded_parent_background);
            if (drawable instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                gradientDrawable.setColor(colorParent);
                Window window = getWindow();
                window.setStatusBarColor(R.drawable.rounded_parent_background);
            }
        }else{
            sharedPreferences.edit().putInt("colorParent" , Color.parseColor("#FFA500")).apply();
        }

        int colorChild = sharedPreferences.getInt("colorChild", 0);
        if(colorChild != 0) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.rounded_child_background);
            if (drawable instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                gradientDrawable.setColor(colorChild);
            }
        }else{
            sharedPreferences.edit().putInt("colorChild" , Color.parseColor("#FFCA86")).apply();
        }
    }

    public void authUser(EditText etAuthEmail, String email, String password, Context context) {
        ProgressDialog progressDialog = createProgressDialog(this);
        progressDialog.show();

        hideKeyboard();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    userRef.whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String role = document.getString("role");
                                        boolean isBanned = document.getBoolean("is_banned");
                                        boolean isDeleted = document.getBoolean("is_deleted");

                                        if (isBanned) {
                                            progressDialog.dismiss();
                                            showBanDialog(document.getString("dateUnban"));
                                        } else if (!isDeleted) {
                                            progressDialog.dismiss();
                                            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                                            Snackbar snackbar = Snackbar.make(rootView, "Успешная авторизация!", Snackbar.LENGTH_SHORT);
                                            snackbar.show();

                                            SharedPreferences settings = context.getSharedPreferences("AuthData", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("emailUser", email);
                                            editor.putString("passwordUser", password);
                                            editor.putString("role", role);
                                            editor.apply();

                                            new Handler().postDelayed(() -> {
                                                setColorForViews(etAuthEmail.getContext());
                                                Intent intent = new Intent(context, MenuActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }, 500);
                                        } else {
                                            progressDialog.dismiss();
                                            showErrorAuth();
                                        }
                                    }
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    if (e.getMessage().equalsIgnoreCase("The password is invalid or the user does not have a password.")) {
                        progressDialog.dismiss();
                        showErrorAuth();
                    }
                });
    }

    private ProgressDialog createProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Авторизация...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    private void showBanDialog(String dateUnban) {
        DialogAboutBan dialog = DialogAboutBan.newInstance(dateUnban);
        dialog.show(getSupportFragmentManager(), "my_dialog");
    }

    private void showErrorAuth() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, "Неверные данные для входа!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}