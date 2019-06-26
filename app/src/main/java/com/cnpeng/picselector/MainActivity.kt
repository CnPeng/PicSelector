package com.cnpeng.picselector

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.cnpeng.piclib.PictureSelector
import com.cnpeng.piclib.config.PictureMimeType
import com.cnpeng.piclib.permissions.RxPermissions
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //        val toolbar: Toolbar = findViewById(R.id.toolbar)
        //        setSupportActionBar(toolbar)
        //
        //        val fab: FloatingActionButton = findViewById(R.id.fab)
        //        fab.setOnClickListener { view ->
        //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                    .setAction("Action", null).show()
        //        }
        //        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        //        val navView: NavigationView = findViewById(R.id.nav_view)
        //        val toggle = ActionBarDrawerToggle(
        //                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        //        drawerLayout.addDrawerListener(toggle)
        //        toggle.syncState()
        //
        //        navView.setNavigationItemSelectedListener(this)
        //

        val rxPermission = RxPermissions(this)
        bt.setOnClickListener {
            //            rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //                    Manifest.permission.READ_EXTERNAL_STORAGE,
            //                    Manifest.permission.CAMERA)
            //                    .subscribe(object : Observer<Boolean> {
            //                        override fun onSubscribe(d: Disposable) {
            //                        }
            //
            //                        override fun onNext(t: Boolean) {
            //                            if (t) {
            //                                PictureSelector.create(this@MainActivity)
            //                                        .openGallery(PictureMimeType.ofImage())
            //                                        .enableCrop(true)
            //                                        .isCamera(true)
            //                                        .withAspectRatio(1, 1)
            //                                        .imageSpanCount(3)
            //                                        .maxSelectNum(6)
            //                                        .forResult(666)
            //                            }
            //                        }
            //
            //                        override fun onError(e: Throwable) {
            //                        }
            //
            //                        override fun onComplete() {
            //
            //                        }
            //                    })
            rxPermission.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA)
                    .subscribe(
                            object : Observer<Boolean> {
                                override fun onComplete() {

                                }

                                override fun onSubscribe(d: Disposable) {
                                }

                                override fun onNext(t: Boolean) {
                                    if (t) {
                                        PictureSelector.create(this@MainActivity)
                                                .openGallery(PictureMimeType.ofImage())
                                                .isCamera(true)
                                                .maxSelectNum(6)
                                                .imageSpanCount(3)
                                                .forResult(666)
                                    } else {
                                        Toast.makeText(this@MainActivity, "获取权限失败", LENGTH_SHORT).show()
                                    }
                                }

                                override fun onError(e: Throwable) {
                                }
                            }
                    )
        }

    }


    //    override fun onBackPressed() {
    //        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    //        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
    //            drawerLayout.closeDrawer(GravityCompat.START)
    //        } else {
    //            super.onBackPressed()
    //        }
    //    }
    //
    //    override fun onCreateOptionsMenu(menu: Menu): Boolean {
    //        // Inflate the menu; this adds items to the action bar if it is present.
    //        menuInflater.inflate(R.menu.main, menu)
    //        return true
    //    }
    //
    //    override fun onOptionsItemSelected(item: MenuItem): Boolean {
    //        // Handle action bar item clicks here. The action bar will
    //        // automatically handle clicks on the Home/Up button, so long
    //        // as you specify a parent activity in AndroidManifest.xml.
    //        return when (item.itemId) {
    //            R.id.action_settings -> true
    //            else -> super.onOptionsItemSelected(item)
    //        }
    //    }
    //
    //    override fun onNavigationItemSelected(item: MenuItem): Boolean {
    //        // Handle navigation view item clicks here.
    //        when (item.itemId) {
    //            R.id.nav_home -> {
    //                // Handle the camera action
    //            }
    //            R.id.nav_gallery -> {
    //
    //            }
    //            R.id.nav_slideshow -> {
    //
    //            }
    //            R.id.nav_tools -> {
    //
    //            }
    //            R.id.nav_share -> {
    //
    //            }
    //            R.id.nav_send -> {
    //
    //            }
    //        }
    //        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    //        drawerLayout.closeDrawer(GravityCompat.START)
    //        return true
    //    }
}
