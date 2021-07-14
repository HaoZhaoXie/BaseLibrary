package com.cangjiedata.baselibrary.qr

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.cangjiedata.baselibrary.BaseApplication
import com.govmade.zxing.QRCodeUtils
import com.govmade.zxing.Resource
import com.govmade.zxing.SingleSourceLiveData
import com.govmade.zxing.ThreadManager
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class QRCodeManager(context: Context) {

    private val context: Context = context.applicationContext

    /**
     * 生成群组二维码内容
     */
    private fun generateSoQRCodeContent(groupId: String?): String {
        val fullUrl =
            Uri.Builder().path(BaseApplication.get().getWebUrl()).appendQueryParameter(KEY_TYPE, QRCodeManager.TYPE_CIRCLE).appendQueryParameter(KEY_ID, groupId).build()
        var url: String? = fullUrl.toString()
        try {
            url = URLDecoder.decode(url, "utf-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return url!!
    }

    /**
     * 生成用户二维码内容
     */
    fun generateUserQRCodeContent(userId: String?): String {
        val fullUrl =
            Uri.Builder().path(BaseApplication.get().getWebUrl()).appendQueryParameter(KEY_TYPE, QRCodeManager.TYPE_USER).appendQueryParameter(KEY_ID, userId).build()
        var url: String? = fullUrl.toString()
        try {
            url = URLDecoder.decode(url, "utf-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return url!!
    }


    /**
     * 获取群组二维码
     */
    fun getSoQRCode(groupId: String?, width: Int, height: Int): SingleSourceLiveData<Resource<Bitmap>> {
        val result = SingleSourceLiveData<Resource<Bitmap>>()
        result.postValue(Resource.loading(null))
        ThreadManager.getInstance().runOnWorkThread {
            val qrCodeContent = generateSoQRCodeContent(groupId)
            val bitmap = QRCodeUtils.generateImage(qrCodeContent, width, height, null)
            result.postValue(Resource.success(bitmap))
        }
        return result
    }


    /**
     * 生成签到二维码内容
     * https://h5.wecan.vip/?type=acitivityCode&orderNo=1615514336326463&enroleId=7&activityId=111
     */
    fun getSignCodeQRCode(content: String, width: Int, height: Int): SingleSourceLiveData<Resource<Bitmap>> { //signIn{code,code,code}
        val result = SingleSourceLiveData<Resource<Bitmap>>()
        result.postValue(Resource.loading(null))
        ThreadManager.getInstance().runOnWorkThread {
            val bitmap = QRCodeUtils.generateImage(content, width, height, null)
            result.postValue(Resource.success(bitmap))
        }
        return result
    }

    /**
     * 获取用户二维码
     */
    fun getUserQRCode(userId: String?, width: Int, height: Int): SingleSourceLiveData<Resource<Bitmap>> {
        val result = SingleSourceLiveData<Resource<Bitmap>>()
        result.postValue(Resource.loading(null))
        ThreadManager.getInstance().runOnWorkThread {
            val qrCodeContent = generateUserQRCodeContent(userId)
            val bitmap = QRCodeUtils.generateImage(qrCodeContent, width, height, null)
            result.postValue(Resource.success(bitmap))
        }
        return result
    }


    companion object {
        const val TYPE_CIRCLE = "HiCityCircleInvite"
        const val TYPE_USER = "HiCityUserInvite"
        const val TYPE_MEETING = "activityCode"
        const val TYPE_BINDING = "bindingMerchant"
        const val KEY_TYPE = "type"
        const val KEY_ID = "HiCityId"
        const val KEY_MEETING_ORDER_NUM = "orderNo"
        const val KEY_MERCHANT_ID = "merchantId"

    }

}