package com.cangjiedata.baselibrary.vm

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.launcher.ARouter
import com.cangjiedata.baselibrary.BaseApplication
import com.cangjiedata.baselibrary.bean.NetResp
import com.cangjiedata.baselibrary.repostory.RetrofitFactory
import com.cangjiedata.baselibrary.utils.loge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Create by  at 1/5/21
 */
const val NetworkSuccess = 10021
const val NetworkError = 10022

open class BaseViewModel(application: BaseApplication, private var arguments: Bundle) : AndroidViewModel(application) {
    var needRefresh = SingleSourceLiveData<Boolean>()//通知用，统一处理

    init {
        needRefresh.postValue(false)
    }

    protected fun getArguments(): Bundle {
        return arguments
    }

    fun <T> doNetWork(method: suspend () -> T, data: SingleSourceLiveData<NetResp<T>>, tag:String = "") {
        needRefresh.postValue(false)
        viewModelScope.async {
            try {
                /*withContext表示挂起块  配合Retrofit声明的suspend函数执行 该块会挂起直到里面的网络请求完成 最一行就是返回值*/
                withContext(Dispatchers.IO) {
                    method()
                }.also {
                    loge(it.toString())
                    data.postValue(NetResp(NetworkSuccess, it).apply { this.tag = tag })
                }
            } catch (e: Exception) {
                /*请求异常的话在这里处理*/
                e.printStackTrace()
                if (e is HttpException) {
                    data.postValue(NetResp(e.code(), null))
                } else {
                    data.postValue(NetResp(NetworkError, null))
                }
                Log.i("请求失败", "${e.message}")
            }
        }.start()
    }

    fun <T> doNetWorkWithOpenService(method: suspend () -> T) {
        needRefresh.postValue(false)
        viewModelScope.async {
            try {
                /*withContext表示挂起块  配合Retrofit声明的suspend函数执行 该块会挂起直到里面的网络请求完成 最一行就是返回值*/
                withContext(Dispatchers.IO) {
                    method()
                }
            } catch (e: Exception) {
                /*请求异常的话在这里处理*/
                e.printStackTrace()
                Log.i("请求失败", "${e.message}")
            }
        }.start()
    }

    private fun doNetError(e: Exception) {

    }
}

fun <T> getARouterService(clazz: Class<T>): T {
    return ARouter.getInstance().navigation(clazz)
}

fun <T> getService(clazz: Class<T>): T {
    return RetrofitFactory.instance.getService(clazz)
}

fun <T> doOpenServiceNetWork(method: suspend () -> T, data: SingleSourceLiveData<NetResp<T>>, tag:String = "") {
    BaseApplication.get().getBaseViewModel().doNetWork(method, data, tag)
}