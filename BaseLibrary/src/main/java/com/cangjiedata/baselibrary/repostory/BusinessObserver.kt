package com.cangjiedata.baselibrary.repostory

import com.cangjiedata.baselibrary.bean.BaseResp
import com.cangjiedata.baselibrary.utils.toasty

/**
 * Create by Judge at 1/5/21
 * 特定结构的业务观察函数
 */
const val BusinessSuccess = 0

abstract class BusinessObserver<M> : NetObserver<BaseResp<M>>() {
    override fun onNetSuccess(t: BaseResp<M>, tag:String) {
        t.data?.let {
            if (t.data != null) {
                when (t.code) {
                    BusinessSuccess -> onSuccess(t.data, tag)
                    else -> onError(t.msg, t.is_alert)
                }
            } else {
                onError(t.msg, t.is_alert)
            }
        }
    }

    open fun onSuccess(data: M, tag: String){
        onSuccess(data)
    }

    open fun onSuccess(data: M){}

    open fun onError(msg: String, alert: Int) {
        toasty(msg)
    }
}