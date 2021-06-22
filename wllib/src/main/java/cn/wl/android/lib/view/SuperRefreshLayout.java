package cn.wl.android.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.blankj.utilcode.util.ColorUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import cn.wl.android.lib.R;

public class SuperRefreshLayout extends SmartRefreshLayout {

    private int touchSlop;
    private float mStartX;
    private float mStartY;

    private boolean isDrag;

    public SuperRefreshLayout(Context context) {
        super(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public SuperRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        ClassicsHeader header = new ClassicsHeader(context);
        header.setAccentColor(ColorUtils.getColor(R.color.md_black));
        header.setFinishDuration(400);
        setRefreshHeader(header);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getX();
                mStartY = ev.getY();
                isDrag = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDrag) return false;
                float evX = ev.getX();
                float evY = ev.getY();

                float absX = Math.abs(evX - mStartX);
                float absY = Math.abs(evY - mStartY);

                if (absX > touchSlop && absX > absY) {
                    isDrag = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDrag = false;
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }
}
