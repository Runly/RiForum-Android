package com.github.runly.riforum_android.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.runly.richedittext.RichEditText;
import com.github.runly.richedittext.span.FakeImageSpan;
import com.github.runly.richedittext.span.ImageSpan;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.qiniu.QiniuToken;
import com.github.runly.riforum_android.qiniu.UploadManagerFactory;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.application.App;
import com.github.runly.riforum_android.utils.Constant;
import com.github.runly.riforum_android.utils.ToastUtil;
import com.github.runly.riforum_android.utils.UnitConvert;
import com.google.gson.Gson;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-8.
 */

public class ReleaseActivity extends TopBarActivity implements View.OnClickListener {
    private RichEditText richEditText;
    private String cameraPath;
    private List<String> pathList = new ArrayList<>();
    private List<String> urlList = new ArrayList<>();
    private int imageNumber = 0;
    private final int[] currentImageIndex = {0};
    private ProgressDialog dialog;
    private double progress = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        init();
    }

    private void init() {
        richEditText = (RichEditText) findViewById(R.id.content_edit_text);
        findViewById(R.id.add_photo).setOnClickListener(this);
        findViewById(R.id.open_camera).setOnClickListener(this);
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
        txtRight.setOnClickListener(this);
    }

    private void openCamera() {
        // 指定相机拍摄照片保存地址
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            CharSequence presentDate = android.text.format.DateFormat.
                    format("yyyyMMdd-kk:mm:ss", System.currentTimeMillis());

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

    private void addPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Constant.ALBUM_REQUEST_CODE);
    }

    private void displayDialog() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(100);
        dialog.setProgress(0);
        dialog.show();
    }

    private void setDialogProgress(int pro) {
        dialog.setProgress(pro);
    }

    private void cancelDialog() {
        if (null != dialog) {
            dialog.cancel();
        }
    }

    private void release() {
        User user = App.getInstance().getUser();
        if (user == null) {
            ToastUtil.makeShortToast(this, getString(R.string.not_login));
            return;
        }

        List<ImageSpan> imageSpanList;
        imageSpanList = richEditText.getToUploadImageSpanList();
        if (imageSpanList != null && imageSpanList.size() > 0) {
            imageNumber = imageSpanList.size();
            Map<String, Object> map = new HashMap<>();
            String uid = String.valueOf(user.id);
            map.put("uid", uid);

            RetrofitFactory.getInstance().getQiuniuTokenService().qiniuToken(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.code.equals("1")) {
                            upLoadImage(imageSpanList.get(currentImageIndex[0]), response.data);
                        } else {

                        }
                    });
        } else {
            pushToMyServer(richEditText.getRichText());
            cancelDialog();
        }

    }

    private void upLoadImage(ImageSpan imageSpan, QiniuToken qiniuToken) {
        UpCompletionHandler handler = (key, info, response) -> {
            if (info.isOK()) {
                pathList.add(imageSpan.getFilePath());
                urlList.add(qiniuToken.getUrl());
                currentImageIndex[0]++;
                if (currentImageIndex[0] < imageNumber) {
                    release();
                } else {
                    currentImageIndex[0] = 0;
                    String content = richEditText.getRichText();
                    for (int i = 0; i < pathList.size(); i++) {
                        content = content.replace(pathList.get(i), urlList.get(i));
                    }
                    pushToMyServer(content);
                }
            } else {
                ToastUtil.makeShortToast(ReleaseActivity.this, getString(R.string.release_failed));
                cancelDialog();
            }
        };

        UploadOptions options = new UploadOptions(null, null, false,
                (key, percent) -> {
                    progress = (99 / imageNumber) * (currentImageIndex[0] + percent);
                    Log.i("progress", progress + "");
                    setDialogProgress((int) progress);
                }, null);

        UploadManagerFactory.getUploadManager()
                .put(imageSpan.getFilePath(), qiniuToken.key, qiniuToken.token, handler, options);


    }

    private void pushToMyServer(String content) {
        Log.i("content", content);

        String title = ((EditText) findViewById(R.id.title_edit_text)).getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            ToastUtil.makeShortToast(this, getString(R.string.release_empty));
            return;
        }

        User user = App.getInstance().getUser();
        if (null != user) {
            Map<String, Object> map = new HashMap<>();
            map.put("token", user.token);
            map.put("uid", user.id);
            map.put("title", title);
            map.put("content", content);
            map.put("plate", 0);
            map.put("sort", 0);
            String image = "";
            if (urlList != null && urlList.size() > 0) {
                Gson gson = new Gson();
                image = gson.toJson(urlList);
            }
            map.put("image", image);

            RetrofitFactory.getInstance().getEntryService().release(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            response -> {
                                if (response.code.equals("1")) {
                                    progress = progress + 1;
                                    setDialogProgress((int) progress);
                                    ToastUtil.makeShortToast(ReleaseActivity.this, response.message);
                                    pathList.clear();
                                    urlList.clear();
                                    cancelDialog();
//                                    finish();
                                } else {
                                    ToastUtil.makeShortToast(ReleaseActivity.this, response.message);
                                }
                            },
                            throwable -> {
                                throwable.printStackTrace();
                                ToastUtil.makeShortToast(ReleaseActivity.this, getString(R.string.release_failed));
                                cancelDialog();
                            }
                    );
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_camera:
//                setRichTextContent("哈哈\n" +
//                        "哈哈哈\n" +
//                        "哈哈哈\n" +
//                        "<img src=\"http://ol1tuu1tl.bkt.clouddn.com/10000001_1487174563.66.jpg\" />");
                openCamera();
                break;

            case R.id.add_photo:
                addPhoto();
                break;

            case R.id.txt_right:
                displayDialog();
                release();
                break;

            default:
                break;
        }
    }


}
