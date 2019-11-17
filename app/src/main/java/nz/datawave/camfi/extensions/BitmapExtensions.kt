package nz.datawave.camfi.extensions

import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import nz.datawave.camfi.R
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import nz.datawave.camfi.BuildConfig
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


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
    balloons = balloons.resize(this.width, (this.width * ratio).toInt())
    canvas.drawBitmap(this, Matrix (), null)
    canvas.drawBitmap(balloons, Matrix (), null)

    return res
}

fun Bitmap.frame(context: Context): Bitmap {
    val res = Bitmap.createBitmap(this.width, this.height, this.config)
    val canvas = Canvas(res)
    var balloons = BitmapFactory.decodeResource(context.resources, R.drawable.pencils)
    balloons = balloons.resize(this.width, this.height)
    canvas.drawBitmap(this, Matrix (), null)
    canvas.drawBitmap(balloons, Matrix (), null)

    return res
}

fun Bitmap.resize(width:Int, height:Int):Bitmap{
    return Bitmap.createScaledBitmap(
        this,
        width,
        height,
        false
    )
}

fun Bitmap.share(context: Context) {
    val bitmap = this
    CoroutineScope(Dispatchers.Main).launch {
        val uri: Uri? = async(Dispatchers.IO) {
            val file =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
            val uri =
                FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)

            return@async uri
        }.await()
        try {

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/jpeg"
            context.startActivity(Intent.createChooser(shareIntent, "Share"))

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}