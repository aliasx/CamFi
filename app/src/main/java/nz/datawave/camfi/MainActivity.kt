package nz.datawave.camfi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nz.datawave.camfi.camera.CameraFragment
import nz.datawave.camfi.gallery.GalleryFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState ?: supportFragmentManager.beginTransaction()
            .replace(R.id.containerCamera, CameraFragment.newInstance())
            .replace(R.id.containerGallery, GalleryFragment.newInstance())
            .commit()
    }
}
