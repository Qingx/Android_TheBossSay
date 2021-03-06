package cn.wl.android.lib.ui.holder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ColorUtils;

import cn.wl.android.lib.R;
import cn.wl.android.lib.utils.Actions;
import cn.wl.android.lib.utils.IntentHelper;
import cn.wl.android.lib.utils.OnClick;
import cn.wl.android.lib.utils.WLClick;
import cn.wl.android.lib.view.drawable.TitleDrawable;

/**
 * Created by JustBlue on 2019-09-03.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public class TitleProxy {

    private Space spacePosition;
    private ImageView ivTitleBg;
    private TextView tvTitleName;
    private ImageView ivTitleBack;
    private TextView tvTitleRight;
    private ImageView ivTitleIcon;
    private ImageView ivTitleRight;

    private ConstraintLayout clTitleRoot;
    private Activity mAct;

    public static Actions.Filter<Activity> mCheckAct;

    public void initView(Activity activity) {
        mAct = activity;
        clTitleRoot = activity.findViewById(R.id.cl_title_root);

        if (clTitleRoot != null) {
            ivTitleBg = clTitleRoot.findViewById(R.id.iv_title_bg);
            tvTitleName = clTitleRoot.findViewById(R.id.tv_title_name);
            ivTitleBack = clTitleRoot.findViewById(R.id.iv_title_back);
            tvTitleRight = clTitleRoot.findViewById(R.id.tv_title_right);

            ivTitleIcon = clTitleRoot.findViewById(R.id.iv_title_icon);
            ivTitleRight = clTitleRoot.findViewById(R.id.iv_title_right);
            spacePosition = clTitleRoot.findViewById(R.id.space_position);

            ivTitleBack.setOnClickListener(new OnClick() {
                @Override
                protected void doClick(View v) {
                    Context context = v.getContext();

                    if (context instanceof Activity) {
                        ((Activity) context).onBackPressed();
                    }
                }
            });
        }
    }

    /**
     * ?????????????????????
     *
     * @param darkMode
     */
    public void switchDarkMode(boolean darkMode) {
        if (ivTitleBg != null) {
            if (darkMode) {
                int color = ColorUtils.getColor(R.color.md_black_4);
                ivTitleBg.setImageDrawable(new ColorDrawable(color));
            } else {
                ivTitleBg.setImageDrawable(null);
            }
        }
    }

    /**
     * ??????????????????
     */
    public void transparent() {
        if (ivTitleBg != null) {
            int color = ColorUtils.getColor(R.color.md_black_of);
            ivTitleBg.setImageDrawable(new ColorDrawable(color));
            ivTitleBg.setAlpha(0.37f);
        }
    }

    /**
     * ??????????????????????????????
     */
    public void setBackground() {
        int color = ColorUtils.getColor(R.color.title_bar_background);
        ivTitleBg.setImageDrawable(new ColorDrawable(color));
    }

    /**
     * ??????????????????
     */
    public void transparentReal() {
        if (ivTitleBg != null) {
            int color = ColorUtils.getColor(R.color.translucent_real);
            ivTitleBg.setImageDrawable(new ColorDrawable(color));
        }
    }

    /**
     * ???????????????
     *
     * @param text
     */
    public void setTitle(CharSequence text) {
        if (tvTitleName != null) {
            tvTitleName.setText(text);
        }
    }

    /**
     * ????????????icon?????????????????????
     *
     * @param drawableRes
     * @param listener
     */
    public void setLeftIcon(@DrawableRes int drawableRes, View.OnClickListener listener) {
        if (ivTitleBack != null) {
            ivTitleBack.setImageResource(drawableRes);
            ivTitleBack.setOnClickListener(new WLClick(listener));
        }
    }

    /**
     * ???????????????icon?????????????????????
     *
     * @param drawableRes
     * @param listener
     */
    public void setRightIcon(@DrawableRes int drawableRes, View.OnClickListener listener) {
        if (ivTitleRight != null) {
            ivTitleRight.setVisibility(View.VISIBLE);
            ivTitleRight.setImageResource(drawableRes);
            ivTitleRight.setOnClickListener(new WLClick(listener));
        }
    }

    public void setRightTextColor(@ColorInt int color) {
        if (tvTitleRight != null) {
            tvTitleRight.setTextColor(color);
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param text
     * @param listener
     */
    public void setRightText(CharSequence text, View.OnClickListener listener) {
        if (tvTitleRight != null) {
            tvTitleRight.setVisibility(View.VISIBLE);
            tvTitleRight.setText(text);
            tvTitleRight.setOnClickListener(new WLClick(listener));
        }
    }

    public void setRightText(@DrawableRes int iconRes, CharSequence text, View.OnClickListener listener) {
        setRightText(text, listener);

        if (ivTitleIcon != null) {
            ivTitleIcon.setImageResource(iconRes);
            ivTitleIcon.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ????????????????????????
     *
     * @param visible
     */
    public void setLeftVisible(boolean visible) {
        if (ivTitleBack != null) {
            ivTitleBack.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setRightIconPadding(int padding) {
        if (ivTitleRight != null) {
            ivTitleRight.setPadding(padding, 0, padding, 0);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param visible
     */
    public void setRightImgVisible(boolean visible) {
        if (ivTitleRight != null) {
            ivTitleRight.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param visible
     */
    public void setRightTextVisible(boolean visible) {
        if (tvTitleRight != null) {
            tvTitleRight.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setTitleEms(int i) {
        if (tvTitleName != null) {
            tvTitleName.setMaxEms(i);
            tvTitleName.setMaxWidth(Integer.MAX_VALUE);
        }
    }
}
