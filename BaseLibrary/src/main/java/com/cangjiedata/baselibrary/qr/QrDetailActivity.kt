package com.cangjiedata.baselibrary.qr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewTreeObserver
import com.alibaba.android.arouter.facade.annotation.Route
import com.cangjiedata.baselibrary.constant.Base.SCANQR
import com.cangjiedata.baselibrary.constant.KEY
import com.cangjiedata.baselibrary.databinding.ActivityQrDetailBinding
import com.cangjiedata.baselibrary.utils.GlideUtil
import com.cangjiedata.baselibrary.utils.setOnClickListenerSingle
import com.cangjiedata.baselibrary.utils.toasty
import com.cangjiedata.baselibrary.view.BaseActivity
import com.govmade.zxing.FileManager
import com.govmade.zxing.Status
import com.govmade.zxing.ViewCapture
import java.util.*

/**
 * 二维码
 */
@Route(path = SCANQR)
class QrDetailActivity : BaseActivity<ActivityQrDetailBinding>() {

    companion object {
        const val TYPE_PERSONAL = "1"
        const val TYPE_SO = "2"
        const val TYPE_TASK = "3"
    }

    private var qrCodeManager: QRCodeManager? = null

    var qrBean: QrBean? = null

    override fun initViews(savedInstanceState: Bundle?) {
        if (qrCodeManager == null) {
            qrCodeManager = QRCodeManager(this)
        }
        qrBean = intent.getSerializableExtra(KEY) as QrBean

        qrBean?.run {
            GlideUtil.loadImageWithCircle(this@QrDetailActivity, avatar, viewBinding.ivAvatar)
            viewBinding.tvName.text = name
            when (type) {
                TYPE_SO -> {//群二维码
                    viewBinding.BaseTitleBar.setTitle("圈子二维码")
                    viewBinding.ivQr.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            val qrData = qrCodeManager!!.getSoQRCode(id, viewBinding.ivQr.width, viewBinding.ivQr.height)
                            qrData.observe(this@QrDetailActivity, { t -> viewBinding.ivQr.setImageBitmap(t.data) })
                            viewBinding.ivQr.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    })

                    viewBinding.tvIdNum.text = "圈子ID：$id"
                    viewBinding.tvCodeHint.text = "扫描上方二维码，加入圈子"
                }
                TYPE_PERSONAL -> {//个人二维码
                    viewBinding.BaseTitleBar.setTitle("个人二维码")
                    viewBinding.ivQr.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            val qrData = qrCodeManager!!.getUserQRCode(id, viewBinding.ivQr.width, viewBinding.ivQr.height)
                            qrData.observe(this@QrDetailActivity, { t -> viewBinding.ivQr.setImageBitmap(t.data) })
                            viewBinding.ivQr.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    })

                    viewBinding.tvIdNum.text = "个人ID：$id"
                    viewBinding.tvCodeHint.text = "扫描上方二维码，添加好友"
                }
            }
        }

    }

    override fun initOnClick() {
        viewBinding.layoutDownload.setOnClickListenerSingle {
            saveQRCodeToLocal()
        }

        viewBinding.layoutShare.setOnClickListenerSingle {
            if (checkHasStoragePermission()) {
                qrBean?.run {
                    when (type) {
                        TYPE_PERSONAL -> {
                            shareByMiniProgram("user", id, name, null)
                        }
                        TYPE_SO -> {
                            shareByMiniProgram("circle", id, name, null)
                        }
                        TYPE_TASK -> {
                        }
                    }

                }


//                val beans: MutableList<DialogItemBean> = ArrayList()
//                beans.add(DialogItemBean(R.mipmap.umeng_socialize_wechat, "微信"))
//                beans.add(DialogItemBean(R.mipmap.umeng_socialize_qq, "QQ"))
//                beans.add(DialogItemBean(R.mipmap.umeng_socialize_wxcircle, "朋友圈"))
//                beans.add(DialogItemBean(R.mipmap.umeng_socialize_wxcircle, "QQ空间"))
//                val dialog = ShareDialog(this, R.style.CommonDialogStyle, { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
//                    when (position) {
//                        0 -> {
//                        }
//                    }
//                }, beans)
//                if (!this.isFinishing) {
//                    dialog.show()
//                }
            }
        }
    }

    override fun initData() {

        when (qrBean?.type) {
            TYPE_PERSONAL -> {
            }
            TYPE_SO -> {
            }
            TYPE_TASK -> {
            }
        }
    }

    private fun checkHasStoragePermission(): Boolean {
        // 从6.0系统(API 23)开始，访问外置存储需要动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            val checkPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        }
        return true
    }

    /**
     * 保存二维码到本地
     */
    private fun saveQRCodeToLocal() {
        if (!checkHasStoragePermission()) {
            return
        }
        showLoading()
        val fileManager = FileManager(this)
        val saveFileData = fileManager.saveBitmapToPictures(ViewCapture.getViewBitmap(viewBinding.layoutCode))
        saveFileData.observe(this, { stringResource ->
            if (stringResource.status == Status.SUCCESS && !TextUtils.isEmpty(stringResource.data)) {
                dismissLoading()
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + stringResource.data)))
                toasty("保存成功")
            } else if (stringResource.status == Status.ERROR) {
                dismissLoading()
                toasty("保存失败")
            }
        })
    }
}