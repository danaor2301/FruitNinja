package com.example.fruitninja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OpeningActivity extends AppCompatActivity
{
    static MediaPlayer themeSound, mp_click, mp_bomb, mp_lose, mp_slice, mp_woosh, mp_wooshBomb,
            mp_startGame, mp_gameOver, mp_pause, mp_unPause, mp_newBestScore;
    final static float volume = (float) (1 - (Math.log(100 - 70) / Math.log(100))),
            volume_max = (float) (1 - (Math.log(100 - 100) / Math.log(100)));
    static ArrayList<MediaPlayer> mpList = new ArrayList<>();
    static ArrayList<MediaPlayer> mpClicksList = new ArrayList<>();
    static boolean soundCheck, musicCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        soundCheck = true;
        musicCheck = true;

        // Media player
        mp_click = MediaPlayer.create(this, R.raw.click2_sound);
        themeSound = MediaPlayer.create(this, R.raw.theme_sound);
        mp_bomb = MediaPlayer.create(this, R.raw.bomb2_sound);
        mp_lose = MediaPlayer.create(this, R.raw.lose2_sound);
        mp_slice = MediaPlayer.create(this, R.raw.slice2_sound);
        mp_woosh = MediaPlayer.create(this, R.raw.fruit_throw_sound);
        mp_wooshBomb = MediaPlayer.create(this, R.raw.woosh_bomb_sound);
        mp_startGame = MediaPlayer.create(this, R.raw.game_start_sound);
        mp_gameOver = MediaPlayer.create(this, R.raw.game_over_sound);
        mp_pause = MediaPlayer.create(this, R.raw.pause_sound);
        mp_unPause = MediaPlayer.create(this, R.raw.unpause_sound);
        mp_newBestScore = MediaPlayer.create(this, R.raw.new_best_score_sound);

        // set volume of media player
        mp_bomb.setVolume(volume, volume);
        mp_lose.setVolume(volume_max, volume_max);
        mp_slice.setVolume(volume, volume);
        mp_woosh.setVolume(volume, volume);
        mp_wooshBomb.setVolume(volume, volume);
        mp_startGame.setVolume(volume, volume);
        mp_gameOver.setVolume(volume, volume);
        mp_pause.setVolume(volume, volume);
        mp_unPause.setVolume(volume, volume);
        themeSound.setVolume(volume, volume);
        mp_newBestScore.setVolume(volume_max, volume_max);
        mp_click.setVolume(volume_max, volume_max);

        mpList.add(mp_gameOver); mpList.add(mp_bomb); mpList.add(mp_lose); mpList.add(mp_slice);
        mpList.add(mp_woosh); mpList.add(mp_wooshBomb); mpList.add(mp_startGame);
        mpClicksList.add(mp_pause); mpClicksList.add(mp_unPause); mpClicksList.add(mp_click);

        themeSound.start();
        themeSound.setLooping(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(OpeningActivity.this, signInActivity.class));
                finish();
            }
        }, 1500);
    }
}