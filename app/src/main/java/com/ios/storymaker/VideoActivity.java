package com.ios.storymaker;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.media.MediaPlayer;
import android.view.WindowManager;
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

    // registerReceiver(onDownloadComplete,new
    // IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    Window window = this.getWindow();
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(Color.parseColor("#FFC107"));
    window.setNavigationBarColor(Color.parseColor("#424242"));
    GradientDrawable gd001 =
        new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, new int[] {0xFFFFC107, 0xFF424242});
    binding.linear1.setBackgroundDrawable(gd001);

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
