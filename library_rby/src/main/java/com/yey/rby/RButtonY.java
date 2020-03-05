package com.yey.rby;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RButtonY extends View {
    private final static String TAG = RButtonY.class.getName();
    private RectF mRectF;
    private Paint mCirclePaint;
    private Paint mRectPaint;
    private ValueAnimator mAnimator;
    private int centerX;
    private int centerY;
    private int radius;
    private int mRectStartSize;
    private int mRectEndSize;
    private float mTempRectSize;
    private boolean up;//按钮是否开始记录, true 为开始, false 还没开始
    private boolean isAnimRuning;
    private int mShortest;//最短时长
    private int mLongest;//最长时长
    private int mCurrent;//当前时间
    private RBYCallback rbyCb;
    private int mCircleOutMarginSize;//外圆margin
    private float mRectRateStart;//内方形初始边长相对于外圆内切正方形边长比率
    private float mRectRateFinish;//内方形结束边长相对于初始边长的比率
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mCurrent++;
            if (rbyCb != null) {
                rbyCb.eventCb(String.valueOf(mCurrent));
            }
            if (mCurrent >= mLongest) {//当前记录时间大于或等于最大记录时间，将自动结束记录
                recordFinish();
            } else {
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };
    private int mCircleWidth;
    private int mCirclePaintColor;
    private int mRectPaintColor;


    public RButtonY(Context context) {
        this(context, null);
    }

    public RButtonY(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RButtonY(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParame(context, attrs, defStyleAttr);
        initPaint();
        initRect();
        initAnimator();
    }

    /**
     * 初始化参数
     */
    private void initParame(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RButtonY, defStyleAttr, 0);
        mCircleOutMarginSize = typedArray.getDimensionPixelSize(R.styleable.RButtonY_rby_circle_out_margin, 5);
        mCircleWidth = typedArray.getDimensionPixelSize(R.styleable.RButtonY_rby_circle_width, 5);
        mCirclePaintColor = typedArray.getColor(R.styleable.RButtonY_rby_circle_paint_color, Color.YELLOW);
        mRectPaintColor = typedArray.getColor(R.styleable.RButtonY_rby_rect_paint_color, Color.RED);
        mRectRateStart = typedArray.getFloat(R.styleable.RButtonY_rby_rect_rate_start, 0.9f);
        mRectRateFinish = typedArray.getFloat(R.styleable.RButtonY_rby_rect_rate_fnish, 0.5f);
        mShortest = typedArray.getInteger(R.styleable.RButtonY_rby_short_time, 3);
        mLongest = typedArray.getInteger(R.styleable.RButtonY_rby_long_time, 10);
        typedArray.recycle();
    }

    // Paint.Style.FILL设置只绘制图形内容
    // Paint.Style.STROKE设置只绘制图形的边
    // Paint.Style.FILL_AND_STROKE设置都绘制
    private void initPaint() {
        //外圆画笔
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCirclePaintColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleWidth);
        //内部正方形画笔
        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setColor(mRectPaintColor);
        mRectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    //初始化内方形RectF
    private void initRect() {
        mRectF = new RectF();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        //圆心坐标
        centerX = width / 2;
        centerY = height / 2;
        //半径
        radius = Math.min(centerX, centerY) - mCircleOutMarginSize / 2;
        //pow 平方，sqrt 开方
        //正方形开始边长,圆形直径的平方除以二再开放,为正方形边长.
        mRectStartSize = (int) (Math.sqrt(Math.pow(radius * 2, 2) / 2) * mRectRateStart);
        //正方形结束边长
        mRectEndSize = (int) (mRectStartSize * mRectRateFinish);
        //mTempRectSize == 0 时, 即第一创建该View.
        if (mTempRectSize == 0) {
            //如果屏幕旋转,onLayout将被回调,此时并不希望mTempRectSize被重新赋值为mRectStartSize(开始状态).
            //所以只有当第一次创建时,才需要为mTempRectSize赋值为mRectStartSize(开始状态)
            mTempRectSize = mRectStartSize;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //外圆绘制
        canvas.drawCircle(centerX, centerY, radius, mCirclePaint);
        //正方形四点坐标
        int mLeftRectTemp = (int) (centerX - mTempRectSize / 2);
        int mRightRectTemp = (int) (centerX + mTempRectSize / 2);
        int mTopRectTemp = (int) (centerY + mTempRectSize / 2);
        int mButtonRectTemp = (int) (centerY - mTempRectSize / 2);
        //绘制正方形
        mRectF.set(mLeftRectTemp, mTopRectTemp, mRightRectTemp, mButtonRectTemp);
        //(float) Math.sqrt(radius): 圆角半径
        canvas.drawRoundRect(mRectF, (float) Math.sqrt(radius), (float) Math.sqrt(radius), mRectPaint);
    }

    /**
     * 初始化动画
     * 这里对动画进行监听, 获取正方形边长随动画改变的值,然后重绘
     */
    private void initAnimator() {
        mAnimator = new ValueAnimator();
        /**
         * onAnimationStart() - 当动画开始的时候调用.
         * onAnimationEnd() - 动画结束时调用.
         * onAnimationRepeat() - 动画重复时调用.
         * onAnimationCancel() - 动画取消时调用.取消动画也会调用onAnimationEnd，它不会关系动画是怎么结束的。
         */
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束
                isAnimRuning = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                //动画开始
                isAnimRuning = true;
            }
        });
        //动画进度监听,获取正方形随动画变化的边长,然后重绘
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //动态获取正方形边长
                mTempRectSize = (float) animation.getAnimatedValue();
                invalidate();//重绘
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //如果正方形动画正在播放，就拒绝按钮点击
                if (isAnimRuning) return true;
                //up为false代表未开始记录,true 代表开始记录
                //未开始记录时,mCurrent是等于0
                if (!up && mCurrent == 0) {
                    recordStart();
                }
                //已开始记录,并且当前录制时间大于或者等于所设置的最短记录时长,则按钮可以手动结束
                if (up && mCurrent >= mShortest) {
                    recordFinish();
                }
                //已开始记录,当前录制时间小于所设置的最短记录时长,并且录制时间大于1,则回调方法通知当前还不能手动结束录制
                if (up && mCurrent < mShortest && mCurrent >= 1) {
                    if (rbyCb != null) {
                        rbyCb.lessShortTimeRecode(String.valueOf(mCurrent));
                    }
                }
                break;
        }
        return true;//消费事件
    }

    /**
     * 设置回调方法
     */
    public void setiRBYClick(RBYCallback iClick) {
        rbyCb = iClick;
    }

    /**
     * 录制开始
     */
    private void recordStart() {
        //正方形开始动画
        startAnimation(mRectStartSize, mRectEndSize);
        if (rbyCb != null) {
            //录制开始的回调
            rbyCb.startCb(String.valueOf(mCurrent));
        }
        //开始计时
        mHandler.sendEmptyMessage(0);
        //录制标识为开始
        up = true;
        mTempRectSize = mRectEndSize;
    }

    /**
     * 录制结束
     */
    private void recordFinish() {
        //正方形结束动画
        startAnimation(mRectEndSize, mRectStartSize);
        if (rbyCb != null) {
            //结束时回调
            rbyCb.finishCb(String.valueOf(mCurrent));
        }
        //录制结束,当前时间归0
        mCurrent = 0;
        mHandler.removeCallbacksAndMessages(null);
        //录制标识为结束
        up = false;
        mTempRectSize = mRectStartSize;
    }

    /**
     * 开始动画
     *
     * @param startValue
     * @param endValue
     */
    private void startAnimation(float startValue, float endValue) {
        mAnimator.setFloatValues(startValue, endValue);
        mAnimator.setDuration(100);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.start();
    }

    //屏幕旋转时候保存必要的数据
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        if (mCurrent != 0) {
            Bundle bundle = new Bundle();
            //保存系统其他原有的状态信息
            bundle.putParcelable("instance", super.onSaveInstanceState());
            //保存当前的一些状态
            bundle.putFloat("rect_size", mTempRectSize);//保存方形边长
            bundle.putBoolean("up", up);
            bundle.putInt("mCurrent", mCurrent);
            return bundle;
        } else {
            return super.onSaveInstanceState();
        }

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //判断state的类型是否为bundle,若是则从bundle中取数据
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mTempRectSize = bundle.getFloat("rect_size");
            up = bundle.getBoolean("up");
            mCurrent = bundle.getInt("mCurrent");
            //开始计时
            mHandler.sendEmptyMessage(0);
            super.onRestoreInstanceState(bundle.getParcelable("instance"));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    //页面销毁,清空消息,防止内存泄漏
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}