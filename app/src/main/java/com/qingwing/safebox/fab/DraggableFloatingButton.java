package com.qingwing.safebox.fab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

 
public class DraggableFloatingButton extends FloatingActionButton {

    public DraggableFloatingButton(Context context) {
        super(context);
    }

    public DraggableFloatingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int mLastX, mLastY;
 
    private long mDownTime, mUpTime;
 
    private int mLastLeft = -1;
    private int mLastRight = -1;
    private int mLastTop = -1;
    private int mLastBottom = -1;

    public int getLastLeft() {
        return mLastLeft;
    }

    public int getLastRight() {
        return mLastRight;
    }

    public int getLastTop() {
        return mLastTop;
    }

    public int getLastBottom() {
        return mLastBottom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:

                int deltaX = x - mLastX;
                int deltaY = y - mLastY;

                // 绉诲姩鍚庣殑涓婁笅宸�?彸鍧愭爣
                int left = getLeft() + deltaX;
                int right = getRight() + deltaX;
                int top = getTop() + deltaY;
                int bottom = getBottom() + deltaY;

                int marginLeft = 0;
                int marginRight = 0;
                int marginTop = 0;
                int marginBottom = 0;

                if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    // 鑾峰彇margin鍊硷紝鍋氱Щ鍔ㄧ殑杈圭晫鍒ゆ柇
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getLayoutParams();
                    marginLeft = lp.leftMargin;
                    marginRight = lp.rightMargin;
                    marginTop = lp.topMargin;
                    marginBottom = lp.bottomMargin;
                }

                int parentWidth = 0;
                int parentHeight = 0;

                if (getParent() != null && getParent() instanceof ViewGroup) {

                    ViewGroup parent = (ViewGroup) getParent();

                    // 鎷垮埌鐖跺竷灞�楂�?
                    parentWidth = parent.getWidth();
                    parentHeight = parent.getHeight();

                    if (left < marginLeft) {
                        // 绉诲埌鍒版渶宸︾殑鏃跺�锛岄檺鍒跺湪marginLeft
                        left = marginLeft;
                        right = getWidth() + left;
                    }

                    if (right > parentWidth - marginRight) {
                        // 绉诲姩鍒版渶鍙�
                        right = parentWidth - marginRight;
                        left = right - getWidth();
                    }

                    if (top < marginTop) {
                        // 绉诲姩鍒伴�?�閮�?
                        top = marginTop;
                        bottom = getHeight() + top;
                    }

                    if (bottom > parentHeight - marginBottom) {
                        // 绉诲姩鍒板簳閮�
                        bottom = parentHeight - marginBottom;
                        top = bottom - getHeight();
                    }

                    layout(left, top, right, bottom);

                    // 璁板綍绉诲姩鍚庣殑鍧愭爣鍊�
                    mLastLeft = left;
                    mLastRight = right;
                    mLastTop = top;
                    mLastBottom = bottom;

                }

                break;
            case MotionEvent.ACTION_UP:
                mLastX = x;
                mLastY = y;
                mUpTime = System.currentTimeMillis();
                // 鐐瑰嚮浜嬩欢鐨勫鐞�?
                return mUpTime - mDownTime > 200 || super.onTouchEvent(event);
        }

        mLastX = x;
        mLastY = y;

        return super.onTouchEvent(event);
    }
}
