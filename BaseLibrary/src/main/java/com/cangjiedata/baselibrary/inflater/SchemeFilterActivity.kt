package com.cangjiedata.baselibrary.inflater

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter




/**
 * Create by Judge at 1/13/21
 */
class SchemeFilterActivity :  AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri: Uri? = intent.data
        ARouter.getInstance().build(uri).navigation()
        finish()
    }
}