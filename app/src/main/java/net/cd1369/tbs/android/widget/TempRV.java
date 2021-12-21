package net.cd1369.tbs.android.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewConfigurationCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.cd1369.tbs.android.config.TbsApp;

import cn.wl.android.lib.config.WLConfig;

public class TempRV extends RecyclerView {

    private boolean isLock = false;
    private Point downPoint = new Point();
    private Point movePoint = new Point();
    private ViewConfiguration vft =
            ViewConfiguration.get(WLConfig.getContext());
    private int touchSlop;

    public TempRV(@NonNull Context context) {
        super(context);

        init();
    }

    public TempRV(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TempRV(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        touchSlop = vft.getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isLock = true;
            downPoint.set(
                    (int) ev.getX(),
                    (int) ev.getY());
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (isLock) {
                movePoint.set(
                        (int) ev.getX(),
                        (int) ev.getY());

                int absX = Math.abs(downPoint.x - movePoint.x);
                int absY = Math.abs(downPoint.y - movePoint.y);

                if (absX > touchSlop || absY > touchSlop) {
                    isLock = false;

                    if (absY > absX) {
                        ViewParent parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(false);
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
