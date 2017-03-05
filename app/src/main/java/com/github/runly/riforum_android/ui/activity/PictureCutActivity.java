package com.github.runly.riforum_android.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.oginotihiro.cropview.CropUtil;
import com.oginotihiro.cropview.CropView;

import java.io.File;

/**
 * Created by ranly on 17-3-2.
 */

public class PictureCutActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_cut);
        init();
    }

    private void init(){
        Uri sourceUri = getIntent().getData();

        CropView cropView = (CropView) findViewById(R.id.cropView);
        cropView.of(sourceUri)
                .withOutputSize(Constants.AVATAR_MAX_SIZE, Constants.AVATAR_MAX_SIZE)
                .initialize(this);

        Button sureButton = (Button) findViewById(R.id.sure);
        sureButton.setOnClickListener(v -> {
            new Thread() {
                public void run() {
                    Bitmap croppedBitmap = cropView.getOutput();

                    Uri destination = Uri.fromFile(new File(Constants.SAVED_AVATAR_DIR_PATH));
                    CropUtil.saveOutput(PictureCutActivity.this, destination, croppedBitmap, 100);
                    setResult(RESULT_OK, (new Intent()).setData(destination));
                    finish();
                }
            }.start();

        });

    }

}
