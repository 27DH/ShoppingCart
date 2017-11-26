package com.dailyart.shoppingcart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;


/**
 * Created by 27DH on 2017/11/19.
 */

public class CircularImageView extends AppCompatImageView {

    private static final String TAG = CircularImageView.class.getSimpleName();

    private int borderWith;
    private int borderColor;
    private float borderRadius;
    private RectF borderRect = new RectF();
    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private Paint borderPaint = new Paint();

    private int backgroundColor;
    //private float backgroundRadius;
    private static final int DEFAULT_BACKGROUND = Color.TRANSPARENT;
    private Paint backgroundPaint = new Paint();

    private Paint bitmapPaint = new Paint();
    private RectF drawableRect = new RectF();
    private float drawRadius;
    private BitmapShader bitmapShader;
    private Bitmap bitmap;

    private ColorFilter colorFilter;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;

    private int badgeBorderWidth;
    private int badgeBorderColor;
    private int badgeRadius;
    private int badgeBackgroundColor;
    private int count;
    private int badgeTextColor;
    private float badgeTextSize;
    private RectF badgeBorderRect = new RectF();
    private Paint badgeBorderPaint = new Paint();
    private Paint badgeBackgroundPaint = new Paint();
    private Paint badgeTextPaint = new Paint();
    private static final int DEFAULT_BADGE_BORDER_WIDTH = 0;
    private static final int DEFAULT_BADGE_BORDER_COLOR = Color.BLACK;
    private static final int DEFAULT_BADGE_BACKGROUND = Color.RED;
    private static final int DEFAULT_BADGE_TEXT_COLOR = Color.WHITE;
    //private static final int DEFAULT_BADGE_TEXT_SIZE = 5;
    private static final double ROTATE_ANGLE = Math.PI / 4;

    private static final String DEFAULT_BADGE_NUMBER = "99+";



    public CircularImageView(Context context) {
        this(context,null);

    }

    public CircularImageView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public CircularImageView(Context context,AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);

        TypedArray a = context.obtainStyledAttributes(attr, R.styleable.CircularImageView, defStyle, 0);
        borderWith = a.getDimensionPixelSize(R.styleable.CircularImageView_border_with, DEFAULT_BORDER_WIDTH);
        borderColor = a.getColor(R.styleable.CircularImageView_border_color, DEFAULT_BORDER_COLOR);
        backgroundColor = a.getColor(R.styleable.CircularImageView_background_color,DEFAULT_BACKGROUND);

        badgeBorderColor = a.getColor(R.styleable.CircularImageView_badge_border_color, DEFAULT_BADGE_BORDER_COLOR);
        badgeBackgroundColor = a.getColor(R.styleable.CircularImageView_background_color, DEFAULT_BADGE_BACKGROUND);
        badgeBorderWidth = a.getDimensionPixelSize(R.styleable.CircularImageView_badge_border_Width, DEFAULT_BADGE_BORDER_WIDTH);
        badgeTextColor = a.getColor(R.styleable.CircularImageView_badge_text_color, DEFAULT_BADGE_TEXT_COLOR);

