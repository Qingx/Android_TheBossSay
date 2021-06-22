package net.cd1369.tbs.android.util

import android.graphics.Bitmap
import android.graphics.Matrix
import android.text.TextUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*


/**
 * Created by Xiang on 2020/10/10 15:42
 * @description
 * @email Cymbidium@outlook.com
 */
class ZXingUtils {
    companion object {
        fun createQRImage(content: String, w: Int, h: Int, logo: Bitmap?): Bitmap? {
            if (TextUtils.isEmpty(content)) {
                return null
            }
            /*偏移量*/
            /*偏移量*/
            var offsetX: Int = w / 2
            var offsetY: Int = h / 2

            /*生成logo*/

            /*生成logo*/
            var logoBitmap: Bitmap? = null

            if (logo != null) {
                val matrix = Matrix()
                val scaleFactor: Float =
                    Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight())
                matrix.postScale(scaleFactor, scaleFactor)
                logoBitmap =
                    Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true)
            }


            /*如果log不为null,重新计算偏移量*/


            /*如果log不为null,重新计算偏移量*/
            var logoW = 0
            var logoH = 0
            if (logoBitmap != null) {
                logoW = logoBitmap.width
                logoH = logoBitmap.height
                offsetX = (w - logoW) / 2
                offsetY = (h - logoH) / 2
            }

            /*指定为UTF-8*/

            /*指定为UTF-8*/
            val hints = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            //容错级别
            //容错级别
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            //设置空白边距的宽度
            //设置空白边距的宽度
            hints[EncodeHintType.MARGIN] = 0
            // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
            // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
            var matrix: BitMatrix? = null
            return try {
                matrix = MultiFormatWriter().encode(
                    content,
                    BarcodeFormat.QR_CODE, w, h, hints
                )

                // 二维矩阵转为一维像素数组,也就是一直横着排了
                val pixels = IntArray(w * h)
                for (y in 0 until h) {
                    for (x in 0 until w) {
                        if (x >= offsetX && x < offsetX + logoW && y >= offsetY && y < offsetY + logoH) {
                            var pixel = logoBitmap!!.getPixel(x - offsetX, y - offsetY)
                            if (pixel == 0) {
                                pixel = if (matrix.get(x, y)) {
                                    -0x1000000
                                } else {
                                    -0x1
                                }
                            }
                            pixels[y * w + x] = pixel
                        } else {
                            if (matrix.get(x, y)) {
                                pixels[y * w + x] = -0x1000000
                            } else {
                                pixels[y * w + x] = -0x1
                            }
                        }
                    }
                }
                val bitmap = Bitmap.createBitmap(
                    w, h,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
                bitmap
            } catch (e: WriterException) {
                print(e)
                null
            }
        }
    }
}