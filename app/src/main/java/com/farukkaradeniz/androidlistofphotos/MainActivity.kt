package com.farukkaradeniz.androidlistofphotos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.farukkaradeniz.androidlistofphotos.adapters.ListPhotosAdapter
import com.farukkaradeniz.androidlistofphotos.api.PixabayApi
import com.farukkaradeniz.androidlistofphotos.extensions.*
import com.farukkaradeniz.androidlistofphotos.model.Photo
import com.farukkaradeniz.androidlistofphotos.model.PixabayResponse
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_view.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private var pageNo = 1
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var api: PixabayApi
    private lateinit var adapter: ListPhotosAdapter
    private var dialog: AlertDialog? = null
    private var view: View? = null
    private var requestId = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        api = PixabayApi.getPixabayApi()
        layoutManager = LinearLayoutManager(this)
        adapter = ListPhotosAdapter(ArrayList<Photo>(), { photo ->
            showDialogView(photo)
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(current_page: Int) {
                getPhotos("popular", ++pageNo)
            }

        })

        getPhotos("popular", pageNo)


    }

    /**
     * RecyclerView içerisinde üzerine tıklanılan resmin daha büyük şekilde gösterilmesini
     * sağlayan dialog oluşturulur ve kullanıcıya gösterilir
     */
    private fun showDialogView(photo: Photo) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.dialog_view, null)
        }
        view?.dialogPhotoView?.visibility = View.INVISIBLE
        view?.progressBarDialog?.visibility = View.VISIBLE
        with(photo) {
            view?.dialogPhotoView?.setImage(photo.largeImageURL, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    logi("Successful download")
                    view?.progressBarDialog?.visibility = android.view.View.GONE
                    view?.dialogPhotoView?.visibility = android.view.View.VISIBLE
                    view?.saveButton?.setOnClickListener {
                        savePhoto(photo.largeImageURL, view?.dialogPhotoView?.drawable as BitmapDrawable)
                        android.widget.Toast.makeText(this@MainActivity, "File will be saved in Downloads/", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    view?.shareButton?.setOnClickListener {
                        sharePhoto(savePhoto(photo.largeImageURL, view?.dialogPhotoView?.drawable as BitmapDrawable))
                    }
                }

                override fun onError(e: Exception?) {
                    loge("Error download")
                }

            })
        }

        if (dialog == null) {
            dialog = AlertDialog.Builder(this)
                    .setView(view)
                    .setCancelable(true)
                    .setTitle("Photo from Pixabay")
                    .create()
        }

        dialog?.setView(view)
        dialog?.show()
    }

    /**
     * Pixabay API'sine istek yapılır ve alınan sonuç recyclerview içerisinde gösterilir
     */
    private fun getPhotos(searchQuery: String, page: Int) {
        api.getPhotos(searchQuery = searchQuery, page = page)
                .enqueue(object : Callback<PixabayResponse> {
                    override fun onFailure(call: Call<PixabayResponse>?, t: Throwable?) {
                        loge("Request failed ${t?.message}")
                        Toast.makeText(this@MainActivity, "Error: ${t?.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<PixabayResponse>?, response: Response<PixabayResponse>?) {
                        logi("Successful request")
                        val list = response?.body()?.hits ?: return
                        adapter.addData(list)
                        if (progressBar.visibility == View.VISIBLE) {
                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                    }
                })
    }

    /**
     * ImageView içerisinde gösterilen fotoğraf Downloads/ dizinine kaydedilir
     */
    private fun savePhoto(url: String, drawable: BitmapDrawable?): String {
        if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), requestId)
        } else {
            //exposed beyond app through ClipData.Item.getUri() hatası çözümü
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())

            val filename = url.takeLastWhile { char -> char != '/' }
            val array = drawableToByteArray(drawable)
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(directory, "/$filename")
            val out = FileOutputStream(file)
            out.write(array)
            out.flush()
            out.close()
            return file.absolutePath
        }
        return ""
    }

    /**
     * ImageView içerisinde gösterilen fotoğrafı başka uygulamalarda paylaşma
     */
    private fun sharePhoto(filePath: String) {
        if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) { //file:/// exposed beyond app through ClipData.Item.getUri() hatasını engellemek için tekrar izin alınır
            requestPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), requestId)
        } else {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "image/*"
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(filePath)))
            startActivity(Intent.createChooser(i, "Share image using"))
        }
    }

    /**
     * Bitmap, byteArray'e çeviren method
     */
    private fun drawableToByteArray(drawable: BitmapDrawable?): ByteArray? {
        val bitmap = drawable?.bitmap
        return try {
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.toByteArray()
        } catch (e: Exception) {
            loge(e.message!!)
            ByteArray(0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestId -> {
                if (grantResults.size > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    permissions.forEach { logi(it) }
                    Toast.makeText(this@MainActivity, "Permission Granted.\nClick the button again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
