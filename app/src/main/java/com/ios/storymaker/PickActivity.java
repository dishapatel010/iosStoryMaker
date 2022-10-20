package com.ios.storymaker;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.ios.storymaker.PickActivity;
import com.ios.storymaker.databinding.PickBinding;
import com.itsaky.androidide.logsender.LogSender;
import android.view.View;

public class PickActivity extends AppCompatActivity {

  private PickBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    LogSender.startLogging(this);
    super.onCreate(savedInstanceState);
    binding = PickBinding.inflate(getLayoutInflater());
    // set content view to binding's root
    setContentView(binding.getRoot());

    HandleShare();

    binding.button1.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (binding.edittext1.getText().toString().trim().length() > 0) {
              if (isStoragePermissionGranted()) {
                onStorageAlreadyGranted();
              } else {
                onStorageDenied();
              }
            }
          }
        });

    binding.edittext1.setOnEditorActionListener(
        new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
              if (actionId == EditorInfo.IME_ACTION_DONE) {
                  binding.button1.performClick();
              return true;
            }
            return false;
          }
        });
  }

  protected BroadcastReceiver onDownloadComplete =
      new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
          if (DownloadUtil.downloadId == id) {

            binding.button1.setText("NEXT");
            if (DownloadUtil.filename.contains(".mp4")) {
              final Intent MainPage = new Intent();
              MainPage.putExtra(
                  "videopath",
                  "storage/emulated/0/"
                      + Environment.DIRECTORY_DOWNLOADS
                      + "/"
                      + DownloadUtil.filename);
              MainPage.putExtra("username", DownloadUtil.username);
              MainPage.setClass(context, MainActivity.class);
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
          }
        }
      };

  public boolean isStoragePermissionGranted() {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED;
  }

  protected void onStorageAlreadyGranted() {
    AppUtil.hideKeyboard(PickActivity.this);
    // AppUtil.hideKeyboard(getApplicationContext());
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
    startActivity(PermissionPage);
  }

  public void HandleShare() {
    Intent intent = getIntent();
    String receivedText = intent.getStringExtra(Intent.EXTRA_TEXT);
    if (receivedText != null) {
      binding.edittext1.setText(receivedText);
      if (isStoragePermissionGranted()) {
        onStorageAlreadyGranted();
      } else {
        onStorageDenied();
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(onDownloadComplete);
  }
}