package com.example.fruitninja;

public class scoresTable
{
    int number;
    String user;;
    int score;

    public scoresTable(int number, String user, int score) {
        this.number = number;
        this.user = user;
        this.score = score;
    }

    public int getNumber() {
        return number;
    }

    public String getUser() {
        return user;
    }

    public int getScores() {
        return score;
    }
}
