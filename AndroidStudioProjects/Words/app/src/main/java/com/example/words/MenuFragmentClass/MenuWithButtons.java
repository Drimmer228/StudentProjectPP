package com.example.words.MenuFragmentClass;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.words.R;
import com.example.words.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.grpc.internal.AbstractReadableBuffer;

public class MenuWithButtons extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private  CollectionReference userRef = db.collection("users");
    private String userIdInFirebase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_button_menu, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.fragment_button_menu, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserData();

        userRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String emailFromFireBase = document.getString("email");
                    if (emailFromFireBase.equals(firebaseAuth.getCurrentUser().getEmail())) {
                        long[] countAttempts = {(long)document.get("amountAttempts")};
                        String dateTomorrow = document.getString("dateUpdate");
                        TextView textViewAtt = view.findViewById(R.id.countAttemptsOnButton);
                        textViewAtt.setText(String.valueOf(countAttempts[0]) + "/5");

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        try {
                            Date date = dateFormat.parse(dateTomorrow);
                            Date currentDate = new Date();
                            if (currentDate.after(date)) {
                                textViewAtt.setText("5/5");
                                countAttempts[0] = 5;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        LinearLayout play = view.findViewById(R.id.btPlay);
                        play.setOnClickListener(v ->{
                            if((int)countAttempts[0] == 0){
                                Activity activity = getActivity();
                                View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                                Snackbar snackbar = Snackbar.make(rootView, "Попробуйте ещё раз", Snackbar.LENGTH_SHORT);
                                snackbar.show();

                                Handler handler = new Handler();
                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        while(snackbar.isShown()){
                                            Calendar calendar = Calendar.getInstance();

                                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                                            calendar.set(Calendar.MINUTE, 0);
                                            calendar.set(Calendar.SECOND, 0);
                                            calendar.add(Calendar.DAY_OF_MONTH, 1);

                                            long timeInMillis = calendar.getTimeInMillis() - System.currentTimeMillis();

                                            String time = String.format("%02d:%02d:%02d",
                                                    TimeUnit.MILLISECONDS.toHours(timeInMillis),
                                                    TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % TimeUnit.HOURS.toMinutes(1),
                                                    TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % TimeUnit.MINUTES.toSeconds(1));

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    snackbar.setText("На сегодня попытки закончились!\nСледующая попытка через " + time);
                                                }
                                            });

                                            try {
                                                Thread.sleep(999);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                };

                                Thread myThread = new Thread(myRunnable);
                                myThread.start();

                            }else{
                                setHeaderSupportBar("Выбор категории слова");
                                setFragment(new ChangeLevelsFragment());
                            }
                        });
                    }
                }

            }
        });

        TextView rating = view.findViewById(R.id.btRating);
        rating.setOnClickListener(v ->{
            setHeaderSupportBar("Рейтинг");
            setFragment(new RatingFragment());
        });

        TextView settings = view.findViewById(R.id.btSettings);
        settings.setOnClickListener(v ->{
            setHeaderSupportBar("Настройки");
            setFragment(new SettingFragment());
        });
    }



    private void loadUserData() {
        userRef.get().addOnCompleteListener(task->{
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    String emailFromFireBase = document.getString("email");
                    if(emailFromFireBase.equals(firebaseAuth.getCurrentUser().getEmail())){
                        userIdInFirebase = document.getId();
                        String passwordFromFireBase = document.getString("password");
                        String nickNameFromFireBase = document.getString("nickName");
                        Object ratingFromFireBase = document.get("rating");
                        String roleFromFireBase = document.getString("role");
                        Object is_deletedFromFireBase = document.get("is_deleted");
                        Object is_bannedFromFireBase = document.get("is_banned");
                        String nextDate = document.getString("dateUpdate");
                        long amountAtt = (long)document.get("amountAttempts");
                        int rating = Integer.parseInt(ratingFromFireBase.toString());
                        String dateReg = document.getString("dateRegistration");

                        boolean is_deleted = (boolean)is_deletedFromFireBase;
                        boolean is_banned = (boolean)is_bannedFromFireBase;
                        String dateUnban = document.getString("dateUnban");

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        try {
                            Date date = dateFormat.parse(nextDate);
                            Date currentDate = new Date();
                            if (currentDate.after(date)) {
                                amountAtt = 5;
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.DAY_OF_YEAR, 1);
                                nextDate = dateFormat.format(calendar.getTime());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        User user = new User(
                                nickNameFromFireBase,
                                emailFromFireBase,
                                passwordFromFireBase,
                                rating,
                                is_deleted,
                                nextDate,
                                (int)amountAtt,
                                roleFromFireBase,
                                dateReg,
                                is_banned,
                                dateUnban);
                        userRef.document(userIdInFirebase).set(user);
                    }
                }
            }
        });

    }

    private void setHeaderSupportBar(String title){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle(title);
    }

    public void setFragment(Fragment fragment){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentLayoutMenu, fragment, null).commit();
    }
}