package com.ios.storymaker;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.ios.storymaker.databinding.UrlBinding;

public class UrlActivity extends AppCompatActivity {

  private UrlBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // layout inflate in binding
    binding = UrlBinding.inflate(getLayoutInflater());
    // set content view to binding's root
    setContentView(binding.getRoot());
    // when opened iosStoryMaker app from instagram's share option
    onReceivedUrlFromOutside();
    
    // next button on clicked listener
    binding.button1.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (binding.edittext1.getText().toString().trim().length() > 0) {
              if (AppUtil.isPermissionGranted(UrlActivity.this)) {
                onStorageAlreadyGranted();
              } else {
                onStorageDenied();
              }
            }
          }
        });

    // edittext on editor action listener
    binding.edittext1.setOnEditorActionListener(
        new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // keyboard's done button on clicked
            if (actionId == EditorInfo.IME_ACTION_DONE) {
              // click next button automattically
              binding.button1.performClick();
              return true;
            }
            return false;
          }
        });
        
    // cutpaste button on clicker listener    
    binding.cutpaste.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (binding.imageview1.getTag() != null) {
              if (binding.imageview1.getTag().toString().equals("cut")) {
                binding.edittext1.setText("");
              } else {
                AppUtil.getClipBoard(getApplicationContext(), binding.edittext1);
              }
            } else {
              AppUtil.getClipBoard(getApplicationContext(), binding.edittext1);
            }
          }
        });
        
    // edittext on changed listener    
    binding.edittext1.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

          @Override
          public void onTextChanged(CharSequence param1, int arg1, int arg2, int arg3) {
            final String charSeq = param1.toString();
            if (charSeq.trim().length() > 0) {
              binding.imageview1.setImageResource(R.drawable.cut);
              binding.imageview1.setTag("cut");
            } else {
              binding.imageview1.setImageResource(R.drawable.paste);
              binding.imageview1.setTag("paste");
            }
          }

          @Override
          public void afterTextChanged(Editable arg0) {}
        });
  }
  
  // On DownloadComplete Broadcast Receiver
  protected BroadcastReceiver onDownloadComplete =
      new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          final String id = intent.getAction();
          if (id.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            if (FileUtil.isExistFile(
                "storage/emulated/0/"
                    + Environment.DIRECTORY_DOWNLOADS
                    + "/"
                    + DownloadUtil.filename)) {
              binding.button1.setText("NEXT");
              binding.button1.setEnabled(true);
              if (DownloadUtil.filename.contains(".mp4")) {
                final Intent MainPage = new Intent();
                MainPage.putExtra(
                    "videopath",
                    "storage/emulated/0/"
                        + Environment.DIRECTORY_DOWNLOADS
                        + "/"
                        + DownloadUtil.filename);
                MainPage.putExtra("username", DownloadUtil.username);
                MainPage.setClass(context, VideoActivity.class);
                MainPage.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(MainPage);
              } else {
                final Intent ImagePage = new Intent();
                ImagePage.putExtra(
                    "imagepath",
                    "storage/emulated/0/"
                        + Environment.DIRECTORY_DOWNLOADS
                        + "/"
                        + DownloadUtil.filename);
                ImagePage.putExtra("username", DownloadUtil.username);
                ImagePage.setClass(context, ImageActivity.class);
                ImagePage.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(ImagePage);
              }
            } else {
              binding.button1.setText("NEXT");
              binding.button1.setEnabled(true);
              AppUtil.showSnackbar(binding.linear1, "Failed");
            }
          }
        }
      };

  protected void onStorageAlreadyGranted() {
    AppUtil.hideKeyboard(UrlActivity.this);
    java.util.concurrent.ExecutorService executor =
        java.util.concurrent.Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            handler.post(
                new Runnable() {
                  @Override
                  public void run() {
                    binding.button1.setText("GETTING..");
                    binding.button1.setEnabled(false);
                  }
                });
            DownloadUtil.downloadContent(
                getApplicationContext(),
                binding.edittext1.getText().toString(),
                binding.button1,
                binding.linear1);
            registerReceiver(
                onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
          }
        });
  }

  protected void onStorageDenied() {
    final Intent PermissionPage = new Intent();
    PermissionPage.setClass(getApplicationContext(), PermissionsActivity.class);
    PermissionPage.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    startActivityForResult(PermissionPage, 1);
  }

  public void onReceivedUrlFromOutside() {
    Intent intent = getIntent();
    String receivedText = intent.getStringExtra(Intent.EXTRA_TEXT);
    if (receivedText != null) {
      binding.edittext1.setText(receivedText);
      binding.imageview1.setImageResource(R.drawable.cut);
      binding.imageview1.setTag("cut");
      if (AppUtil.isPermissionGranted(UrlActivity.this)) {
        onStorageAlreadyGranted();
      } else {
        onStorageDenied();
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1) {
      if (resultCode == Activity.RESULT_OK) {
        binding.button1.performClick();
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(onDownloadComplete);
  }
}
