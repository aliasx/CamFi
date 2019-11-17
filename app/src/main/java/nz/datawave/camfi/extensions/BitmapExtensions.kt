package nz.datawave.camfi.extensions

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import nz.datawave.camfi.R

fun Bitmap.changeColor():Bitmap {

    val result = Bitmap.createBitmap(
        this.width,
        this.height, Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(result)
    val paint = Paint()

    val cm = ColorMatrix()
    cm.setRGB2YUV()
    paint.colorFilter = ColorMatrixColorFilter(cm)
    canvas.drawBitmap(this, 0f, 0f, paint)

    return result
}

fun Bitmap.invertColor():Bitmap {

    val result = Bitmap.createBitmap(
        this.width,
        this.height, Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(result)
    val paint = Paint()

    val cmData = floatArrayOf(
        -1f,
        0f,
        0f,
        0f,
        255f,
        0f,
        -1f,
        0f,
        0f,
        255f,
        0f,
        0f,
        -1f,
        0f,
        255f,
        0f,
        0f,
        0f,
        1f,
        0f
    )
    val cm = ColorMatrix(cmData)
    paint.colorFilter = ColorMatrixColorFilter(cm)
    canvas.drawBitmap(this, 0f, 0f, paint)

    return result
}


fun Bitmap.balloons(context: Context): Bitmap {
    val res = Bitmap.createBitmap(this.width, this.height, this.config)
    val canvas = Canvas(res)
    var balloons = BitmapFactory.decodeResource(context.resources, R.drawable.balloons)
    val ratio = balloons.height.toFloat() / balloons.width.toFloat()
    balloons = Bitmap.createScaledBitmap(
        balloons,
        this.width,
        (this.width * ratio).toInt(),
        false
    )
    canvas.drawBitmap(this, Matrix (), null)
    canvas.drawBitmap(balloons, Matrix (), null)

    return res
}

fun Bitmap.frame(context: Context): Bitmap {
    val res = Bitmap.createBitmap(this.width, this.height, this.config)
    val canvas = Canvas(res)
    var balloons = BitmapFactory.decodeResource(context.resources, R.drawable.pencils)
    balloons = Bitmap.createScaledBitmap(
        balloons,
        this.width,
        this.height,
        false
    )
    canvas.drawBitmap(this, Matrix (), null)
    canvas.drawBitmap(balloons, Matrix (), null)

    return res
}

fun Bitmap.resize(size: Int): Bitmap{
        val width = this.width
        val height = this.height
        val ratioBitmap = width.toFloat() / height.toFloat()
        var finalWidth = size
        var finalHeight = size
        if (ratioBitmap < 1)
            finalWidth = (size.toFloat() * ratioBitmap).toInt()
        else
            finalHeight = (size.toFloat() / ratioBitmap).toInt()

        return Bitmap.createScaledBitmap(this, finalWidth, finalHeight, true)

}