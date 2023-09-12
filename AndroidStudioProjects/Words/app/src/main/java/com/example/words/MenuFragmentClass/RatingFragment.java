package com.example.words.MenuFragmentClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.words.InfoAboutUser;
import com.example.words.MenuActivity;
import com.example.words.R;
import com.example.words.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class RatingFragment extends Fragment {
    private Context mContext;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FrameLayout frameLayout = view.findViewById(R.id.frameRating);
        FrameLayout.LayoutParams paramsButton = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );

        Button goBackInMenu = new Button(view.getContext());
        goBackInMenu.setLayoutParams(paramsButton);
        goBackInMenu.setText("НАЗАД");
        goBackInMenu.setBackgroundColor(getResources().getColor(R.color.red));
        goBackInMenu.setOnClickListener(v -> {
            setHeaderSupportBar("Главное меню");
            setFragment(new MenuWithButtons());
        });

        frameLayout.addView(goBackInMenu);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> unsortedList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Object is_deletedFromFireBase = document.get("is_deleted");
                    boolean is_deleted = (boolean)is_deletedFromFireBase;

                    Object is_bannedFromFireBase = document.get("is_banned");
                    boolean is_banned = (boolean)is_bannedFromFireBase;
                    if(!is_deleted & !is_banned){
                        User user = new User(document.getString("nickName"),
                                document.getString("email"),
                                document.getString("password"),
                                Integer.parseInt(document.get("rating").toString()),
                                false,
                                document.getString("dateUpdate"),
                                Integer.parseInt(document.get("amountAttempts").toString()),
                                document.getString("role"),
                                document.getString("dateRegistration"),
                                is_banned,
                                document.getString("dateUnban")
                        );
                        unsortedList.add(user);
                    }
                }

                for (int i = 0; i < unsortedList.size()-1; i++){
                    String email = unsortedList.get(i).getEmail();
                    String nick = unsortedList.get(i).getNickName();
                    int rating = unsortedList.get(i).getRating();
                    String role = unsortedList.get(i).getRole();
                    String password = unsortedList.get(i).getPassword();
                    boolean is_deleted = unsortedList.get(i).isIs_deleted();
                    String dateUpdate = unsortedList.get(i).getDateUpdate();
                    String dateRegestration = unsortedList.get(i).getDateRegistration();
                    int amountAttempts = unsortedList.get(i).getAmountAttempts();
                    boolean is_banned = unsortedList.get(i).isIs_banned();
                    String dateUnban = unsortedList.get(i).getDateUnban();

                    String emailNext = unsortedList.get(i+1).getEmail();
                    String nickNext = unsortedList.get(i+1).getNickName();
                    int ratingNext = unsortedList.get(i+1).getRating();
                    String roleNext = unsortedList.get(i+1).getRole();
                    String passwordNext = unsortedList.get(i+1).getPassword();
                    boolean is_deletedNext = unsortedList.get(i+1).isIs_deleted();
                    String dateUpdateNext = unsortedList.get(i+1).getDateUpdate();
                    String dateRegestrationNext = unsortedList.get(i+1).getDateRegistration();
                    int amountAttemptsNext = unsortedList.get(i+1).getAmountAttempts();
                    boolean is_bannedNext = unsortedList.get(i+1).isIs_banned();
                    String dateUnbanNext = unsortedList.get(i+1).getDateUnban();

                    if(rating < ratingNext){
                        unsortedList.get(i).setNickName(nickNext);
                        unsortedList.get(i).setRating(ratingNext);
                        unsortedList.get(i).setEmail(emailNext);
                        unsortedList.get(i).setRole(roleNext);
                        unsortedList.get(i).setPassword(passwordNext);
                        unsortedList.get(i).setIs_deleted(is_deletedNext);
                        unsortedList.get(i).setDateUpdate(dateUpdateNext);
                        unsortedList.get(i).setDateRegistration(dateRegestrationNext);
                        unsortedList.get(i).setAmountAttempts(amountAttemptsNext);
                        unsortedList.get(i).setIs_banned(is_bannedNext);
                        unsortedList.get(i).setDateUnban(dateUnbanNext);

                        unsortedList.get(i+1).setNickName(nick);
                        unsortedList.get(i+1).setRating(rating);
                        unsortedList.get(i+1).setEmail(email);
                        unsortedList.get(i+1).setRole(role);
                        unsortedList.get(i+1).setPassword(password);
                        unsortedList.get(i+1).setIs_deleted(is_deleted);
                        unsortedList.get(i+1).setDateUpdate(dateUpdate);
                        unsortedList.get(i+1).setDateRegistration(dateRegestration);
                        unsortedList.get(i+1).setAmountAttempts(amountAttempts);
                        unsortedList.get(i+1).setIs_banned(is_banned);
                        unsortedList.get(i+1).setDateUnban(dateUnban);
                        i = -1;
                    }
                }

                for (int i = 0; i < unsortedList.size(); i++){
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.gravity = Gravity.CENTER;

                    LinearLayout strokeLayout = new LinearLayout(view.getContext());
                    strokeLayout.setOrientation(LinearLayout.HORIZONTAL);
                    strokeLayout.setLayoutParams(params);
                    float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources().getDisplayMetrics());

                    TextView tVPlaceIn = new TextView(view.getContext());
                    tVPlaceIn.setText(String.valueOf(i+1));
                    tVPlaceIn.setTextSize(textSize);
                    tVPlaceIn.setWidth(140);
                    tVPlaceIn.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                    strokeLayout.addView(tVPlaceIn);

                    TextView tVNickNameIn = new TextView(view.getContext());
                    tVNickNameIn.setText(unsortedList.get(i).getNickName());
                    tVNickNameIn.setTextSize(textSize);
                    tVNickNameIn.setWidth(790);
                    tVNickNameIn.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                    strokeLayout.addView(tVNickNameIn);

                    TextView tVRatingIn = new TextView(view.getContext());
                    tVRatingIn.setText(String.valueOf(unsortedList.get(i).getRating()));
                    tVRatingIn.setTextSize(textSize);
                    tVRatingIn.setWidth(150);
                    tVRatingIn.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                    strokeLayout.addView(tVRatingIn);

                    SharedPreferences settings = view.getContext().getSharedPreferences("AuthData", Context.MODE_PRIVATE);
                    String role = settings.getString("role", "null");
                    if(role.equals("moder")){
                        tVNickNameIn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (User user : unsortedList){
                                    String nick = ((TextView)v).getText().toString();
                                    if(user.getNickName().equals(nick)){
                                        Intent intent = new Intent(getContext(), InfoAboutUser.class);
                                        intent.putExtra("userData", user);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }

                    String EmailFromFireBase = firebaseAuth.getCurrentUser().getEmail();
                    if(unsortedList.get(i).getEmail().equals(EmailFromFireBase)){
                        strokeLayout.setBackgroundResource(R.drawable.rounded_child_background);
                        tVNickNameIn.setBackgroundResource(R.drawable.rounded_parent_background);
                        tVRatingIn.setBackgroundResource(R.drawable.rounded_parent_background);
                        tVPlaceIn.setBackgroundResource(R.drawable.rounded_parent_background);
                    }else{
                        strokeLayout.setBackgroundResource(0);
                    }

                    LinearLayout allListRating = view.findViewById(R.id.allListRating);
                    allListRating.addView(strokeLayout);
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