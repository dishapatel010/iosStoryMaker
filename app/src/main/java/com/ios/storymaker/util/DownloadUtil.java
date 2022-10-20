package com.ios.storymaker;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONException;
import org.json.JSONObject;

public class DownloadUtil {

  public static long downloadId = 0;

  public static String filename = "";

  public static String RootDirectoryInstagram = "/";

  public static String username = "";

  public static void downloadContent(
      Context context, String postUrl, Button button, LinearLayout linear) {
    String replacedUrl;
    final String[] finalPostUrl = new String[1];
    RequestQueue requestQueue;
    requestQueue = Volley.newRequestQueue(context);
    if (TextUtils.isEmpty(postUrl)) {
      Log.e("VideoURLErrors", "Provided String is empty.");
    } else {
      replacedUrl = postUrl.split("\\?")[0] + "?__a=1&__d=dis";
      JsonObjectRequest jsonObjectRequest =
          new JsonObjectRequest(
              Request.Method.GET,
              replacedUrl,
              null,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  JSONObject Obj1 = null;
                  try {
                    Obj1 = response.getJSONObject("graphql");
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  JSONObject Obj2 = null;
                  try {
                    Obj2 = Obj1.getJSONObject("shortcode_media");
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  JSONObject Obj3 = null;
                  try {
                    Obj3 = Obj2.getJSONObject("owner");
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  try {
                    if (Obj2.has("video_url")) {
                      finalPostUrl[0] = Obj2.getString("video_url");
                      filename = System.currentTimeMillis() + ".mp4";
                    } else {
                      finalPostUrl[0] = Obj2.getString("display_url");
                      filename = System.currentTimeMillis() + ".jpg";
                    }
                    if (Obj3.has("username")) {
                      username = Obj3.getString("username");
                    }
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  download(finalPostUrl[0], RootDirectoryInstagram, context, filename);
                }
              },
              new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  button.setText("NEXT");
                  if (error instanceof NoConnectionError
                      || error instanceof NetworkError
                      || error instanceof AuthFailureError) {
                    Snackbar snackbar = Snackbar.make(linear, "No Internet", Snackbar.LENGTH_LONG);
                    snackbar.setAction(
                        "Retry",
                        new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {}
                        });
                    snackbar.setDuration(3000);
                    snackbar.show();
                  } else {
                    Snackbar snackbar =
                        Snackbar.make(linear, error.toString(), Snackbar.LENGTH_LONG);
                    snackbar.setAction(
                        "Retry",
                        new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {}
                        });
                    snackbar.setDuration(3000);
                    snackbar.show();
                  }
                }
              });
      requestQueue.add(jsonObjectRequest);
    }
  }

  public static void download(
      String downloadPath, String destinationPath, Context context, String fileName) {
    Uri uri = Uri.parse(downloadPath);
    DownloadManager.Request request = new DownloadManager.Request(uri);
    request.setAllowedNetworkTypes(
        DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
    request.setTitle(fileName);
    request.setDestinationInExternalPublicDir(
        Environment.DIRECTORY_DOWNLOADS, destinationPath + fileName);
    downloadId =
        ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
  }
}