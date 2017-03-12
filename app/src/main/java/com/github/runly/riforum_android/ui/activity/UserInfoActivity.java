package com.github.runly.riforum_android.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.interfaces.OnChooseGenderListener;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.qiniu.QiniuToken;
import com.github.runly.riforum_android.qiniu.UploadManagerFactory;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.view.ChooseAvatarDialog;
import com.github.runly.riforum_android.ui.view.GenderDialog;
import com.github.runly.riforum_android.utils.ToastUtil;
import com.github.runly.riforum_android.utils.TxtUtils;
import com.github.runly.riforum_android.utils.UnitConvert;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadOptions;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-3-1.
 */

@RuntimePermissions
public class UserInfoActivity extends TopBarActivity {
    private User user;
    private boolean isEdit = false;
    private CircleImageView userAvatar;
    private EditText nameEdit;
    private EditText genderEdit;
    private GenderDialog genderDialog;
    private int gender;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        user = (User) getIntent().getSerializableExtra(Constants.INTENT_USER_DATA);
        init();
    }

    private void init() {
        userAvatar = (CircleImageView) findViewById(R.id.user_info_avatar);

        nameEdit = (EditText) findViewById(R.id.user_info_name_edit);
        genderEdit = (EditText) findViewById(R.id.user_info_gender_edit);
        genderEdit.setKeyListener(null);

        if (user != null) {
            User loggedUser = App.getInstance().getUser();
            if (loggedUser != null && loggedUser.id == user.id) {
                userAvatar.setOnClickListener(v -> {
                    ChooseAvatarDialog avatarDialog = new ChooseAvatarDialog(this, user.avatar);
                    View.OnClickListener onClickListener = view -> UserInfoActivityPermissionsDispatcher.addPhotoWithCheck(this, avatarDialog);
                    avatarDialog.show();
                    avatarDialog.setButtonListener(onClickListener);
                });
            } else {
                userAvatar.setOnClickListener(v -> {
                    ChooseAvatarDialog avatarDialog = new ChooseAvatarDialog(this, user.avatar);
                    View.OnClickListener onClickListener = view -> addPhoto(avatarDialog);
                    avatarDialog.show();
                    LinearLayout linearLayout = (LinearLayout) avatarDialog.findViewById(R.id.action_linear);
                    linearLayout.setVisibility(View.GONE);
                    avatarDialog.setButtonListener(onClickListener);
                });
            }

            OnChooseGenderListener listener = genderInt -> {
                gender = genderInt;
                genderEdit.setText(TxtUtils.whatGender(genderInt));
            };
            genderDialog = new GenderDialog(this, listener, user.gender);

            genderEdit.setOnClickListener(v -> genderDialog.show());


            if (!TextUtils.isEmpty(user.avatar)) {
                String avatarUrl = user.avatar + "?imageView2/1/w/" +
                    UnitConvert.dp2Px(this, Constants.USER_INFO_AVATAR_SIZE) + "/h/" +
                    UnitConvert.dp2Px(this, Constants.USER_INFO_AVATAR_SIZE) + "/format/webp";

                Glide.with(this)
                    .load(avatarUrl)
                    .crossFade()
                    .into(userAvatar);
            }

            nameEdit.setText(user.name);
            genderEdit.setText(TxtUtils.whatGender(user.gender));
            gender = user.gender;

            EditText id = (EditText) findViewById(R.id.user_info_id_edit);
            id.setText(String.valueOf(user.id));
            EditText account = (EditText) findViewById(R.id.user_info_account_edit);

            if (!TextUtils.isEmpty(user.phone))
                account.setText(user.phone);
            else
                account.setText(user.email);

            EditText time = (EditText) findViewById(R.id.user_info_time_edit);
            time.setText(DateFormat.format("yyyy-MM-dd", user.time));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.ALBUM_REQUEST_CODE) {
                try {
                    Uri uri = data.getData();
                    toCutPicture(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == Constants.CROP_REQUEST) {
                Uri uri = data.getData();
                Map<String, Object> map = new HashMap<>();
                map.put("uid", user.id);
                map.put("token", user.token);
                displayDialog();
                RetrofitFactory.getInstance().getQiuniuTokenService().qiniuToken(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if ("1".equals(response.code)) {
                            QiniuToken qiniuToken = response.data;
                            upLoadAvatar(qiniuToken, uri);
                        } else {
                            cancelDialog();
                            ToastUtil.makeShortToast(this, getString(R.string.avatar_modify_failed));
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        cancelDialog();
                        ToastUtil.makeShortToast(this, getString(R.string.avatar_modify_failed));
                    });
            }
        }
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


    private void upLoadAvatar(QiniuToken qiniuToken, Uri uri) {
        UpCompletionHandler handler = (key, info, response) -> {
            String avatarUrl = qiniuToken.getUrl();
            Map<String, Object> map = new HashMap<>();
            map.put("uid", user.id);
            map.put("avatar", avatarUrl);

            RetrofitFactory
                .getInstance()
                .getUserService()
                .modify_avatar(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    if ("1".equals(res.code)) {
                        user = res.data;
                        App.getInstance().setUser(user);
                        if (!TextUtils.isEmpty(user.avatar)) {
                            String avatar_url = user.avatar + "?imageView2/1/w/" +
                                UnitConvert.dp2Px(this, Constants.USER_INFO_AVATAR_SIZE) + "/h/" +
                                UnitConvert.dp2Px(this, Constants.USER_INFO_AVATAR_SIZE) + "/format/webp";
                            Glide
                                .with(this)
                                .load(avatar_url)
                                .crossFade()
                                .into(userAvatar);
                            cancelDialog();
                            ToastUtil.makeShortToast(this, getString(R.string.avatar_modified));
                        } else {
                            cancelDialog();
                            ToastUtil.makeShortToast(this, getString(R.string.avatar_modify_failed));
                        }
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    cancelDialog();
                    ToastUtil.makeShortToast(this, getString(R.string.avatar_modify_failed));
                });
        };

        UploadOptions options = new UploadOptions(null, null, false,
            (key, percent) -> {
                Log.i("progress", percent + "");
                setDialogProgress((int) (percent * 100));
            }, null);

        UploadManagerFactory.getUploadManager().put(uri.getPath(), qiniuToken.key, qiniuToken.token, handler, options);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void addPhoto(Dialog dialog) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Constants.ALBUM_REQUEST_CODE);
        dialog.cancel();
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForWriteExternalStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
            .setMessage(R.string.permission_camera_rationale)
            .setPositiveButton(R.string.button_allow, (dialog, button) -> request.proceed())
            .setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
            .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForWriteExternalStorage() {
        ToastUtil.makeShortToast(this, getString(R.string.no_camera_permission));
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForWriteExternalStorage() {
        ToastUtil.makeShortToast(this, getString(R.string.never_ask_again));
    }

    private void toCutPicture(Uri uri) {
        Intent intent = new Intent(this, PictureCutActivity.class);
        intent.setData(uri);
        startActivityForResult(intent, Constants.CROP_REQUEST);
    }

    @Override
    protected void onStart() {
        super.onStart();

        topBar.getTxtLeft().setText("个人资料");

        User loginUser = App.getInstance().getUser();
        if (loginUser == null) {
            return;
        }

        if (user.id == loginUser.id) {

            topBar.getTxtRight().setText("修改");

            topBar.getTxtRight().setOnClickListener(v -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!isEdit) {
                    topBar.getTxtRight().setText("完成");
                    nameEdit.setFocusableInTouchMode(true);
                    nameEdit.setEnabled(true);
                    genderEdit.setEnabled(true);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    isEdit = true;

                } else {
                    topBar.getTxtRight().setText("修改");
                    nameEdit.setEnabled(false);
                    genderEdit.setEnabled(false);
                    isEdit = false;

                    if (user != null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("uid", user.id);
                        String name;
                        name = nameEdit.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            ToastUtil.makeShortToast(this, getString(R.string.user_info_modify_empty));
                            return;
                        }
                        map.put("name", name);
                        map.put("gender", gender);

                        RetrofitFactory
                            .getInstance()
                            .getUserService()
                            .modify_info(map)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                if ("1".equals(response.code)) {
                                    user = response.data;
                                    App.getInstance().setUser(user);
                                    ToastUtil.makeShortToast(this, "修改成功");
                                }
                            }, Throwable::printStackTrace);
                    }

                }
            });
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_USER_DATA, user);
        setResult(RESULT_OK, intent);
        super.finish();
    }
}
