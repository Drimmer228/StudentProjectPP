package com.example.words;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.example.words.GameResource.AlphabetData;
import com.example.words.AdminFunctions.AdminFragment;
import com.example.words.MenuFragmentClass.MenuWithButtons;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;
import java.util.Random;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Window window = getWindow();
        window.setStatusBarColor(R.drawable.rounded_child_background);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userRef = db.collection("users");

        userRef.get().addOnCompleteListener(task-> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(document.getString("email").equals(firebaseAuth.getCurrentUser().getEmail())){
                        if(document.getString("role").equals("admin")) setFragment(new AdminFragment());
                            else setFragment(new MenuWithButtons());
                    }
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        AlphabetData alphabet = new AlphabetData();
        Random random = new Random();
        if(random.nextInt(2)==1){
            startAnimBackground(alphabet.AlphabetUpper);
            startAnimBackground(alphabet.AlphabetUpper);
        }else{
            startAnimBackground(alphabet.AlphabetLower);
            startAnimBackground(alphabet.AlphabetLower);
        }
    }

    private void startAnimBackground(String[] alphabetUpper) {
        for (int i = 0; i < 30; i++) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;
            Random random = new Random();

            TextView textView = new TextView(getApplicationContext());
            textView.setText(alphabetUpper[random.nextInt(32)]);
            textView.setTextSize(40);
            textView.setElevation(0f);

            int y = random.nextInt(screenHeight - textView.getHeight());

            int rotation = random.nextInt(180);

            int duration = random.nextInt(15000) + 5000;

            TranslateAnimation translation = new TranslateAnimation(
                    1100,
                    -100,
                    y-500,
                    y-random.nextInt(200));
            translation.setDuration(duration);
            translation.setInterpolator(new AccelerateInterpolator());

            RotateAnimation rotationAnim = new RotateAnimation(0, rotation,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            rotationAnim.setDuration(duration);

            AnimationSet animSet = new AnimationSet(false);
            animSet.addAnimation(translation);
            animSet.addAnimation(rotationAnim);

            ViewGroup rootView = findViewById(android.R.id.content);
            rootView.addView(textView, 0);
            textView.startAnimation(animSet);

            animSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    textView.startAnimation(animSet);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    public void setFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentLayoutMenu, fragment, null).commit();
    }
}