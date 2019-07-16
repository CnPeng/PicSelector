package com.cnpeng.picselector

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cnpeng.piclib.PictureSelector
import com.cnpeng.piclib.config.PictureMimeType
import com.cnpeng.piclib.permissions.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rxPermission = RxPermissions(this)
        bt1.setOnClickListener {
            PictureSelector.create(this@MainActivity)
                    .openGallery(PictureMimeType.ofVideo())
                    .isCamera(true)
                    .maxSelectNum(6)
                    .imageSpanCount(3)
                    .forResult(666)
        }

        bt2.setOnClickListener {
            TestHandler.openCamera(this, rxPermission, 777)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (null != data && resultCode == RESULT_OK && requestCode == 777) {
            val picList = PictureSelector.obtainMultipleResult(data)
            Log.d("拍照", picList[0].path)
        }
    }

}
