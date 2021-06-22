package cn.wl.android.lib.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import cn.wl.android.lib.R;

/**
 * Created by JustBlue on 2019-09-26.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public class LoadingDialog extends Dialog {

    private String mMsg;
    private TextView tvMessage;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public void setMsg(String msg) {
        this.mMsg = msg;

        if (tvMessage != null) {
            tvMessage.setText(msg);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);

//        getWindow().setLayout(
//                (int) (ViewGroup.LayoutParams.MATCH_PARENT*0.8),
//                ViewGroup.LayoutParams.WRAP_CONTENT);

        tvMessage = findViewById(R.id.tv_loading);
        tvMessage.setText(mMsg);
    }
}
