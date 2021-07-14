package com.cangjiedata.baselibrary.repostory

import androidx.lifecycle.Observer
import com.cangjiedata.baselibrary.BaseApplication
import com.cangjiedata.baselibrary.bean.NetResp
import com.cangjiedata.baselibrary.constant.AuthErrorMsg
import com.cangjiedata.baselibrary.constant.Login
import com.cangjiedata.baselibrary.constant.Merchant
import com.cangjiedata.baselibrary.constant.NetErrorMsg
import com.cangjiedata.baselibrary.utils.buildIntent
import com.cangjiedata.baselibrary.utils.doIntent
import com.cangjiedata.baselibrary.utils.toasty
import com.cangjiedata.baselibrary.vm.NetworkError
import com.cangjiedata.baselibrary.vm.NetworkSuccess

/**
 * Create by Judge at 1/5/21
 * 统一结构的网络观察函数
 */
open class NetObserver<T> : Observer<NetResp<T>?> {
    override fun onChanged(t: NetResp<T>?) {
        if (t == null) {
            onNetError(NetworkError)
        } else {
            when (t.code) {
                NetworkSuccess -> {
                    if (t.data != null) {
                        onNetSuccess(t.data!!, t.tag)
                    } else {
                        onNetError(NetworkError)
                    }
                }
                401 -> {
                    onNetEnd()
                    toasty(AuthErrorMsg)
                    if (BaseApplication.get().isMerchantApp()) {
                        BaseApplication.get(). doIntent(buildIntent(Merchant.MerchantLoginActivity))
                    } else {
                        BaseApplication.get().doIntent(buildIntent(Login.PAGER_LOGIN))
                    }

                }
                else -> {
                    onNetError(t.code)
                }
            }
        }
        onNetEnd()
    }

    open fun onNetSuccess(t: T, tag:String) {
        onNetSuccess(t)
    }
    open fun onNetSuccess(t: T) {}

    open fun onNetError(int: Int) {
        toasty(NetErrorMsg)
    }

    open fun onNetEnd() {}
}