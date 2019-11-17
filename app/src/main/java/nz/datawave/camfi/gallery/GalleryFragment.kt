package nz.datawave.camfi.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*
import nz.datawave.camfi.R
import nz.datawave.camfi.REQUEST_STORAGE_PERMISSION
import nz.datawave.camfi.helpers.getImagesPath
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import nz.datawave.camfi.INTENT_IMAGE_PATH
import nz.datawave.camfi.filters.FiltersActivity

class GalleryFragment: Fragment(){
    var click: Subject<String> = PublishSubject.create()

    companion object {
        fun newInstance(): GalleryFragment {
            return GalleryFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requestGalelryPermissions()
    }


    private fun requestGalelryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PermissionChecker.PERMISSION_DENIED || checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PermissionChecker.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissions, REQUEST_STORAGE_PERMISSION)
            }
            else
                initGallery()
        }
        else
            initGallery()
    }

    private fun initGallery() {
        click.subscribe(this::onSelectImage)
        gallery.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        gallery.adapter = GalleryAdapter(
            activity!!,
            getImagesPath(activity!!),
            click
        )
    }

    private fun onSelectImage(path:String){
        val intent = Intent(context, FiltersActivity::class.java)
        intent.putExtra(INTENT_IMAGE_PATH, path)
        startActivity(intent)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    initGallery()
                else
                    activity!!.finish()
            }
        }
    }
}