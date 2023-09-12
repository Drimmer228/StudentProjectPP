package com.example.words;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.words.ApiPackage.ApiInterface;
import com.example.words.ApiPackage.RequestBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoAboutUser extends AppCompatActivity {
    private User user;
    private TextView nickUser;
    private TextView emailUser;
    private EditText passwordUser;
    private TextView dateRegUser;
    private TextView dateUpdateUser;
    private TextView countAttUser;
    private TextView ratingUser;
    private TextView roleUser;

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_about_user);
        Objects.requireNonNull(getSupportActionBar()).hide();

        checkAdminUser();

        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("userData");

        nickUser = findViewById(R.id.nickUserTextView);
        emailUser = findViewById(R.id.emailTextView);
        passwordUser = findViewById(R.id.passwordEditText);
        dateRegUser = findViewById(R.id.dateRegTextView);
        dateUpdateUser = findViewById(R.id.dateUpdateAttemptsTextView);
        countAttUser = findViewById(R.id.countAttemptsTextView);
        ratingUser = findViewById(R.id.ratingUserTextView);
        roleUser = findViewById(R.id.roleUserTextView);

        nickUser.setText(user.getNickName());
        emailUser.setText("Почта\n" + user.getEmail());
        passwordUser.setText(user.getPassword());
        dateRegUser.setText("Зарегистрирован\n" + user.getDateRegistration());
        dateUpdateUser.setText("Попытки обновятся\n" + user.getDateUpdate());
        countAttUser.setText("Количество попыток\n" + String.valueOf(user.getAmountAttempts()));
        ratingUser.setText("Рейтинг\n" + String.valueOf(user.getRating()));

        switch (user.getRole()){
            case "moder":
                roleUser.setText("Роль в системе\nМодератор");
                break;
            case "user":
                roleUser.setText("Роль в системе\nПользователь");
                break;
            case "admin":
                roleUser.setText("Роль в системе\nАдминистратор");
                break;
        }

        ImageView buttonGoBack = findViewById(R.id.goBackToRating);
        buttonGoBack.setOnClickListener(v->finish());

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            checkUserSuspect(currentDate, dateFormat.parse(user.getDateRegistration()), user.getRating());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        TextView updateUserPasswordTextView = findViewById(R.id.tvUpdatePassword);
        updateUserPasswordTextView.setOnClickListener(v-> {
                updateUserPassword();
        });
    }

    private void checkAdminUser() {
        userRef.get().addOnCompleteListener(task-> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String emailFromFireBase = document.getString("email");
                    if(emailFromFireBase.equals(fbAuth.getCurrentUser().getEmail())){
                        String roleUser = document.getString("role");
                        if(roleUser.equals("admin")){
                            LinearLayout layout = findViewById(R.id.infoAboutUserLayout);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)emailUser.getLayoutParams();
                            params.setMargins(0,10,0,10);
                            Button unBanUserButton = new Button(this);
                            unBanUserButton.setText("Снять бан");
                            unBanUserButton.setAllCaps(true);
                            unBanUserButton.setLayoutParams(params);
                            unBanUserButton.setPadding(10,10,10,10);
                            unBanUserButton.setBackgroundResource(R.drawable.rounded_parent_background);
                            unBanUserButton.setOnClickListener(this::unBanUser);

                            Button unDeleteUserButton = new Button(this);
                            unDeleteUserButton.setText("Восстановить доступ");
                            unDeleteUserButton.setAllCaps(true);
                            unDeleteUserButton.setLayoutParams(params);
                            unDeleteUserButton.setPadding(10,10,10,10);
                            unDeleteUserButton.setBackgroundResource(R.drawable.rounded_parent_background);
                            unDeleteUserButton.setOnClickListener(this::unDeleteUser);

                            Button deleteUserButton = new Button(this);
                            deleteUserButton.setText("Удалить");
                            deleteUserButton.setAllCaps(true);
                            deleteUserButton.setLayoutParams(params);
                            deleteUserButton.setPadding(10,10,10,10);
                            deleteUserButton.setBackgroundResource(R.drawable.rounded_parent_background);
                            deleteUserButton.setOnClickListener(this::deleteUser);

                            layout.addView(unBanUserButton);
                            layout.addView(unDeleteUserButton);
                            layout.addView(deleteUserButton);
                        }
                    }
                }
            }
        });
    }

    private void deleteUser(View view) {
        String[] emailFromFireBaseCurrentUser = {""};
        String[] passwordFromFireBaseCurrentUser = {""};
        userRef.get().addOnCompleteListener(task-> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String emailFromFireBase = document.getString("email");
                    if (emailFromFireBase.equals(fbAuth.getCurrentUser().getEmail())) {
                        {
                            emailFromFireBaseCurrentUser[0] = document.getString("email");
                            passwordFromFireBaseCurrentUser[0] = document.getString("password");
                        }
                    }
                }
            }
        });

        userRef.get().addOnCompleteListener(task2-> {
            if (task2.isSuccessful()) {
                for (QueryDocumentSnapshot document : task2.getResult()) {
                    String emailFromFireBase = document.getString("email");
                    if (emailFromFireBase.equals(user.getEmail())) {
                        if(emailFromFireBase.equals(fbAuth.getCurrentUser().getEmail())){
                            Snackbar snackbar = Snackbar.make(view, "Нельзя удалить себя!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }else{
                            fbAuth.signOut();
                            fbAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    fbAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            fbAuth.signOut();
                                            fbAuth.signInWithEmailAndPassword(emailFromFireBaseCurrentUser[0], passwordFromFireBaseCurrentUser[0]);
                                            userRef.document(document.getId()).delete();
                                        }
                                    });
                                }
                            });
                        }
                        break;
                    }
                }
            }
        });
    }

    private void unDeleteUser(View view) {
        userRef.get().addOnCompleteListener(task-> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String emailFromFireBase = document.getString("email");
                    if (emailFromFireBase.equals(user.getEmail())) {
                        boolean isBanned = (boolean)document.get("is_deleted");
                        if(isBanned){
                            userRef.document(document.getId()).update("is_deleted", false);
                            Snackbar snackbar = Snackbar.make(view, "Пользователь восстановлен", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }else{
                            Snackbar snackbar = Snackbar.make(view, "Пользователь не удален -" +
                                    "\nНичего не произошло", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                        break;
                    }
                }
            }
        });
    }

    private void unBanUser(View view) {
        userRef.get().addOnCompleteListener(task-> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String emailFromFireBase = document.getString("email");
                    if (emailFromFireBase.equals(user.getEmail())) {
                        boolean isBanned = (boolean)document.get("is_banned");
                        if(isBanned){
                            userRef.document(document.getId()).update("is_banned", false);
                            Snackbar snackbar = Snackbar.make(view, "Пользователь разбанен", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }else{
                            Snackbar snackbar = Snackbar.make(view, "Пользователь не забанен -" +
                                    "\n Ничего не произошло", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                        break;
                    }
                }
            }
        });
    }

    private void updateUserPassword() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }

        if(passwordUser.getText().toString().trim().length() < 6){
            passwordUser.setError("Длина пароля должна быть больше 6 символов!");
            Snackbar snackbar = Snackbar.make(rootView, "Ошибка изменения пароля!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else{
            //Оповещение по почте
            new Thread(new SendEmailTask("Words inc", "Здравствуйте, " + user.getNickName()
                    + "\nПароль от вашего аккаунта был изменён на " + passwordUser.getText().toString().trim(),
                    user.getEmail()))
                    .start();

            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailCurrentUser = auth.getCurrentUser().getEmail();
            userRef.get().addOnCompleteListener(task-> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String emailFromFireBase = document.getString("email");
                        String passwordFromFireBase = document.getString("password");
                        if (emailFromFireBase.equals(user.getEmail())) {
                            userRef.document(document.getId()).update("password", passwordUser.getText().toString().trim());
                        }
                        if (emailFromFireBase.equals(emailCurrentUser)){
                            auth.signOut();
                            auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseUser userUpdate = auth.getCurrentUser();
                                    String newPassword = passwordUser.getText().toString().trim();
                                    userUpdate.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Snackbar snackbar = Snackbar.make(rootView, "Пароль изменён", Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                            auth.signOut();
                                            auth.signInWithEmailAndPassword(emailFromFireBase, passwordFromFireBase);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void checkUserSuspect(Date currentDate, Date dateRegestration, int ratingUser) {
        TextView userSuspectTextView = findViewById(R.id.userIsSuspect);
        ApiInterface apiInterface = RequestBuilder.buildRequest().create(ApiInterface.class);
        Call<Integer> maxAmount = apiInterface.getMaxAmountLetter();
        maxAmount.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    int maxAmount = response.body();

                    Calendar calendarRegistration = Calendar.getInstance();
                    calendarRegistration.setTime(dateRegestration);
                    Calendar calendarUpdate = Calendar.getInstance();
                    calendarUpdate.setTime(currentDate);

                    long diffMillis = calendarUpdate.getTimeInMillis() - calendarRegistration.getTimeInMillis();
                    int diffDays = (int) TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS) + 1; //Количество дней в игре

                    int maxRating = diffDays * 5 * maxAmount;

                    if(maxRating < ratingUser){
                        userSuspectTextView.setText("Подозрительный пользователь");
                        userSuspectTextView.setAllCaps(true);
                        userSuspectTextView.setTextColor(getResources().getColor(R.color.red));

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                        LinearLayout linearLayout = findViewById(R.id.layoutWithNick);
                        TextView BanUserTextView = new TextView(userSuspectTextView.getContext());
                        BanUserTextView.setText("Забанить");
                        BanUserTextView.setAllCaps(true);
                        BanUserTextView.setPadding(20,20,20,20);
                        BanUserTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        BanUserTextView.setLayoutParams(params);
                        BanUserTextView.setTextSize(30);
                        BanUserTextView.setBackgroundResource(R.drawable.rounded_red_ban_background);
                        BanUserTextView.setOnClickListener(v->{
                            BanUser();
                        });
                        linearLayout.addView(BanUserTextView);
                    }else{
                        userSuspectTextView.setText("Не подозрительный пользователь");
                        userSuspectTextView.setAllCaps(false);
                        userSuspectTextView.setTextColor(getResources().getColor(R.color.green));
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }

    private void BanUser() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if(user.getEmail().equals(fbAuth.getCurrentUser().getEmail())) {
            Snackbar snackbar = Snackbar.make(rootView, "Нельзя забанить самого себя!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else{
            Calendar myCalendar = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                StringBuilder dateBan = new StringBuilder();
                monthOfYear++;

                if(dayOfMonth < 10){
                    dateBan.append("0");
                }
                dateBan.append(dayOfMonth + ".");
                if(monthOfYear < 10){
                    dateBan.append("0");
                }
                dateBan.append(monthOfYear + "." +year);

                userRef.get().addOnCompleteListener(task-> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String emailFromFireBase = document.getString("email");
                            if (emailFromFireBase.equals(user.getEmail())) {
                                Map<String, Object> updatesIsBanned = new HashMap();
                                updatesIsBanned.put("is_banned", true);

                                Map<String, Object> updatesDateUnban = new HashMap();
                                updatesDateUnban.put("dateUnban", dateBan.toString());

                                userRef.document(document.getId()).update(updatesIsBanned);
                                userRef.document(document.getId()).update(updatesDateUnban);
                                finish();
                            }
                        }
                    }
                });
            };

            DatePickerDialog datePicker = new DatePickerDialog(nickUser.getContext(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            myCalendar.add(Calendar.DAY_OF_YEAR, 1);
            datePicker.getDatePicker().setMinDate(myCalendar.getTimeInMillis()+1);
            datePicker.show();
        }
    }
}