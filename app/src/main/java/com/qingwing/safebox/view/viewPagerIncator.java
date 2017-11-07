package com.qingwing.safebox.view;

import java.util.List;

import com.qingwing.safebox.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class viewPagerIncator extends LinearLayout {

    private static final int DEFAULT_COUNT = 4;
    private int mTabCount;
    private Paint paint;
    private Path mPath;
    private List<String> totalTitles;
    private ViewPager mPager;
    private int mTriangwidth;
    private int mInitTranslation;
    private int mTriangleHeight;
    private int mTranslationX;

    //定义三角形所占每个控件的比例
    private static final float RADIO_TRIANGEL_WIDTH = 1 / 6F;
    //正常的颜色
    private static final int COLOR_TEXT_NORMAL = 0xFFFFFFFF;
    //高亮的颜色
    private static final int HEIGHT_LIGHTCOLOR = 0xFF10a4e8;

    public viewPagerIncator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.viewPagerIndicator);
        mTabCount = array.getInt(R.styleable.viewPagerIndicator_visible_tab_count, DEFAULT_COUNT);
        if (mTabCount < 0) {
            mTabCount = DEFAULT_COUNT;
        }
        array.recycle();
        //初始化画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#ffffff"));
        paint.setStyle(Style.FILL);
        paint.setPathEffect(new CornerPathEffect(3));
    }

    public viewPagerIncator(Context context) {
        this(context, null);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mTabCount == 1) {
            mTriangwidth = (int) (w / mTabCount * 1 / 12f);
        } else {
            mTriangwidth = (int) (w / mTabCount * RADIO_TRIANGEL_WIDTH);
        }
        mInitTranslation = w / mTabCount / 2 - mTriangwidth / 2;
        initTriangle();
        Log.d("View的绘制流程", "onSizeChanged()");
    }

    private void initTriangle() {
        mTriangleHeight = mTriangwidth / 2;
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangwidth, 0);
        mPath.lineTo(mTriangwidth / 2, -mTriangleHeight);
        mPath.close();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mInitTranslation + mTranslationX, getHeight() + 4);
        canvas.drawPath(mPath, paint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    private int getScreenWidth() {
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        ;
        systemService.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public void setTabItemTitle(List<String> title) {
        if (title != null && title.size() > 0) {
            this.removeAllViews();
            totalTitles = title;
            for (String string : totalTitles) {
                addView(generateTextView(string));
            }
        }
        setItemOnClick();
        Log.d("View的绘制流程", "setTabItemTitle()");
    }

    /**
     * 添加点击事件
     */
    private void setItemOnClick() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPager.setCurrentItem(j);
                }
            });
        }
    }

    public void setVisibleTab(int count) {
        Log.d("显示TAB的值", mTabCount + ":" + count);
        this.mTabCount = count;
    }

    private View generateTextView(String string) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams lpLayoutParams =
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lpLayoutParams.width = getScreenWidth() / mTabCount;
        lpLayoutParams.weight = 0;
        textView.setText(string);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(COLOR_TEXT_NORMAL);
        textView.setLayoutParams(lpLayoutParams);
        return textView;
    }

    public void scroll(int postion, float postionoffset) {
        int tabWidth = getWidth() / mTabCount;
        mTranslationX = (int) (tabWidth * postionoffset + postion * tabWidth);
        //当容器移动到最后一步的时候
        /*if (postion>=(mTabCount-2)&postionoffset>0&&getChildCount()>mTabCount) {

		}*/
        invalidate();
    }

    public int recordScollPostion;

    public void setViewPager(ViewPager viewPager, int postion) {
        mPager = viewPager;
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                setHeightLightColor(arg0);
            }

            @Override
            public void onPageScrolled(int postion, float postionoffset, int arg2) {
                recordScollPostion = postion;
                scroll(postion, postionoffset);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        setHeightLightColor(postion);
    }

    public void setHeightLightColor(int postion) {
        resetTextViewColor();
        View view = getChildAt(postion);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(HEIGHT_LIGHTCOLOR);
        }
    }

    //重置颜色
    private void resetTextViewColor() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("View的绘制流程", "onMeasure()");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d("View的绘制流程", " onLayout()");
    }
}
