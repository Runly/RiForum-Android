package com.github.runly.riforum_android.ui.activity;

import android.content.Intent;
import android.content.pm.InstrumentationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.oginotihiro.cropview.CropUtil;
import com.oginotihiro.cropview.CropView;

import java.io.File;

import static android.R.attr.x;
import static android.R.attr.y;

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
        Uri srouceUri = getIntent().getData();

        CropView cropView = (CropView) findViewById(R.id.cropView);
        cropView.of(srouceUri)
                .withAspect(0, 0)
                .withOutputSize(400, 400)
                .initialize(this);

        Button sureButton = (Button) findViewById(R.id.sure);
        sureButton.setOnClickListener(v -> {
            new Thread() {
                public void run() {
                    Bitmap croppedBitmap = cropView.getOutput();

                    Uri destination = Uri.fromFile(new File(Constants.SAVED_AVATAR_DIR_PATH));
                    CropUtil.saveOutput(PictureCutActivity.this, destination, croppedBitmap, 90);
                }
            }.start();


            Bitmap croppedBitmap = cropView.getOutput();
            Uri uri = Uri.parse(Constants.SAVED_AVATAR_DIR_PATH);
            CropUtil.saveOutput(this, uri, croppedBitmap, 100);
            setResult(RESULT_OK);
        });

    }

}
