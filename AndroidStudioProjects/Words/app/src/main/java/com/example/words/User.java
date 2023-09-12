package com.example.words;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User implements Serializable {
    private String role;
    private String NickName;
    private String Email;
    private String Password;
    private int Rating;
    private boolean is_deleted;
    private boolean is_banned;
    private String dateUnban;
    private String dateUpdate;
    private int amountAttempts;

    private String dateRegistration;

    public User(String email, String password, String nickName, int amountAttempts, String dateUpdate, String currentDate){
        this.NickName = nickName;
        this.Email = email;
        this.Password = password;
        this.amountAttempts = amountAttempts;
        this.dateUpdate = dateUpdate;
        this.Rating = 0;
        this.is_deleted = false;
        this.role = "user";
        this.is_banned = false;
        this.dateUnban = null;
        this.dateRegistration = currentDate;
    }

    public User(String nickName, String email, String password, int rating, boolean is_deleted, String dateUpdate, int amountAttempts,
                String role, String dateReg, boolean is_banned, String dateUnban){
        this.NickName = nickName;
        this.Email = email;
        this.Password = password;
        this.Rating = rating;
        this.is_deleted = is_deleted;
        this.dateUpdate = dateUpdate;
        this.amountAttempts = amountAttempts;
        this.role = role;
        this.dateRegistration = dateReg;
        this.is_banned = is_banned;
        this.dateUnban = dateUnban;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public int getAmountAttempts() {
        return amountAttempts;
    }

    public void setAmountAttempts(int amountAttempts) {
        this.amountAttempts = amountAttempts;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDateRegistration() {
        return dateRegistration;
    }

    public void setDateRegistration(String dateRegistration) {
        this.dateRegistration = dateRegistration;
    }

    public boolean isIs_banned() {
        return is_banned;
    }

    public void setIs_banned(boolean is_banned) {
        this.is_banned = is_banned;
    }

    public String getDateUnban() {
        return dateUnban;
    }

    public void setDateUnban(String dateUnban) {
        this.dateUnban = dateUnban;
    }
}
