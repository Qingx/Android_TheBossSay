package cn.wl.android.lib.utils;

import android.view.View;


/**
 * Created by JustBlue on 2019-09-20.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public final class ViewHelper {

    /**
     * 设置View的显示状态
     *
     * @param view
     * @param show
     */
    public static void setVisible(View view, boolean show) {
        if (view == null) return;

        int current = view.getVisibility();
        int target = show ? View.VISIBLE : View.GONE;

        if (current != target) view.setVisibility(target);
    }

    /**
     * 切换{@linkplain View#setVisibility(int)}状态;
     *
     * @param view
     * @param invisible
     * @see true {@linkplain View#INVISIBLE}
     * @see false {@linkplain View#VISIBLE}
     */
    public static void setInvisible(View view, boolean invisible) {
        if (view == null) return;

        view.setVisibility(invisible ? View.INVISIBLE : View.VISIBLE);
    }

}
