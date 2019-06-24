package com.example.widget;

import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class RoundImageView extends AppCompatImageView {

    private final int mCornerRadius;
    private final int mBgColor;
    private final Path mRoundPath = new Path();
    private final RectF mRect = new RectF();

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCornerRadius = 120;// * a.getDimensionPixelSize(R.styleable.RoundImageView_cornerRadius, mCornerRadius);
        mBgColor = 0;//a.getColor(R.styleable.RoundImageView_backgroundColor, Color.TRANSPARENT);
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        paint.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    private final Paint paint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        mRoundPath.reset();
        mRoundPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        //不使用硬加速时,有些机型裁切出来角会有黑块,所以绘制背景色来遮挡
        if (Build.VERSION.SDK_INT >= 21) {
            mRoundPath.addRoundRect(0, 0, getWidth(), getHeight(),
                    mCornerRadius, mCornerRadius, Path.Direction.CW);
        } else {
            mRect.set(0, 0, getWidth(), getHeight());
            mRoundPath.addRoundRect(mRect, mCornerRadius, mCornerRadius, Path.Direction.CW);
        }
//        canvas.clipPath(mRoundPath);
        super.onDraw(canvas);
        canvas.drawPath(mRoundPath, paint);
    }
//未完成呢! TODO
    public void setXfMode(PorterDuff.Mode mode) {
        paint.setXfermode(new PorterDuffXfermode(mode));
        invalidate();
    }
}
