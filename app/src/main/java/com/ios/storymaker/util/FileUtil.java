package com.ios.storymaker;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.io.*;

public class FileUtil {

  public static File file;

  private static void createNewFile(String path) {
    int lastSep = path.lastIndexOf(File.separator);
    if (lastSep > 0) {
      String dirPath = path.substring(0, lastSep);
      makeDir(dirPath);
    }
    File file = new File(path);
    try {
      if (!file.exists()) file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void deleteFile(String path) {
    File file = new File(path);
    if (!file.exists()) return;
    if (file.isFile()) {
      file.delete();
      return;
    }
    File[] fileArr = file.listFiles();
    if (fileArr != null) {
      for (File subFile : fileArr) {
        if (subFile.isDirectory()) {
          deleteFile(subFile.getAbsolutePath());
        }

        if (subFile.isFile()) {
          subFile.delete();
        }
      }
    }
    file.delete();
  }

  public static boolean isExistFile(String path) {
    File file = new File(path);
    return file.exists();
  }

  public static void makeDir(String path) {
    if (!isExistFile(path)) {
      File file = new File(path);
      file.mkdirs();
    }
  }

  public static String convertUriToFilePath(final Context context, final Uri uri) {
    String path = null;
    if (DocumentsContract.isDocumentUri(context, uri)) {
      if (isExternalStorageDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];
        if ("primary".equalsIgnoreCase(type)) {
          path = Environment.getExternalStorageDirectory() + "/" + split[1];
        }
      } else if (isDownloadsDocument(uri)) {
        final String id = DocumentsContract.getDocumentId(uri);
        if (!TextUtils.isEmpty(id)) {
          if (id.startsWith("raw:")) {
            return id.replaceFirst("raw:", "");
          }
        }
        final Uri contentUri =
            ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
        path = getDataColumn(context, contentUri, null, null);
      } else if (isMediaDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        Uri contentUri = null;
        if ("image".equals(type)) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        final String selection = "_id=?";
        final String[] selectionArgs = new String[] {split[1]};
        path = getDataColumn(context, contentUri, selection, selectionArgs);
      }
    } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
      path = getDataColumn(context, uri, null, null);
    } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
      path = uri.getPath();
    }

    if (path != null) {
      try {
        return URLDecoder.decode(path, "UTF-8");
      } catch (Exception e) {
        return null;
      }
    }
    return null;
  }

  private static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  private static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  private static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  private static String getDataColumn(
      Context context, Uri uri, String selection, String[] selectionArgs) {
    final String column = MediaStore.Images.Media.DATA;
    final String[] projection = {column};
    try (Cursor cursor =
        context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
      if (cursor != null && cursor.moveToFirst()) {
        final int column_index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(column_index);
      }
    } catch (Exception e) {
    }
    return null;
  }

  public static void writeFile(String path, String str) {
    createNewFile(path);
    FileWriter fileWriter = null;

    try {
      fileWriter = new FileWriter(new File(path), false);
      fileWriter.write(str);
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fileWriter != null) fileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void rename(String path, String to) {
    new File(path)
        .renameTo(new File(path.replace(Uri.parse(path).getLastPathSegment(), "").concat(to)));
  }

  public static String getPackageDataDir(Context context) {
    return context.getExternalFilesDir(null).getAbsolutePath();
  }

  public static int calculateInSampleSize(
      BitmapFactory.Options options, int reqWidth, int reqHeight) {
    final int width = options.outWidth;
    final int height = options.outHeight;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
        inSampleSize *= 2;
      }
    }
    return inSampleSize;
  }

  public static Bitmap decodeSampleBitmapFromPath(String path, int reqWidth, int reqHeight) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(path, options);

    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFile(path, options);
  }
}
