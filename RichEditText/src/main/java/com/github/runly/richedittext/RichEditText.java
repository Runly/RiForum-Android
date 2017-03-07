package com.github.runly.richedittext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.widget.AppCompatEditText;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.github.runly.richedittext.effects.Effects;
import com.github.runly.richedittext.span.EmojiSpan;
import com.github.runly.richedittext.span.FakeImageSpan;
import com.github.runly.richedittext.span.ImageSpan;
import com.github.runly.richedittext.span.UrlSpan;
import com.github.runly.richedittext.utils.BitmapUtils;
import com.github.runly.richedittext.utils.DisplayUtils;
import com.github.runly.richedittext.utils.RichTextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liye
 * @version 4.1.0
 * @since: 15/12/24 下午3:56
 */
public class RichEditText extends AppCompatEditText{

    private static final int IMAGE_MAX_WIDTH = DisplayUtils.getWidthPixels() - DisplayUtils.dp2px(32);
    private static final int IMAGE_MAX_HEIGHT = DisplayUtils.getHeightPixels();

    private Context mContext;

    private RichEditTextListener mListener;

    private IEmojiFactory mIEmojiFactory;

    public RichEditText(Context context) {
        super(context);
        mContext = context;
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setRichEditTextListener(RichEditTextListener listener) {
        mListener = listener;
    }

    public void setEmojiFactory(IEmojiFactory iEmojiFactory) {
        mIEmojiFactory = iEmojiFactory;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (mListener != null) {
            mListener.onSelectionChanged(selStart, selEnd);
        }
    }

    public void addImage(String filePath) {
        SpannableString spannable = new SpannableString("\n<img src=\"" + filePath + "\"/>");
//        Bitmap bitmap = BitmapUtils.decodeScaleImage(filePath, IMAGE_MAX_WIDTH);
        Bitmap bitmap = getScaledBitmap(filePath);
        if (bitmap == null) {
            return;
        }
        ImageSpan span = new ImageSpan(mContext, bitmap, filePath);
        spannable.setSpan(span, 1, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getText().insert(getSelectionStart(), spannable);
    }

    private Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    private int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private Bitmap getScaledBitmap(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        // 获取旋转角度
        int degree = readPictureDegree(filePath);
        if (degree != 0) {
            // 判断图片是否旋转， 是则旋转回来
            bitmap = rotateBitmapByDegree(bitmap, degree);
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int maxHeight = (int) ((float) height / (float) width *  IMAGE_MAX_WIDTH);
        Bitmap scaledBitmap;
        if (DisplayUtils.getHeightPixels() == 1800 && DisplayUtils.getWidthPixels() == 1080) {
            // 适配魅族 1080 × 1800
            if (width < IMAGE_MAX_WIDTH && height > IMAGE_MAX_HEIGHT) {
                int w = IMAGE_MAX_WIDTH - 500;
                maxHeight = (int) ((float) height / (float) width *  w);
                scaledBitmap = BitmapUtils.createScaledBitmap(bitmap, bitmap.getWidth(),
                        bitmap.getHeight(), w, maxHeight);
            } else if (width > IMAGE_MAX_WIDTH) {
                scaledBitmap = BitmapUtils.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(),
                        IMAGE_MAX_WIDTH, maxHeight);
            } else {
                scaledBitmap = bitmap;
            }

        } else if (DisplayUtils.getWidthPixels() >= 1080) {
            // 适配大屏手机
            if (width > IMAGE_MAX_WIDTH) {
                scaledBitmap = BitmapUtils.createScaledBitmap(bitmap, bitmap.getWidth(),
                        bitmap.getHeight(), IMAGE_MAX_WIDTH, maxHeight);
            } else {
                scaledBitmap = bitmap;
            }

        } else {
            // 适配小屏手机
            if (width < IMAGE_MAX_WIDTH && height > IMAGE_MAX_HEIGHT) {
                int w = IMAGE_MAX_WIDTH - DisplayUtils.dp2px(100);
                maxHeight = (int) ((float) height / (float) width *  w);
                scaledBitmap = BitmapUtils.createScaledBitmap(bitmap, bitmap.getWidth(),
                        bitmap.getHeight(), w, maxHeight);
            } else if (width > IMAGE_MAX_WIDTH) {
                scaledBitmap = BitmapUtils.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(),
                        IMAGE_MAX_WIDTH, maxHeight);
            } else {
                scaledBitmap = bitmap;
            }
        }

        return scaledBitmap;
    }

    public void addEmoji(Emoji emoji) {
        SpannableString spannable = new SpannableString(String.format("[%s]", emoji.getName()));
        EmojiSpan span = new EmojiSpan(mContext, emoji);
        spannable.setSpan(span, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getText().insert(getSelectionStart(), spannable);
    }

    public void replaceDownloadedImage(FakeImageSpan span, Bitmap bitmap, String url) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int maxHeight = (int) ((float) height / (float) width *  IMAGE_MAX_WIDTH);
        Bitmap scaledBitmap;
        if (DisplayUtils.getHeightPixels() == 1800 && DisplayUtils.getWidthPixels() == 1080) {
            // 适配魅族 1080 × 1800
            if (width < IMAGE_MAX_WIDTH && height > IMAGE_MAX_HEIGHT) {
                int w = IMAGE_MAX_WIDTH - 500;
                maxHeight = (int) ((float) height / (float) width *  w);
                scaledBitmap = BitmapUtils.createScaledBitmap(bitmap, bitmap.getWidth(),
                        bitmap.getHeight(), w, maxHeight);
            } else if (width > IMAGE_MAX_WIDTH) {
                scaledBitmap = BitmapUtils.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(),
                        IMAGE_MAX_WIDTH, maxHeight);
            } else {
                scaledBitmap = bitmap;
            }

        } else if (DisplayUtils.getWidthPixels() >= 1080) {
            // 适配大屏手机
            if (width > IMAGE_MAX_WIDTH) {
                scaledBitmap = BitmapUtils.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(),
                        IMAGE_MAX_WIDTH, maxHeight);
            } else {
                scaledBitmap = bitmap;
            }

        } else {
            // 适配小屏手机
            if (width < IMAGE_MAX_WIDTH && height > IMAGE_MAX_HEIGHT) {
                int w = IMAGE_MAX_WIDTH - DisplayUtils.dp2px(100);
                maxHeight = (int) ((float) height / (float) width *  w);
                scaledBitmap = BitmapUtils.createScaledBitmap(bitmap, bitmap.getWidth(),
                        bitmap.getHeight(), w, maxHeight);
            } else if (width > IMAGE_MAX_WIDTH) {
                scaledBitmap = BitmapUtils.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(),
                        IMAGE_MAX_WIDTH, maxHeight);
            } else {
                scaledBitmap = bitmap;
            }
        }


        SpannableString spannable = new SpannableString("<img />");
        ImageSpan imageSpan = new ImageSpan(mContext, scaledBitmap, null);
        imageSpan.setUrl(url);
        spannable.setSpan(imageSpan, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getText().replace(getText().getSpanStart(span), getText().getSpanEnd(span), spannable);
    }

    public void replaceLocalImage(FakeImageSpan span, String filePath) {
        Bitmap bitmap = BitmapUtils.decodeScaleImage(filePath, IMAGE_MAX_WIDTH);
        if (bitmap == null) {
            return;
        }
        SpannableString spannable = new SpannableString("<img src=\"" + filePath + "\"/>");
        ImageSpan imageSpan = new ImageSpan(mContext, bitmap, filePath);
        spannable.setSpan(imageSpan, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getText().replace(getText().getSpanStart(span), getText().getSpanEnd(span), spannable);
    }

    public void addLink(String linkName, String url) {
        SpannableString spannable = new SpannableString(linkName);
        spannable.setSpan(new UrlSpan(url), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getText().insert(getSelectionStart(), spannable);
    }

    public void addHref(int start, int end, String text) {
        UrlSpan[] spans = getText().getSpans(start, end, UrlSpan.class);
        if (spans.length > 0) {
            if (start == getText().getSpanStart(spans[0])
                    && end == getText().getSpanEnd(spans[0])) {
                return;
            }
            for (UrlSpan span : spans) {
                getText().removeSpan(span);
            }
        }
        //如果还没有添加span
        getText().setSpan(new UrlSpan(text),
                start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    public void setRichText(String richText) {
        setText(RichTextUtils.convertRichTextToSpanned(richText, mIEmojiFactory));
    }

    public String getRichText() {
        return RichTextUtils.convertSpannedToRichText(getText());
    }

    public FakeImageSpan[] getFakeImageSpans() {
        return getText().getSpans(0, getText().length(), FakeImageSpan.class);
    }

    public List<ImageSpan> getToUploadImageSpanList() {
        ImageSpan[] imageSpan = getText()
                .getSpans(0, getText().length(), ImageSpan.class);
        ArrayList<ImageSpan> spanList = new ArrayList<>();
        for (ImageSpan imgSpan : imageSpan) {
            if (TextUtils.isEmpty(imgSpan.getUrl())) {
                spanList.add(imgSpan);
            }
        }
        return spanList;
    }

    public void applyBoldEffect(int start, int end, boolean value) {
        Effects.BOLD.apply(this, start, end, value);
    }

    public void applyBoldEffect(boolean value) {
        Effects.BOLD.applyToSelection(this, value);
    }

    public boolean getBoldEffectValue() {
        return Effects.BOLD.existsInSelection(this, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
