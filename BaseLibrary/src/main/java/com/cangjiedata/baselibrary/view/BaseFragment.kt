package com.cangjiedata.baselibrary.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.cangjiedata.baselibrary.BaseApplication
import com.cangjiedata.baselibrary.R
import com.cangjiedata.baselibrary.constant.ACTION_UPDATE_SINGLE_DATA
import com.cangjiedata.baselibrary.constant.KEY_TARGET
import com.cangjiedata.baselibrary.constant.KEY_TARGET_DATA
import com.cangjiedata.baselibrary.utils.BroadcastManager
import com.cangjiedata.baselibrary.utils.PermissionTool
import com.cangjiedata.baselibrary.vm.BaseViewModel
import com.cangjiedata.baselibrary.vm.VMFactory
import com.cangjiedata.baselibrary.weight.TitleBarView
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType


/**
 * Create by Judge at 1/2/21
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseFragment<T : ViewBinding> : Fragment() {
    protected lateinit var viewBinding: T
    private val updateSingleData: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateTargetData(intent?.getStringExtra(KEY_TARGET) ?: "", intent?.getBundleExtra(KEY_TARGET_DATA) ?: Bundle())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val type: ParameterizedType = javaClass.genericSuperclass as ParameterizedType
        val cls = type.actualTypeArguments[0] as Class<*>
        try {
            val inflate = cls.getDeclaredMethod("inflate", LayoutInflater::class.java)
            viewBinding = inflate.invoke(null, layoutInflater) as T
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        ARouter.getInstance().inject(this)
        return viewBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getContext()?.let {
            BroadcastManager.instance.register(it, ACTION_UPDATE_SINGLE_DATA, updateSingleData)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(savedInstanceState)
        initOnClick()
        val titleView = viewBinding.root.findViewWithTag<TitleBarView>(getString(R.string.BaseTitle))
        if (titleView != null) {
            titleView.setBackOnclick {
                onBackPress()
            }
            titleView.onTitleRestListener = object : TitleBarView.OnTitleRestListener {
                override fun onReset() {
                    initData()
                }
            }
        } else {
            initData()
        }
    }

    abstract fun initViews(savedInstanceState: Bundle?)

    abstract fun initOnClick()

    abstract fun initData()

    fun <T : ViewModel?> getViewModel(modelClass: Class<T>, bundle: Bundle = Bundle()): T {
        val clazz = ViewModelProvider(this, VMFactory.getInstance(BaseApplication.get(), bundle)).get(modelClass)
//        val clazz = ViewModelProvider(this, SavedStateViewModelFactory(BaseApplication.get(), this, bundle)).get(modelClass)
        BaseApplication.get().saveAppViewModel("$this", modelClass.name, clazz as BaseViewModel)
        return clazz
    }

    fun <T : ViewModel?> getViewModel(modelClass: Class<T>, bundle: Bundle = Bundle(), activity: FragmentActivity): T {
        val clazz = ViewModelProvider(activity, VMFactory.getInstance(BaseApplication.get(), bundle)).get(modelClass)
//        val clazz = ViewModelProvider(activity, SavedStateViewModelFactory(BaseApplication.get(), this, bundle)).get(modelClass)
        BaseApplication.get().saveAppViewModel("$this", modelClass.name, clazz as BaseViewModel)
        return clazz
    }

    open fun onBackPress() {

    }

    open fun <T> updateOtherModel(clazz: Class<T>) {
        BaseApplication.get().updateOtherModel(clazz)
    }

    open fun setTitle(title: String) {
        try {
            viewBinding.root.findViewWithTag<TitleBarView>(getString(R.string.BaseTitle))?.setTitle(title)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun setBackIcon(resId: Int) {
        try {
            viewBinding.root.findViewWithTag<TitleBarView>(getString(R.string.BaseTitle))?.setBackIcon(resId)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun sendUpdateTargetData(action: String, bundle: Bundle) {
        context?.let {
            BroadcastManager.instance.sendUpdateBroadcast(it, action, bundle)
        }
    }

    open fun updateTargetData(action: String, bundle: Bundle) {

    }

    open fun showLoading() {
        (activity as BaseActivity<*>).showLoading()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionTool.onPermissionsResult(activity!!, requestCode, permissions, grantResults)
    }

    open fun doPermissionSuccess(permissions: MutableList<String>) {}

    open fun doPermissionReject(permissions: MutableList<String>) {}

    override fun onDestroyView() {
        BaseApplication.get().removeAppViewModel("$this")
        context?.let {
            BroadcastManager.instance.unregister(it, updateSingleData)
        }
        super.onDestroyView()
    }
}