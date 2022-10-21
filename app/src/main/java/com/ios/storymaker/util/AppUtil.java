package com.ios.storymaker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import java.util.Timer;
import java.util.TimerTask;

public class AppUtil {

  public static TimerTask countdown;
  public static double currentduration = 0;

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

  public static void CountDown(TextView textview, Double duration) {
    Timer _timer = new Timer();
    currentduration = duration;
    countdown =
        new TimerTask() {
          @Override
          public void run() {
            new Handler(Looper.getMainLooper())
                .post(
                    new Runnable() {
                      @Override
                      public void run() {
                        if (currentduration == 0) {
                          currentduration = 15;
                          if (currentduration < 11) {
                            currentduration--;
                            textview.setText(
                                "0:0".concat(String.valueOf((long) (currentduration))));
                          } else {
                            currentduration--;
                            textview.setText("0:".concat(String.valueOf((long) (currentduration))));
                          }
                        } else {
                          if (currentduration < 11) {
                            currentduration--;
                            textview.setText(
                                "0:0".concat(String.valueOf((long) (currentduration))));
                          } else {
                            currentduration--;
                            textview.setText("0:".concat(String.valueOf((long) (currentduration))));
                          }
                        }
                      }
                    });
          }
        };
    _timer.scheduleAtFixedRate(countdown, (int) (0), (int) (1000));
  }
}
