package net.cd1369.tbs.android.widget

import android.graphics.*
import android.graphics.drawable.Drawable
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ConvertUtils
import net.cd1369.tbs.android.R

class LabelDrawable : Drawable() {

    private var lg: LinearGradient? = null
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mRect = Rect()
    private val mPath1 = Path()
    private val mPath2 = Path()

    private val color1 = ColorUtils.getColor(R.color.label_color1)
    private val color2 = ColorUtils.getColor(R.color.label_color2)

    private val padding = ConvertUtils.dp2px(1F)
    private val angleSize = ConvertUtils.dp2px(6F)
    private val radiusSize = ConvertUtils.dp2px(8f)

    init {
        mPaint.style = Paint.Style.FILL
    }

    override fun onBoundsChange(bounds: Rect?) {
        mRect.set(bounds ?: Rect())

        resolvePath(mRect)
    }

    private fun resolvePath(rect: Rect) {
        if (rect.isEmpty) return

        val temp = Rect(rect)
        temp.left += angleSize
        temp.right -= angleSize

        mPath1.reset()
        mPath1.addRoundRect(
            RectF(temp),
            floatArrayOf(
                0F, 0F, 0F, 0F,
                radiusSize.toFloat(),
                radiusSize.toFloat(),
                radiusSize.toFloat(),
                radiusSize.toFloat(),
            ),
            Path.Direction.CCW
        )

        mPath2.reset()
        mPath2.moveTo(temp.left.toFloat(), rect.top.toFloat())
        mPath2.lineTo(temp.right.toFloat(), rect.top.toFloat())
        mPath2.lineTo(rect.right.toFloat() - padding, angleSize.toFloat())
        mPath2.lineTo(rect.left.toFloat() + padding, angleSize.toFloat())
        mPath2.close()

        lg = LinearGradient(
            rect.left.toFloat(),
            rect.top.toFloat(),
            rect.left.toFloat(),
            rect.bottom.toFloat(),
            color1, color2, Shader.TileMode.CLAMP
        )
    }

    override fun draw(canvas: Canvas) {
        if (mRect.isEmpty) return

        mPaint.color = color2
        canvas.drawPath(mPath2, mPaint)

        lg.let {
            if (it != null) {
                mPaint.shader = it
            } else {
                mPaint.color = color1
            }
        }
        canvas.drawPath(mPath1, mPaint)
        mPaint.shader = null
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSPARENT

}