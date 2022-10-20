package com.ios.storymaker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.ios.storymaker.FileUtil;
import com.ios.storymaker.databinding.ImageBinding;

public class ImageActivity extends AppCompatActivity {

  private ImageBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ImageBinding.inflate(getLayoutInflater());
    // set content view to binding's root
    setContentView(binding.getRoot());
    binding.imageview1.setImageBitmap(
        FileUtil.decodeSampleBitmapFromPath(getIntent().getStringExtra("imagepath"), 1024, 1024));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    FileUtil.deleteFile(getIntent().getStringExtra("imagepath"));
  }
}
