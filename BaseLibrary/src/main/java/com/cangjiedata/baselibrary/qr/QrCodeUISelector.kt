package com.cangjiedata.baselibrary.qr

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.cangjiedata.baselibrary.BaseApplication
import com.cangjiedata.baselibrary.bean.EventBean
import com.cangjiedata.baselibrary.constant.*
import com.govmade.zxing.Resource
import org.greenrobot.eventbus.EventBus

/**
 * QR 二维码界面跳转工具
 */
class QrCodeUISelector(private val context: Context) {
    /**
     * 根据 QR 二维码中的 uri 进行相应的界面展示
     * @return Resource 状态 success 为成功跳转，data 中值为 null ;error 为没有跳转成功，此时 data 中的值为 错误信息
     */
    fun handleUri(type: Int, uriString: String?): LiveData<Resource<String>> {
        val result = MutableLiveData<Resource<String>>()
//        if (uriString!!.startsWith(BaseApplication.get().getWebUrl())) {//
        if (!TextUtils.isEmpty(uriString!!)) {//
            val uri = Uri.parse(uriString)
            //圈子二维码
            when {
                TextUtils.equals(uri.getQueryParameter(QRCodeManager.KEY_TYPE), QRCodeManager.TYPE_CIRCLE) -> {
                    //http://183.131.134.242:10173/?type=HiCityCircleInvite&HiCityId=49
                    ARouter.getInstance().build(Im.CREATECIRCLE_DETAIL).withString(KEY, uri.getQueryParameter(QRCodeManager.KEY_ID)).navigation()
                    result.postValue(Resource.success(null))
                }
                TextUtils.equals(uri.getQueryParameter(QRCodeManager.KEY_TYPE), QRCodeManager.TYPE_USER) -> {
                    if (type == 1) {//商家收款
                        //http://183.131.134.242:10173/?type=HiCityUserInvite&HiCityId=49
//                        ARouter.getInstance().build(Merchant.CollectionBillActivity).withString(USER_ID, uri.getQueryParameter(QRCodeManager.KEY_ID)).navigation()//
                        var bean = EventBean("SCAN_USER_INFO",uri.getQueryParameter(QRCodeManager.KEY_ID))
                        EventBus.getDefault().post(bean)
                    } else {//添加好友
                        //http://183.131.134.242:10173/?type=HiCityUserInvite&HiCityId=49
                        ARouter.getInstance().build(Im.PersonalActivity).withString(USER_ID, uri.getQueryParameter(QRCodeManager.KEY_ID)).navigation()//
                    }

                    result.postValue(Resource.success(null))
                }
                TextUtils.equals(uri.getQueryParameter(QRCodeManager.KEY_TYPE), QRCodeManager.TYPE_MEETING) -> {
                    //活动签到
                    //?type=activityCode&orderNo=1615514336326463&enroleId=7&activityId=111
                    ARouter.getInstance().build(Meeting.OrderVerificationActivity).withString(KEY, uri.getQueryParameter(QRCodeManager.KEY_MEETING_ORDER_NUM)).navigation()//
                    result.postValue(Resource.success(null))
                }


                TextUtils.equals(uri.getQueryParameter(QRCodeManager.KEY_TYPE), QRCodeManager.TYPE_BINDING) -> {
                    //绑定商户
                    //https://h5.wecan.vip/?type=bindingMerchant&merchantId=2
                    if (BaseApplication.get().isMerchantApp()) {
                        ARouter.getInstance().build(Merchant.BusinessClaimActivity).withString(KEY, uri.getQueryParameter(QRCodeManager.KEY_MERCHANT_ID)).navigation()//
                        result.postValue(Resource.success(null))
                    } else {
                        result.postValue(Resource.error(-1, "请使用商家端扫码！"))
                    }
                }
                else -> {
                    if (uriString.startsWith("http")) {
                        ARouter.getInstance().build(WEB.PAGER_WEB).withString(KEY_WEB_URL, uriString).navigation()
                        result.postValue(Resource.success(null))
                    } else {
                        result.postValue(Resource.error(-1, "暂不支持该二维码，请检查！"))
                    }
                }
            }

        } else {
            result.postValue(Resource.error(-1, "暂未检出二维码，请检查！"))
        }
//        } else {
//            if (uriString.startsWith("http")) {
//                ARouter.getInstance().build(WEB.PAGER_WEB).withString(KEY_WEB_URL, uriString).navigation()
//                result.postValue(Resource.success(null))
//            } else {
//                result.postValue(Resource.error(-1, "暂不支持该二维码，请检查！"))
//            }
//        }
        return result
    }
}