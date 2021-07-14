package com.cangjiedata.baselibrary.bean

import java.io.Serializable

/**
 * Create by Judge at 1/4/21
 */
/*数据解析扩展函数*/
fun <T> BaseResp<T>.dataConvert(): T {
    if (code == 0) {
        return data
    } else {
        throw Exception(msg)
    }
}

data class BaseResp<T>(var code: Int = 0, var msg: String = "", var `data`: T, var is_alert: Int = 0)

data class BusinessResp<T>(var businessCode: Int = 0, var msg: String = "", var `data`: T)

data class PageListBean<T>(var total: Long = 0, var size: Int = 0, var current: Int = 0, var searchCount: Boolean = false, var pages: Int = 0, var maxId: String? = null,
                           var records: MutableList<T>, var numberRemaining: Int = 0) : Serializable

data class NetResp<T>(
    var code: Int = 0,
    var `data`: T?,
    var msg: String? = null,
) {
    var tag: String = ""//数据标签
}
