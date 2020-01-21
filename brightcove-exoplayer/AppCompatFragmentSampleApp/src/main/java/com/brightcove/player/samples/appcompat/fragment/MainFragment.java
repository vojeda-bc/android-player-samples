package com.brightcove.player.samples.appcompat.fragment;

import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.brightcove.player.appcompat.BrightcovePlayerFragment;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.pictureinpicture.PictureInPictureManager;

public class MainFragment extends BrightcovePlayerFragment implements MainActivity.PlayerFragmentListener {

    public static final String TAG = MainFragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_main, container, false);
        baseVideoView = result.findViewById(R.id.brightcove_video_view);
        super.onCreateView(inflater, container, savedInstanceState);

        Button settingsButton = result.findViewById(R.id.configure_pip);
        settingsButton.setOnClickListener(this::onClickConfigurePictureInPicture);

        Video video = Video.createVideo("http://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4);
        baseVideoView.add(video);
        baseVideoView.getAnalytics().setAccount("1760897681001");
        baseVideoView.start();

        return result;
    }

    @Override
    public void onResume() {
        super.onResume();

        // AND-1752 Note: Register the parent activity.
        attemptToRegisterPiPActivity();

        SettingsModel settingsModel = new SettingsModel(getContext());

        // AND-1752 Note: Configure Picture in Picture
        PictureInPictureManager manager = PictureInPictureManager.getInstance();
        manager.setClosedCaptionsEnabled(settingsModel.isPictureInPictureClosedCaptionsEnabled())
                .setOnUserLeaveEnabled(settingsModel.isPictureInPictureOnUserLeaveEnabled())
                .setClosedCaptionsReductionScaleFactor(settingsModel.getPictureInPictureCCScaleFactor())
                .setAspectRatio(settingsModel.getPictureInPictureAspectRatio());
    }

    private void onClickConfigurePictureInPicture(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(),
                    "Picture-in-Picture is currently available only on Android Oreo or Higher",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void attemptToRegisterPiPActivity() {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.setPictureInPictureParams((new PictureInPictureParams.Builder()).build());
                    PictureInPictureManager.getInstance().registerActivity(activity, baseVideoView);
                }
            } catch (IllegalStateException var2) {
                Log.w(TAG, "This activity was not set to use Picture-in-Picture.");
            }
        }
    }

    @Override
    public void onUserLeaveHint() {
        PictureInPictureManager.getInstance().onUserLeaveHint();
    }

    @Override
    public void onActivityPictureInPictureModeChanged(boolean isInPictureInPictureMode,
                                                      Configuration newConfig) {
        // AND-1752 Note: If using AppCompatActivity add this code
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null) {
            ActionBar actionBar = appCompatActivity.getSupportActionBar();
            if (actionBar != null) {
                if (isInPictureInPictureMode) {
                    actionBar.hide();
                } else {
                    actionBar.show();
                }
            }
        }

        // AND-1752 Note: Going to PiP mode.
        PictureInPictureManager.getInstance().onPictureInPictureModeChanged(
                isInPictureInPictureMode,
                newConfig);
    }
}
