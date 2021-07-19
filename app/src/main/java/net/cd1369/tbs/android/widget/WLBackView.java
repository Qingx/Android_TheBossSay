package net.cd1369.tbs.android.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.ConvertUtils;

import net.cd1369.tbs.android.R;

public class WLBackView extends View {

    private final Paint mPaint;
    private final Rect mRect = new Rect();
    private final Path mPath = new Path();
    private final int mOffset = ConvertUtils.dp2px(32);

    public WLBackView(Context context) {
        this(context, null);
    }

    public WLBackView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WLBackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ColorUtils.getColor(R.color.colorYellow));
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRect.set(0, 0, w, h);
        resolvePath(mRect);
    }

    private void resolvePath(Rect rect) {
        if (rect.isEmpty()) return;

        mPath.reset();
        mPath.moveTo(rect.left, rect.top);
    }
}
