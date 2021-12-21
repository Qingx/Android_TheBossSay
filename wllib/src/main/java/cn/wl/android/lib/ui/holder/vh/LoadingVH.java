package cn.wl.android.lib.ui.holder.vh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.wl.android.lib.R;
import cn.wl.android.lib.ui.holder.StatusCodePool;
import cn.wl.android.lib.view.LoadingView;
import cn.wl.android.lib.view.holder.BaseHolder;

/**
 * Created by JustBlue on 2019-08-28.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public class LoadingVH extends BaseHolder implements IAinmHolder {

    private ImageView mLoading;

    public LoadingVH() {
        super(StatusCodePool.LOADING_CODE);
    }

    @Override
    protected void initContentView(View contentView) {
        mLoading = getView(R.id.iv_loading);

        Glide.with(mLoading)
                .load(R.drawable.loading)
                .into(mLoading);
    }

    @Override
    protected View getContentView(Context context) {
        return LayoutInflater.from(context)
                .inflate(R.layout.layout_holder_loading, null);
    }

    @Override
    public void onAnim(float fraction) {
        mLoading.setScaleX(1 - fraction);
        mLoading.setScaleY(1 - fraction);

        getContentView().setAlpha(1 - fraction);
    }


}
