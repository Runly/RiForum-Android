package com.github.runly.riforum_android.ui.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.runly.richedittext.RichEditText;
import com.github.runly.richedittext.span.FakeImageSpan;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.utils.Constant;
import com.github.runly.riforum_android.utils.UnitConvert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ranly on 17-2-8.
 */

public class ReleaseActivity extends TopBarActivity {
    private RichEditText richEditText;
    private String cameraPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        init();
    }

    private void init() {
        richEditText = (RichEditText) findViewById(R.id.content_edit_text);

        findViewById(R.id.add_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Constant.ALBUM_REQUEST_CODE);
            }
        });

        findViewById(R.id.open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 指定相机拍摄照片保存地址
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    CharSequence presentDate =android.text.format.DateFormat.
                            format("yyyy  MMddkk:mm:ss",  System.currentTimeMillis());

                    cameraPath = Constant.SAVED_IMAGE_DIR_PATH +
                            presentDate + ".jpg";
                    Intent intent = new Intent();
                    // 指定开启系统相机的Action
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    String out_file_path = Constant.SAVED_IMAGE_DIR_PATH;
                    File dir = new File(out_file_path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    // 把文件地址转换成Uri格式
                    Uri uri = Uri.fromFile(new File(cameraPath));
                    // 设置系统相机拍摄照片完成后图片文件的存放地址
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, Constant.CAMERA_REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "请确认已经插入SD卡",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setRichTextContent(String richText) {
        richEditText.setRichText(richText);
        FakeImageSpan[] imageSpans = richEditText.getFakeImageSpans();
        if (imageSpans == null || imageSpans.length == 0) {
            return;
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for (final FakeImageSpan imageSpan : imageSpans) {
            final String src = imageSpan.getValue();
            if (src.startsWith("http")) {
                // web images
                new AsyncTask<String, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        try {
                            InputStream is = new URL(src).openStream();
                            return BitmapFactory.decodeStream(is);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        if (bitmap == null) {
                            return;
                        }
                        richEditText.replaceDownloadedImage(imageSpan, bitmap, src);
                    }
                }.executeOnExecutor(executorService, src);
            } else {
                // local images
                richEditText.replaceLocalImage(imageSpan, src);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.ALBUM_REQUEST_CODE) {
                try {

                    Uri uri = data.getData();
                    final String absolutePath = getAbsolutePath(this, uri);

                    Log.d("uri", "path=" + absolutePath);
                    richEditText.addImage(absolutePath);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == Constant.CAMERA_REQUEST_CODE) {
                richEditText.addImage(cameraPath);
            }

        }
    }

    public String getAbsolutePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA},
                    null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(
                            MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 配置topBar
        topBar.getTxtCenter().setText(getString(R.string.release_txt_center));
        TextView txtRight = topBar.getTxtRight();
        txtRight.setGravity(Gravity.CENTER);
        txtRight.setText(getString(R.string.release_txt_right));
        ViewGroup.LayoutParams layoutParams = txtRight.getLayoutParams();
        layoutParams.width = UnitConvert.dipToPixels(this, 48);
        layoutParams.height = UnitConvert.dipToPixels(this, 24);
        txtRight.setLayoutParams(layoutParams);
        txtRight.setBackground(ContextCompat.getDrawable(this, R.drawable.release_text_border));
        txtRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("content", richEditText.getRichText());
            }
        });
    }
}
