package com.ll.progressbar.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.ll.progressbar.DpUtil;
import com.ll.progressbar.R;
import com.ll.progressbar.ScreenUtil;

/**
 * Created by admin on 2016/11/25.
 */

public class ProgressView extends View {
    private PorterDuffXfermode mMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private Paint mPaint;//绘制圆角矩形的画笔
    private Paint progressPaint;//绘制进度的画笔
    private Paint progressTextPaint;//绘制进度文本的画笔
    private Paint rotateBgPaint;//绘制旋转背景画笔
    private Paint movePointPaint;//绘制移动小球画笔
    private static final int DEFAULT_REACHED_COLOR = Color.BLUE;//默认已达到的颜色
    private static final int DEFAULT_NORMAL_COLOR = Color.GRAY;//默认未达到的颜色
    private static final int DEFAULT_PROGRESSTEXT_COLOR = Color.BLUE;//默认进度文本的颜色
    private int progressReachedColor;//已达到进度的颜色
    private int progressNormalColor;//未达到进度的颜色
    private int progressTextColor;//进度文本的颜色
    private int progressTextSize;//进度文本字体大小
    private int movePointColor;//移动小球的颜色

    private int progress;//进度值
    private int maxProgress = 100;//最大进度值

    private float viewHeight;
    private float viewWidth;

    private Canvas mCanvas;//绘制进度的画布
    private Bitmap progressBitmap;

    private float progressStartX;//当前进度的起始位置
    private float progressTextStartX;//当前进度文本的起始位置
    private String progressText;//绘制的文本

    private Drawable mRotateDrawable;
     private Point currentPoint;
    private boolean isAnimateEnd;
    private boolean isShowProgressText;//是否显示进度值
    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性值
        TypedArray ta  = context.obtainStyledAttributes(attrs, R.styleable.ProgressView, defStyleAttr, 0);
        progressNormalColor = ta.getColor(R.styleable.ProgressView_progressNormalColor,DEFAULT_NORMAL_COLOR);
        progressReachedColor = ta.getColor(R.styleable.ProgressView_progressReachedColor,DEFAULT_REACHED_COLOR);
        progressTextColor = ta.getColor(R.styleable.ProgressView_progressTextColor, DEFAULT_PROGRESSTEXT_COLOR);
        progressTextSize = ta.getDimensionPixelSize(R.styleable.ProgressView_progressTextSize, DpUtil.sp2px(context, 14));
        movePointColor = ta.getColor(R.styleable.ProgressView_movePointColor, DEFAULT_REACHED_COLOR);
        isShowProgressText = ta.getBoolean(R.styleable.ProgressView_isShowProgressText, true);
        ta.recycle();

        mRotateDrawable = getContext().getResources().getDrawable(R.mipmap.ic_rotate);
        this.init();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        switch (heightMode){
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                viewHeight = heightSize;
                viewHeight = Math.max(viewHeight,mRotateDrawable.getIntrinsicHeight()+20);
                break;
            case MeasureSpec.AT_MOST:
                viewHeight = DpUtil.dp2px(getContext(),50);
                break;
        }
        switch (widthMode){
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                viewWidth = widthSize;
                viewWidth = Math.min(viewWidth,ScreenUtil.getScreenWidth(getContext()));//如果宽度超过屏幕的宽度，则将宽度设置为屏幕宽度
                break;
            case MeasureSpec.AT_MOST:
                viewWidth = ScreenUtil.getScreenWidth(getContext());
                break;
        }
        setMeasuredDimension((int) viewWidth, (int) viewHeight);
        if(progressBitmap == null){
            progressBitmap = Bitmap.createBitmap((int)viewWidth,(int)viewHeight, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(progressBitmap);
        }

    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rotateBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        movePointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setColor(progressNormalColor);
        progressPaint.setColor(progressReachedColor);
        progressPaint.setStyle(Paint.Style.FILL);
        progressTextPaint.setTextSize(progressTextSize);
        rotateBgPaint.setStyle(Paint.Style.FILL);
        rotateBgPaint.setColor(progressReachedColor);
        movePointPaint.setStyle(Paint.Style.FILL);
        movePointPaint.setColor(movePointColor);



    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制圆角矩形
        mCanvas.drawRoundRect(0, 0, viewWidth, viewHeight, viewHeight / 2, viewHeight / 2, mPaint);
        //绘制进度
        this.drawProgress(canvas);
        //绘制文本
        if(isShowProgressText){
            this.drawProgressText(canvas);
            //绘制变色文本
            this.drawColorProgressText(canvas);
        }
        //绘制右侧旋转
        this.drawRotateDrawable(canvas);
        //绘制移动的小球
        if(progress != 0){
            this.drawMovePoint(canvas);
        }
       }

