package com.example.fruitninja;

public class User
{
    private String name;
    private String email;
    private int bestScore;
    private boolean check;
    private String date;
    private int place;

    public User(){ }

    public User(String email, String name, int bestScore, boolean check, String date, int place) {
        this.email = email;
        this.name = name;
        this.bestScore = bestScore;
        this.check = check;
        this.date = date;
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }
}
