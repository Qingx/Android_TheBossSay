package cn.wl.android.lib.ui.common;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;

import java.util.concurrent.Callable;

import cn.wl.android.lib.R;
import cn.wl.android.lib.utils.OnClick;

/**
 * Created by JustBlue on 2019-09-11.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public class LoadView extends LoadMoreView {

    private final Runnable doFail;

    public LoadView(Runnable doFail) {
        this.doFail = doFail;
    }

    @Override
    public void convert(BaseViewHolder holder) {
        super.convert(holder);

//        try {
//            View vEnd = holder.getView(getLoadEndViewId());
//            if (vEnd != null) {
//                TextView tvEnd = vEnd.findViewById(R.id.tv_end_name);
//                if (callEnd != null) {
//                    tvEnd.setText(callEnd.call());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        View view = holder.getView(getLoadFailViewId());
        if (view != null) {
            view.setOnClickListener(new OnClick() {
                @Override
                protected void doClick(View v) {
                    v.setVisibility(View.INVISIBLE);
                    holder.getView(getLoadingViewId()).setVisibility(View.VISIBLE);

                    doFail.run();
                }
            });
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_adapter_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.ll_adapter_loading;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.ll_adapter_failure;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.ll_adapter_no_more;
    }
}
