package com.github.runly.riforum_android.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class CircularImageView extends MaskedImage {

    public CircularImageView(Context paramContext) {

        super(paramContext);

    }

    public CircularImageView(Context paramContext, AttributeSet paramAttributeSet) {

        super(paramContext, paramAttributeSet);

    }

    public CircularImageView(Context paramContext, AttributeSet paramAttributeSet,
                             int paramInt) {

        super(paramContext, paramAttributeSet, paramInt);

    }


    @Override
    public Bitmap createMask() {

        int i = getWidth();

        int j = getHeight();

        Bitmap.Config localConfig = Bitmap.Config.ARGB_4444;
        try {
            if (null == maskBitmap || maskBitmap.isRecycled() || i != maskBitmap.getWidth()) {
                maskBitmap = Bitmap.createBitmap(i, j, localConfig);
            }

//		Bitmap localBitmap = Bitmap.createBitmap(i, j, localConfig);

            Canvas localCanvas = new Canvas(maskBitmap);
//            Paint localPaint = new Paint(1);
//            localPaint.setColor(-16777216);

            Paint localPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

//            localPaint.setColor(Color.TRANSPARENT);
            localPaint.setColor(Color.parseColor("#f5f5f5"));

            float f1 = getWidth();

            float f2 = getHeight();

            RectF localRectF = new RectF(0.0F, 0.0F, f1, f2);

            localCanvas.drawOval(localRectF, localPaint);

            return maskBitmap;
        } catch (OutOfMemoryError error) {
            return null;
        }
    }

}