    /**
     * 绘制移动的点
     * @param canvas
     */
    private void drawMovePoint(Canvas canvas) {
      if(currentPoint == null){
            currentPoint = createPoint(viewWidth- viewHeight, viewHeight / 2f,DpUtil.dp2px(getContext(),6));
            startMove();
        }
        //当小球移动到与进度重合时不再进行绘制\
        if(currentPoint.getX() > progressStartX ){
            canvas.drawCircle(currentPoint.getX(),currentPoint.getY(),currentPoint.getRadius(),movePointPaint);
        }else if(isAnimateEnd){//动画已经完成
            currentPoint.setX(viewWidth- viewHeight);
            currentPoint.setY(viewHeight / 2f);
            startMove();
        }
    }


    private void drawRotateDrawable(Canvas canvas) {
        int rotateSaveCount = canvas.save();
        //绘制旋转背景
        canvas.drawCircle(viewWidth- viewHeight/2f, viewHeight / 2f,viewHeight / 2f,rotateBgPaint);

        canvas.rotate(progress*20, viewWidth-viewHeight / 2f, viewHeight / 2f);
        mRotateDrawable.setBounds((int) (viewWidth-viewHeight / 2f - mRotateDrawable.getIntrinsicWidth() / 2),
                (int) (viewHeight / 2f - mRotateDrawable.getIntrinsicHeight() / 2 ),
                (int) (viewWidth-viewHeight / 2f + mRotateDrawable.getIntrinsicWidth() / 2),
                (int) (viewHeight / 2f + mRotateDrawable.getIntrinsicHeight() / 2 ));
        mRotateDrawable.draw(canvas);
        canvas.restoreToCount(rotateSaveCount);
    }

    /**
     * 绘制变色文本
     * @param canvas
     */
    private void drawColorProgressText(Canvas canvas) {
        if(progressStartX > progressTextStartX){
            progressTextPaint.setColor(Color.WHITE);
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            float right = progressStartX - progressTextStartX;
            Paint.FontMetrics fm = progressTextPaint.getFontMetrics();
            float baseLine = viewHeight / 2f - fm.descent + (fm.bottom-fm.top) / 2f;
            canvas.clipRect(progressTextStartX,0,progressTextStartX+right,viewHeight);
            canvas.drawText(progressText,progressTextStartX,baseLine,progressTextPaint);
            canvas.restore();
        }
    }

    /**
     * 绘制文本
     * @param canvas
     */
    private void drawProgressText(Canvas canvas) {
        progressTextPaint.setColor(progressTextColor);
        progressText = progress + "%";
        float textWidth = progressTextPaint.measureText(progressText);//测量文本的宽度
        progressTextStartX  = viewWidth / 2f- textWidth / 2f;
        Paint.FontMetrics fm = progressTextPaint.getFontMetrics();
        float baseLine = viewHeight / 2f - fm.descent + (fm.bottom-fm.top) / 2f;
        canvas.drawText(progressText, progressTextStartX, baseLine, progressTextPaint);
    }

    /**
     * 绘制进度
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {
        progressStartX = progress * viewWidth / maxProgress;
        progressPaint.setXfermode(mMode);
        mCanvas.drawRect(0, 0, progressStartX, viewHeight, progressPaint);
        progressPaint.setXfermode(null);
        canvas.drawBitmap(progressBitmap, 0, 0, null);
    }

    private void startMove() {
        float rangeY = viewHeight / 2f - currentPoint.getRadius();
        ValueAnimator animator = ValueAnimator.ofObject(new PointEvaluator(rangeY,currentPoint.getRadius()),currentPoint,createPoint(0,0));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentPoint = (Point) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimateEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnimateEnd = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(3 * 1000).start();
    }

    public void setProgress(int progress){
        if (progress <= getMaxProgress() && progress >= 0) {
            this.progress = progress;
            postInvalidate();
        }

    }
    public int getProgress(){
        return this.progress;
    }
    public int getMaxProgress(){
        return this.maxProgress;
    }
    public void incrementProgressBy(int by) {
        if (by > 0) {
            setProgress(getProgress() + by);
        }
    }
    /**
     * createPoint()创建Point对象
     */
    public Point createPoint(float x,float y,float radius){
        return new Point(x,y,radius);
    }

    public Point createPoint(float x,float y){
        return new Point(x,y);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("savedInstance", super.onSaveInstanceState());
        bundle.putInt("currentProgress", getProgress());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            Log.e("TAG",""+bundle.getInt("currentProgress"));
            setProgress(bundle.getInt("currentProgress"));
           super.onRestoreInstanceState(bundle.getParcelable("savedInstance"));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
