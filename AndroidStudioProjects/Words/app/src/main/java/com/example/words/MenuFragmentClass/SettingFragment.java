package com.example.words.MenuFragmentClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.words.AdminFunctions.AdminFragment;
import com.example.words.AuthorizationActivity;
import com.example.words.R;
import com.example.words.User;
import com.flask.colorpicker.ColorPickerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class SettingFragment extends Fragment {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout layout = view.findViewById(R.id.settings_layout);


        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        Button goBackInMenu = new Button(view.getContext());
        goBackInMenu.setLayoutParams(layoutParams);
        goBackInMenu.setText("НАЗАД");
        goBackInMenu.setBackgroundColor(getResources().getColor(R.color.red));
        goBackInMenu.setOnClickListener(v -> {
            setHeaderSupportBar("Главное меню");
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
        });

        LinearLayout linearLayoutEditNick = createLinearLayoutForSaveNickName(view);
        LinearLayout linearLayoutColorPickers = createLinearLayoutForEditColorApp(view);

        FrameLayout frameLayout = view.findViewById(R.id.frameLayoutSettings);
        layoutParams.gravity = Gravity.BOTTOM;
        Button button = new Button(view.getContext());
        button.setText("Выйти из аккаунта");
        button.setBackgroundColor(getResources().getColor(R.color.red));
        button.setLayoutParams(layoutParams);
        button.setOnClickListener(this::signOut);
        frameLayout.addView(button);

        layout.addView(goBackInMenu);
        layout.addView(linearLayoutEditNick);
        layout.addView(linearLayoutColorPickers);
    }

    private LinearLayout createLinearLayoutForEditColorApp(View view) {
        Context context = view.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("colorSettings", Context.MODE_PRIVATE);

        LinearLayout.LayoutParams paramsForLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsForLayout.setMargins(5,0,5,0);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(paramsForLayout);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(10,10,10,10);
        linearLayout.setBackgroundResource(R.drawable.rounded_child_background);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(510,510);
        params.setMargins(5,10,5,10);

        LinearLayout linearLayoutParent = new LinearLayout(context);
        linearLayoutParent.setOrientation(LinearLayout.VERTICAL);

        TextView headerParent = new TextView(context);
        headerParent.setText("ГЛАВНЫЙ ЦВЕТ");
        headerParent.setTextSize(20);
        headerParent.setTypeface(null, Typeface.BOLD);
        headerParent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        headerParent.setBackgroundResource(R.drawable.rounded_parent_background);
        headerParent.setPadding(5,5,5,5);

        ColorPickerView colorPickerViewForParent = new ColorPickerView(context);
        int colorParent = sharedPreferences.getInt("colorParent", 0);
        if(colorParent != 0) colorPickerViewForParent.setColor(colorParent, false);
        colorPickerViewForParent.setColor(sharedPreferences.getInt("colorParent", 0), false);
        colorPickerViewForParent.setLayoutParams(params);
        colorPickerViewForParent.setBackgroundResource(R.drawable.rounded_parent_background);

        linearLayoutParent.addView(headerParent);
        linearLayoutParent.addView(colorPickerViewForParent);

        LinearLayout linearLayoutChild = new LinearLayout(context);
        linearLayoutChild.setOrientation(LinearLayout.VERTICAL);

        TextView headerChild = new TextView(context);
        headerChild.setText("ДОП. ЦВЕТ");
        headerChild.setTextSize(20);
        headerChild.setTypeface(null, Typeface.BOLD);
        headerChild.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        headerChild.setBackgroundResource(R.drawable.rounded_parent_background);
        headerChild.setPadding(5,5,5,5);

        ColorPickerView colorPickerViewForChild = new ColorPickerView(context);
        int colorChild = sharedPreferences.getInt("colorChild", 0);
        if(colorChild != 0) colorPickerViewForChild.setColor(colorChild, false);
        colorPickerViewForChild.setLayoutParams(params);
        colorPickerViewForChild.setBackgroundResource(R.drawable.rounded_parent_background);

        linearLayoutChild.addView(headerChild);
        linearLayoutChild.addView(colorPickerViewForChild);

        linearLayout.addView(linearLayoutParent);
        linearLayout.addView(linearLayoutChild);


        colorPickerViewForParent.addOnColorChangedListener(selectedColor -> {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.rounded_parent_background);
            if (drawable instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                gradientDrawable.setColor(selectedColor);
                colorPickerViewForParent.setBackground(gradientDrawable);
                colorPickerViewForChild.setBackground(gradientDrawable);
                headerParent.setBackground(gradientDrawable);
                headerChild.setBackground(gradientDrawable);

                sharedPreferences.edit().putInt("colorParent", selectedColor).apply();
            }
        });

        colorPickerViewForChild.addOnColorChangedListener(selectedColor -> {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.rounded_child_background);
            if (drawable instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                gradientDrawable.setColor(selectedColor);
                linearLayout.setBackground(gradientDrawable);

                sharedPreferences.edit().putInt("colorChild", selectedColor).apply();
            }
        });
        return linearLayout;
    }

    private LinearLayout createLinearLayoutForSaveNickName(View view) {
        View rootView = requireActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParamsWrapContent = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsWrapContent.setMargins(0,10,0,10);

        LinearLayout linearLayout = new LinearLayout(view.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundResource(R.drawable.rounded_child_background);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(layoutParamsWrapContent);
        linearLayout.setPadding(15,15,15,15);

        EditText editText = new EditText(view.getContext());
        editText.setLayoutParams(layoutParamsWrapContent);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        userRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    if(Objects.equals(firebaseAuth.getCurrentUser().getEmail(), document.getString("email"))){
                        editText.setText(document.getString("nickName"));
                    }
                }
            }
        });
        editText.setHint("Ваш никнейм");
        editText.setPadding(15,25,15,25);
        editText.setTextSize(30);
        editText.setBackgroundResource(R.drawable.rounded_parent_background);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 22){
                    s.delete(22, s.length());
                    Snackbar snackbar = Snackbar.make(rootView, "Никнейм не может быть больше 22х символов!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    View currentFocus = requireActivity().getCurrentFocus();
                    if (currentFocus != null) {
                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    }
                    editText.clearFocus();
                }
            }
        });

        Button buttonSaveNickName = new Button(view.getContext());
        buttonSaveNickName.setText("Сохранить никнейм");
        buttonSaveNickName.setTextSize(20);
        buttonSaveNickName.setPadding(15,25,15,25);
        buttonSaveNickName.setBackgroundResource(R.drawable.rounded_parent_background);
        buttonSaveNickName.setLayoutParams(layoutParamsWrapContent);
        buttonSaveNickName.setOnClickListener(v->{
            userRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        if(Objects.equals(firebaseAuth.getCurrentUser().getEmail(), document.getString("email"))){
                            long rating = (long)document.get("rating");
                            long amountAttempts = (long)document.get("amountAttempts");


                            Object is_bannedFromFireBase = document.get("is_banned");
                            boolean is_banned = (boolean)is_bannedFromFireBase;
                            String dateUnban = document.getString("dateUnban");

                            User user = new User(
                                    editText.getText().toString(),
                                    document.getString("email"),
                                    document.getString("password"),
                                    (int)rating,
                                    false,
                                    document.getString("dateUpdate"),
                                    (int)amountAttempts,
                                    document.getString("role"),
                                    document.getString("dateRegistration"),
                                    is_banned,
                                    dateUnban
                            );

                            userRef.document(document.getId()).set(user);

                            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            View currentFocus = requireActivity().getCurrentFocus();
                            if (currentFocus != null) {
                                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                            }
                            editText.clearFocus();

                            Snackbar snackbar = Snackbar.make(rootView, "Никнейм изменён", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                }
            });
        });
        linearLayout.addView(editText);
        linearLayout.addView(buttonSaveNickName);
        return linearLayout;
    }

    private void signOut(View view){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        SharedPreferences settings = view.getContext().getSharedPreferences("AuthData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(view.getContext(), AuthorizationActivity.class);
        startActivity(intent);
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