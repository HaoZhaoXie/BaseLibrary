package com.cangjiedata.baselibrary.qr

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.cangjiedata.baselibrary.R
import com.cangjiedata.baselibrary.constant.Base
import com.cangjiedata.baselibrary.constant.KEY
import com.cangjiedata.baselibrary.databinding.ActivityScanBinding
import com.cangjiedata.baselibrary.utils.GlideUtil
import com.cangjiedata.baselibrary.utils.setOnClickListenerSingle
import com.cangjiedata.baselibrary.utils.toasty
import com.cangjiedata.baselibrary.view.BaseActivity
import com.govmade.zxing.QRCodeUtils
import com.govmade.zxing.Resource
import com.govmade.zxing.Status
import com.govmade.zxing.barcodescanner.CaptureManager
import com.govmade.zxing.barcodescanner.DecoratedBarcodeView
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.bean.ImageItem
import com.lzy.imagepicker.ui.ImageGridActivity
import java.util.*

/**
 * Create by Judge at 1/14/21
 */
@Route(path = Base.PAGE_SCAN_QR)
class ScanActivity : BaseActivity<ActivityScanBinding>() {
    private val REQUEST_CODE_SELECT = 100
    lateinit var capture: CaptureManager
    lateinit var barcodeScannerView: DecoratedBarcodeView
    lateinit var selectPicTv: TextView
    lateinit var lightControlTv: TextView
    lateinit var tipsTv: TextView

    private var isCameraLightOn = false
    private val mHandler = Handler { message ->
        if (message.what == 10) {
            capture.decodeContinue()
        }
        false
    }
    private var type: Int = 0;//1  商家收款  添加好友

    override fun initViews(savedInstanceState: Bundle?) {
        barcodeScannerView = viewBinding.layoutScan.findViewById(R.id.zxing_barcode_scanner)
        lightControlTv = viewBinding.layoutScan.findViewById(R.id.zxing_open_light)
        selectPicTv = viewBinding.layoutScan.findViewById(R.id.zxing_select_pic)
        tipsTv = viewBinding.layoutScan.findViewById(R.id.zxing_user_tips)
        type = intent.getIntExtra(KEY, 0)
        capture = CaptureManager(this, barcodeScannerView)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.setOnCaptureResultListener {
            handleQrCode(type, it.toString())
        }
        barcodeScannerView.viewFinder.networkChange(!isNetWorkAvailable(this))
        if (!isNetWorkAvailable(this)) {
            capture.stopDecode()
        } else {
            capture.decode()
        }
        barcodeScannerView.setTorchListener(object : DecoratedBarcodeView.TorchListener {
            override fun onTorchOn() {
                lightControlTv.text = getText(R.string.zxing_close_light)
                isCameraLightOn = true
            }

            override fun onTorchOff() {
                isCameraLightOn = false
            }
        })

        initImagePicker()
    }

    override fun initOnClick() {
        lightControlTv.setOnClickListenerSingle {
            switchCameraLight()
        }
        selectPicTv.setOnClickListenerSingle {
            if (mHandler.hasMessages(10)) {
                return@setOnClickListenerSingle
            }
            scanFromAlbum()
        }
    }

    override fun initData() {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }


    /**
     * 处理二维码结果，并跳转到相应界面
     *圈子二维码       https://www.hicity.com?type=HiCityCircleInvite&circleId=9
     * @param qrCodeText
     */
    private fun handleQrCode(type: Int, qrCodeText: String) {
        if (TextUtils.isEmpty(qrCodeText)) {
            toasty(resources.getString(R.string.zxing_qr_can_not_recognized))
            return
        }

        // 处理二维码结果
        val uiSelector = QrCodeUISelector(this)
        val resourceLiveData = uiSelector.handleUri(type, qrCodeText)
        resourceLiveData.observeForever(object : Observer<Resource<String>> {
            override fun onChanged(resource: Resource<String>) {
                if (resource.status != Status.LOADING) {
                    resourceLiveData.removeObserver(this)
                }
                if (resource.status == Status.SUCCESS) {
                    if (TextUtils.isEmpty(resource.data)) {
                        finish()
                    } else {

                    }
                } else if (resource.status == Status.ERROR) {
                    toasty("暂不支持该二维码，请检查")
                    barcodeScannerView.viewFinder.isAllowScanAnimation = false
                    lightControlTv.visibility = View.INVISIBLE
                    tipsTv.visibility = View.INVISIBLE
                    finish()
                }
            }

        })
    }

    private fun restartScan() {
        mHandler.sendEmptyMessageDelayed(10, 1500)
    }

    private fun isNetWorkAvailable(context: Context): Boolean {
        val manager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
        return info?.isConnected ?: false
    }

    /**
     * 切换摄像头照明
     */
    private fun switchCameraLight() {
        if (isCameraLightOn) {
            barcodeScannerView.setTorchOff()
        } else {
            barcodeScannerView.setTorchOn()
        }
    }

    /**
     * 从相册中选中
     */
    var images: ArrayList<ImageItem>? = null

    private fun scanFromAlbum() {
        ImagePicker.getInstance().selectLimit = 1
        ImagePicker.getInstance().isMultiMode = true
        val intent1 = Intent(this, ImageGridActivity::class.java)
        /* 如果需要进入选择的时候显示已经选中的图片，
         * 详情请查看ImagePickerActivity
         * */intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES, images)
        startActivityForResult(intent1, REQUEST_CODE_SELECT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT) { //选择图片
            //添加图片返回
            if (data != null) {
                val images = data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) as ArrayList<ImageItem>?
                if (images != null && images.size > 0) {
                    val result = QRCodeUtils.analyzeImage(images[0].path)
                    if (result!=null){
                        handleQrCode(0, result)
                    }else{
                        toasty("请选择含二维码的图片")
                    }
                }
            }
        }
    }

    private fun initImagePicker() {
        val imagePicker = ImagePicker.getInstance()
        imagePicker.imageLoader = GlideUtil() //设置图片加载器
        imagePicker.isShowCamera = true //显示拍照按钮
        imagePicker.isCrop = false //允许裁剪（单选才有效）
    }

}