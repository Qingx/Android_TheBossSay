package cn.wl.android.lib.ui.holder.vh;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;

import cn.wl.android.lib.R;
import cn.wl.android.lib.core.ErrorBean;
import cn.wl.android.lib.ui.holder.StatusCodePool;
import cn.wl.android.lib.utils.WLClick;

/**
 * Created by JustBlue on 2019-08-28.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public class EmptyVH extends WLHolder implements IAinmHolder {
    private final int mTransOffset;
    private View mBotView;
    private ImageView mTopView;
    private TextView mEmptyText;

    public EmptyVH() {
        super(StatusCodePool.EMPTY_CODE);

        mTransOffset = ConvertUtils.dp2px(56);
    }

    @Override
    public void update(ErrorBean data) {
        if (data != null) {
            String msg = data.getMsg();

            if (!TextUtils.isEmpty(msg)) {
                mEmptyText.setText(msg);
            } else {
                mEmptyText.setText("暂无数据");
            }

            int icon = data.getIcon();

            if (icon != 0) {
                mTopView.setImageResource(icon);
            } else {
                mTopView.setImageResource(R.drawable.draw_empty_icon);
            }

            int size = data.getSize();

            if (size > ConvertUtils.dp2px(10)) {
                ViewGroup.LayoutParams params = mTopView.getLayoutParams();

                params.width = size;
                params.height = size;

                mTopView.setLayoutParams(params);
            }
        }
    }

    @Override
    protected void initContentView(View contentView) {
        contentView.setOnClickListener(new WLClick(v -> {
            publishClick();
        }));

        mEmptyText = getView(R.id.tv_empty);
        mTopView = getView(R.id.iv_holder_top);
        mBotView = getView(R.id.tv_holder_bottom);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_holder_empty;
    }

    @Override
    public void onAnim(float fraction) {
        mTopView.setTranslationY(-mTransOffset * fraction);
        mBotView.setTranslationY(mTransOffset * fraction);

        getContentView().setAlpha(1 - fraction);
    }

}
