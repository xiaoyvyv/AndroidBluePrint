package com.xiaoyv.floater.widget.webview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 平滑进度条
 *
 * @author 王怀玉
 * @since 2020/11/11
 */

public class UiWebProgressBar extends View {
    private Drawable drawable;
    private double progressWidth;
    private FlingRunnable mFlingRunnable;
    private int progress;
    public int realProgress;

    public UiWebProgressBar(Context context) {
        super(context);
        init();
    }

    public UiWebProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UiWebProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawable = new ColorDrawable(0xff4fd922);
    }

    /**
     * 设置进度条颜色
     *
     * @param color 颜色
     */
    public void setProgressColor(int color) {
        drawable = new ColorDrawable(color);
        invalidate();
    }

    /**
     * 设定进度
     *
     * @param progress 该进度最大值100
     */
    public void setProgress(int progress) {
        this.realProgress = progress;
        if (progress < 0 || progress > 100) {
            return;
        }
        this.progress = progress;
        if (mFlingRunnable == null) {
            mFlingRunnable = new FlingRunnable();
        } else {
            removeCallbacks(mFlingRunnable);
        }
        mFlingRunnable.startFling();
    }

    /**
     * 获取进度值
     *
     * @return 进度值
     */
    public int getProgress() {
        return progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawable == null) {
            return;
        }
        drawable.setBounds(0, 0, (int) Math.floor(progressWidth), getHeight());
        drawable.draw(canvas);
    }


    class FlingRunnable implements Runnable {

        double speed = 1;
        double targetWidth = 0;

        public void startFling() {
            if (getVisibility() == GONE) {
                setVisibility(VISIBLE);
                progressWidth = 0;
            }
            targetWidth = (getWidth() * progress / 100f);
            if (progressWidth > targetWidth) {
                progressWidth = targetWidth;
            }
            postOnAnimation(this);
        }

        public void endFling() {
            progressWidth = getWidth();
            removeCallbacks(this);
            setVisibility(GONE);
        }

        @Override
        public void run() {
            // 计算速度
            if (progressWidth < targetWidth) {
                speed++;
            } else {
                speed = 1;
            }
            // 计算进度条宽度
            progressWidth += speed / 2;
            // 计算当前进度
            progress = (int) Math.floor(progressWidth / getWidth() * 100);
            if (progressWidth < getWidth()) {
                invalidate();
                postOnAnimationDelayed(this, 10);
            } else {
                endFling();
            }
        }
    }

}