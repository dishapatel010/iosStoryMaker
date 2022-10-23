package com.ios.storymaker;

import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.media.MediaPlayer;
import com.ios.storymaker.databinding.VideoBinding;

public class VideoActivity extends AppCompatActivity {

  private VideoBinding binding;
  private long lefttimetocompletevideo = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Inflate and get instance of binding
    binding = VideoBinding.inflate(getLayoutInflater());
    // set content view to binding's root
    setContentView(binding.getRoot());
    AppUtil.GradientBackgroundColor(VideoActivity.this, binding.linear1, 0xFFFFC107, 0xFF424242);
    binding.textview2.setText(getIntent().getStringExtra("username"));
    binding.videoview1.setMediaController(null);
    binding.videoview1.setVideoURI(Uri.parse(getIntent().getStringExtra("videopath")));
    binding.videoview1.start();
    binding.videoview1.setOnCompletionListener(
        new MediaPlayer.OnCompletionListener() {
          @Override
          public void onCompletion(MediaPlayer mediaPlayer) {
            binding.videoview1.start();
          }
        });
    binding.videoview1.setOnPreparedListener(
        new MediaPlayer.OnPreparedListener() {
          @Override
          public void onPrepared(MediaPlayer mediaplayer) {
            AppUtil.setVideoLeftTime(binding.videoview1, binding.textview1);
          }
        });
  }

  @Override
  public void onResume() {
    super.onResume();
    binding.videoview1.start();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    FileUtil.deleteFile(getIntent().getStringExtra("videopath"));
  }
}
