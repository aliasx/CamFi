package nz.datawave.camfi.helpers

import android.app.Activity
import android.provider.MediaStore

fun getImagesPath(activity: Activity): ArrayList<String> {
    val listOfAllImages = ArrayList<String>()
    val columnIndexData: Int

    val imageCursor = activity.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME),
        null,
        null,
        null)

    imageCursor?.let {
        columnIndexData = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        while (it.moveToNext())
            listOfAllImages.add(it.getString(columnIndexData))
        it.close()
    }
    return listOfAllImages
}