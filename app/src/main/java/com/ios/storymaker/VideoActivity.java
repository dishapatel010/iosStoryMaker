package com.ios.storymaker;

import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.media.MediaPlayer;
import com.ios.storymaker.databinding.VideoBinding;

public class VideoActivity extends AppCompatActivity {

  private VideoBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Inflate and get instance of binding
    binding = VideoBinding.inflate(getLayoutInflater());
    // set content view to binding's root
    setContentView(binding.getRoot());
    // Add a full screen gradient background color
    AppUtil.GradientBackgroundColor(VideoActivity.this, binding.linear1, 0xFFFFC107, 0xFF424242);
    // set username (sent from previous activity)
    binding.textview2.setText(getIntent().getStringExtra("username"));
    // remove videoview controllers
    binding.videoview1.setMediaController(null);
    // set video path (sent from previous activity)
    binding.videoview1.setVideoURI(Uri.parse(getIntent().getStringExtra("videopath")));
    // start playing the video
    binding.videoview1.start();
    
    // video view on completed listener
    binding.videoview1.setOnCompletionListener(
        new MediaPlayer.OnCompletionListener() {
          @Override
          public void onCompletion(MediaPlayer mediaPlayer) {
            // restart playing video again on finished playing  
            binding.videoview1.start();
          }
        });
    
    // videoview on prepared listener    
    binding.videoview1.setOnPreparedListener(
        new MediaPlayer.OnPreparedListener() {
          @Override
          public void onPrepared(MediaPlayer mediaplayer) {
            // set video left duration  
            AppUtil.setVideoLeftTime(binding.videoview1, binding.textview1);
          }
        });
  }

  @Override
  public void onResume() {
    super.onResume();
    // on resuming activity, video stops playing ↷
    // so we restart playing the video again
    binding.videoview1.start();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    // on activity removed we delete the video file ↷
    // that has been downloaded
    FileUtil.deleteFile(getIntent().getStringExtra("videopath"));
  }
}
