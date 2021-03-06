package cn.wl.android.lib.view.holder;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.wl.android.lib.R;

/**
 * Created by JustBlue on 2019-08-26.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public abstract class BaseHolder {

    private View mContentView;

    private Context mContext;
    private WeakReference<View> mHoldRef;

    private final int statusCode;
    private final SparseArray<View> mViewCache;

    public static HashMap<String, Integer> offsetRegisters = new HashMap<>();

    public static void registerOffset(Context context, int offsetHeight) {
        if (context != null) {
            try {
                String name = context.getClass().getName();
                offsetRegisters.put(name, offsetHeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int getOffsetHeight(Context context) {
        try {
            String name = context.getClass().getName();
            Integer integer = offsetRegisters.get(name);

            if (integer != null) return integer;
        } catch (Exception e) {

        }
        return 0;
    }

    public BaseHolder(int statusCode) {
        this.statusCode = statusCode;
        this.mViewCache = new SparseArray<>();
    }

    /**
     * @return
     */
    public View getContentView() {
        View contentView = this.mContentView;

        if (contentView == null) {
            contentView = getContentView(mContext);
            this.mContentView = contentView;

            initContentView(contentView);
        }

        Context context = contentView.getContext();
        int offsetHeight = getOffsetHeight(context);

        if (offsetHeight > 10) {
            try {
                tryUpdateSpaceHeight(contentView, offsetHeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return contentView;
    }

    /**
     * ??????
     *
     * @param contentView
     * @param offsetHeight
     */
    private void tryUpdateSpaceHeight(View contentView, int offsetHeight) {
        View view = contentView.findViewById(R.id.space_holder);

        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams.height != offsetHeight) {
                layoutParams.height = offsetHeight;
                view.setLayoutParams(layoutParams);
            }
        }
    }

    /**
     * ?????????View
     *
     * @param contentView
     */
    protected abstract void initContentView(View contentView);

    /**
     * ??????mContentView
     *
     * @param context
     * @return
     */
    protected abstract View getContentView(Context context);

    /**
     * ???????????????
     *
     * @return
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * ???{@link #mContentView}?????????View, ???????????????
     *
     * @param resId
     * @return
     */
    public <T extends View> T getView(@IdRes int resId) {
        View view = mViewCache.get(resId);

        if (view == null) {
            view = getContentView().findViewById(resId);

            mViewCache.put(resId, view);
        }

        return (T) view;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public boolean isTransparentBackground() {
        return false;
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public boolean interceptTouchEvent() {
        return true;
    }

    /**
     * ?????????View???HolderView?????????
     *
     * @return
     */
    public void removeFromHolderView() {
        View contentView = getContentView();
        ViewParent parent = contentView.getParent();

        if (parent == null) {
            return;
        }

        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(contentView);
        }

        mContext = null;
        mHoldRef = null;
        mContentView = null;

        mViewCache.clear();
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public boolean stickFromHolderView() {
        View contentView = this.getContentView();
        ViewParent parent = contentView.getParent();

        if (parent == null) {
            return false;
        }

        if (parent instanceof ViewGroup) {
            ViewGroup root = ((ViewGroup) parent);

            ViewGroup.LayoutParams params =
                    contentView.getLayoutParams();

            root.removeView(contentView);
            root.addView(contentView, params);
        }
        return true;
    }

    /**
     * ????????????????????????
     *
     * @param show
     */
    public void switchContentVisible(boolean show) {
        View contentView = this.getContentView();
        int vis = show ? View.VISIBLE : View.GONE;

        contentView.setVisibility(vis);
    }

    /**
     * ???????????????HolderView
     *
     * @param parent
     */
    public void bindViewToHolderView(ViewGroup parent) {
        this.mContext = parent.getContext();
        this.mHoldRef = new WeakReference(parent);

        int matchParent = FrameLayout.LayoutParams.MATCH_PARENT;
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        matchParent,
                        matchParent);

        parent.addView(getContentView(), params);
    }

    /**
     * ??????View
     *
     * @param layoutRes
     * @return
     */
    protected View inflater(@LayoutRes int layoutRes) {
        return LayoutInflater.from(mContext).inflate(layoutRes, null);
    }

}
