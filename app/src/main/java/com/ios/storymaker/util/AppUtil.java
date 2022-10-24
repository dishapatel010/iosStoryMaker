package com.ios.storymaker;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AppUtil {

  private static Timer timer = new Timer();
  private static TimerTask countdown;

  public static void hideKeyboard(@NonNull Activity activity) {
    View view = activity.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm =
          (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  public static boolean isPermissionGranted(@NonNull Activity activity) {
    return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED;
  }

  public static void GradientBackgroundColor(
      @NonNull Activity activity, LinearLayout linear, Integer color1, Integer color2) {
    Window window = activity.getWindow();
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    final int statusbarcolor = color1;
    final int navigationbarcolor = color2;
    window.setStatusBarColor(statusbarcolor);
    window.setNavigationBarColor(navigationbarcolor);
    GradientDrawable gd001 =
        new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[] {statusbarcolor, navigationbarcolor});
    linear.setBackgroundDrawable(gd001);
  }

  public static void getClipBoard(Context context, EditText edittext) {
    android.content.ClipboardManager clipboard =
        (android.content.ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
    ClipData clipData = clipboard.getPrimaryClip();
    if (clipData != null) {
      edittext.setText(clipData.getItemAt(0).getText().toString());
    }
  }

  public static void showSnackbar(LinearLayout linear, String message) {
    Snackbar snackbar = Snackbar.make(linear, message, Snackbar.LENGTH_LONG);
    snackbar.setAction(
        "Retry",
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {}
        });
    snackbar.setDuration(3000);
    snackbar.show();
  }

  public static void setVideoLeftTime(VideoView videoview, TextView textview) {
    countdown =
        new TimerTask() {
          @Override
          public void run() {
            new Handler(Looper.getMainLooper())
                .post(
                    new Runnable() {
                      @Override
                      public void run() {
                        final long leftvideodurationInMillis =
                            videoview.getDuration() - videoview.getCurrentPosition();
                        final String leftvideodurationInTimeFormat =
                            String.format(
                                Locale.getDefault(),
                                "%2d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(leftvideodurationInMillis)
                                    - TimeUnit.HOURS.toMinutes(
                                        TimeUnit.MILLISECONDS.toHours(leftvideodurationInMillis)),
                                TimeUnit.MILLISECONDS.toSeconds(leftvideodurationInMillis)
                                    - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(
                                            leftvideodurationInMillis)));
                        textview.setText(leftvideodurationInTimeFormat);
                      }
                    });
          }
        };
    timer.scheduleAtFixedRate(countdown, (int) (0), (int) (700));
  }
}