        a.recycle();

    }

    private void init() {
        setScaleType(ScaleType.CENTER_CROP);
    }



    private void initBitmap(){
        if (getDrawable() != null)
            bitmap = getBitmapFromDrawable(getDrawable());
    }


    private void initBitmapPaint(){
        if (getDrawable() != null) {

            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            float scale;
            float dx = 0;
            float dy = 0;
            if (bitmapWidth * drawableRect.height() > bitmapHeight * drawableRect.width()) {
                scale = drawableRect.height() / bitmapHeight;
                dx = (drawableRect.width() - bitmapWidth * scale) / 2f;
            } else {
                scale = drawableRect.width() / bitmapWidth;
                dy = (drawableRect.height() - bitmapHeight * scale) / 2f;
            }
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            matrix.postTranslate((int)(dx + 0.5) + drawableRect.left, (int)(dy + 0.5) + drawableRect.top);
            bitmapShader.setLocalMatrix(matrix);
            bitmapPaint.setShader(bitmapShader);
        }


    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }


    private RectF caculateBounds() {
        int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;

        return new RectF(left, top, left + sideLength, top + sideLength);
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap;
        if (drawable instanceof ColorDrawable) {
            bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        Log.d(TAG, "onDraw...");


        borderPaint.setAntiAlias(true);
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWith);

        //init background Paint
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);

        borderRect.set(caculateBounds());
        borderRadius = Math.min((borderRect.width() - borderWith)/ 2f, (borderRect.height() - borderWith)/2f);


        drawableRect.set(borderRect);
        if (borderWith > 0) {
            drawableRect.inset(borderWith - 1.0f, borderWith - 1.0f);
        }
        drawRadius = Math.min(drawableRect.width() / 2f, drawableRect.height() / 2f);

        initBitmapPaint();

        if (backgroundColor != Color.TRANSPARENT) {
            canvas.drawCircle(drawableRect.centerX(),drawableRect.centerY(), drawRadius, backgroundPaint);
        }

        if (getDrawable() != null){
            canvas.drawCircle(drawableRect.centerX(),drawableRect.centerY(), drawRadius, bitmapPaint);
        }
        if (borderWith != 0) {
            canvas.drawCircle(borderRect.centerX(), borderRect.centerY(), borderRadius, borderPaint);
        }


        badgeBorderPaint.setAntiAlias(true);
        badgeBorderPaint.setColor(badgeBorderColor);
        badgeBorderPaint.setStyle(Paint.Style.STROKE);
        badgeBorderPaint.setStrokeWidth(badgeBorderWidth);

        badgeBackgroundPaint.setAntiAlias(true);
        badgeBackgroundPaint.setColor(badgeBackgroundColor);
        badgeBackgroundPaint.setStyle(Paint.Style.FILL);

        badgeTextPaint.setAntiAlias(true);
        badgeTextPaint.setColor(badgeTextColor);
        badgeTextPaint.setTextAlign(Paint.Align.CENTER);



        if (count != 0) {
            if (count < 10 && count > 0){

                double maxRadius = borderRadius - (borderRadius * Math.cos(ROTATE_ANGLE));
                badgeRadius = (int) (maxRadius - badgeBorderWidth / 2);
                float badgeCenterX = (float)(borderRect.centerX() + borderRadius * Math.cos(ROTATE_ANGLE));
                float badgeCenterY = (float)(borderRect.centerY() - borderRadius * Math.sin(ROTATE_ANGLE));

                if (badgeBackgroundColor != Color.TRANSPARENT) {
                    canvas.drawCircle(badgeCenterX,badgeCenterY, badgeRadius - badgeBorderWidth / 2, badgeBackgroundPaint);
                }


                canvas.drawCircle(badgeCenterX,badgeCenterY, badgeRadius, badgeBorderPaint);

                badgeTextSize = 0.8f * 2 * (badgeRadius - badgeBorderWidth / 2);
                badgeTextPaint.setTextSize(badgeTextSize);

                Rect textBounds = new Rect();
                String text = count + "";
                badgeTextPaint.getTextBounds(text, 0, text.length(), textBounds);
                int offset = textBounds.height() / 2;
                canvas.drawText(text, badgeCenterX, badgeCenterY + offset, badgeTextPaint );
            } else if (count >= 10) {
                double maxRadius = borderRadius - (borderRadius * Math.cos(ROTATE_ANGLE));
                badgeRadius = (int) (maxRadius - badgeBorderWidth / 2);
                float roundRectHeight = badgeRadius * 2.0f;
                float roundRectWidth = borderRect.width()/ 2.0f - badgeBorderWidth;
                RectF rect = new RectF(borderRect.centerX() + badgeBorderWidth / 2.0f, badgeBorderWidth / 2.0f,
                        borderRect.centerX() + badgeBorderWidth / 2.0f + roundRectWidth, badgeBorderWidth / 2.0f + roundRectHeight);
                RectF backgroundRect =  new RectF(borderRect.centerX() + badgeBorderWidth, badgeBorderWidth,
                        borderRect.centerX() + roundRectWidth, roundRectHeight);

                if (badgeBackgroundColor != Color.TRANSPARENT) {
                    canvas.drawRoundRect(backgroundRect, 0.2f * roundRectWidth, roundRectHeight / 2.0f, badgeBackgroundPaint);
                }

                if (badgeBorderWidth != 0) {
                    canvas.drawRoundRect(rect, 0.2f * roundRectWidth, roundRectHeight / 2.0f, badgeBorderPaint);
                }

                badgeTextSize = 0.8f * 2 * (badgeRadius - badgeBorderWidth / 2);
                badgeTextPaint.setTextSize(badgeTextSize);
                String text;
                Rect textBounds = new Rect();
                if (count > 99) {
                    text = DEFAULT_BADGE_NUMBER;
                } else {
                    text = count + "";
                }

                badgeTextPaint.getTextBounds(text, 0, text.length(), textBounds);
                int offset = textBounds.height() / 2;
                canvas.drawText(text, rect.centerX(), rect.centerY() + offset, badgeTextPaint );
            }

        }

    }


    public int getCount(){
        return count;
    }

    public void setCount(int count){
        this.count = count;
        invalidate();
    }



    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        initBitmap();
        invalidate();
    }

    /**
     * Sets a drawable as the content of this ImageView.
     *
     * @param drawable the Drawable to set, or {@code null} to clear the
     *                 content
     */
    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        initBitmap();
        invalidate();
    }

    /**
     * Sets a Bitmap as the content of this ImageView.
     *
     * @param bm The bitmap to set
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initBitmap();
        invalidate();

    }

    public float getBorderWidth() {
        return borderWith;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == this.borderWith) {
            return;
        }

        this.borderWith = borderWidth;

        invalidate();
    }

    public int getBorderColor(){
        return borderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (borderColor == this.borderColor){
            return;
        }
        this.borderColor = borderColor;
        invalidate();
    }

    /**
     * Returns the active color filter for this ImageView.
     *
     * @return the active color filter for this ImageView
     * @see #setColorFilter(ColorFilter)
     */
    @Override
    public ColorFilter getColorFilter() {
        return colorFilter;
    }

    public void applyColorFilter(ColorFilter colorFilter){
        if (bitmapPaint != null) {
            bitmapPaint.setColorFilter(colorFilter);
        }
        invalidate();
    }

    public void setCircleBackgroundColor(@ColorInt int circleBackgroundColor) {
        if (backgroundColor == circleBackgroundColor) {
            return;
        }
        this.backgroundColor = circleBackgroundColor;
        invalidate();
    }

    public int getBackgroundColor(){
        return backgroundColor;
    }

    public void setCircleBackgroundColorResource(@ColorRes int circleBackgroundRes) {
        setCircleBackgroundColor(getContext().getResources().getColor(circleBackgroundRes));
    }



}
