package com.brightcove.player.samples.appcompat.fragment;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        PlayerFragmentListener playerFragmentListener = getPlayerFragmentListener();
        if (playerFragmentListener != null) {
            playerFragmentListener.onUserLeaveHint();
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode,
                                              Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

        PlayerFragmentListener playerFragmentListener = getPlayerFragmentListener();
        if (playerFragmentListener != null) {
            playerFragmentListener.onActivityPictureInPictureModeChanged(
                    isInPictureInPictureMode,
                    newConfig);
        }
    }

    private PlayerFragmentListener getPlayerFragmentListener() {
        return (PlayerFragmentListener)
                getSupportFragmentManager().findFragmentById(R.id.brightcove_player_fragment);
    }

    public interface PlayerFragmentListener {
        void onUserLeaveHint();
        void onActivityPictureInPictureModeChanged(boolean isInPictureInPictureMode,
                                                   Configuration newConfig);
    }
}