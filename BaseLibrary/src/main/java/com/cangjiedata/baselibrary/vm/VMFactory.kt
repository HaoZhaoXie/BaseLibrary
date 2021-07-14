package com.cangjiedata.baselibrary.vm

import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cangjiedata.baselibrary.BaseApplication
import java.lang.reflect.InvocationTargetException

/**
 * Create by Judge at 1/5/21
 */
/**
 * Creates a `VMFactory`
 *
 * @param application an application to pass in [AndroidViewModel]
 */
class VMFactory(var application: BaseApplication, var bundle: Bundle) :
    ViewModelProvider.NewInstanceFactory() {
    companion object {
        private var sInstance: VMFactory? = null
        fun getInstance(
            application: BaseApplication,
            bundle: Bundle
        ): VMFactory {
            if (sInstance == null) {
                sInstance = VMFactory(application, bundle)
            }
            sInstance?.let {
                it.bundle = bundle
            }
            return sInstance as VMFactory
        }
    }


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (BaseViewModel::class.java.isAssignableFrom(modelClass)) {
            try {
                modelClass.getConstructor(BaseApplication::class.java, Bundle::class.java)
                    .newInstance(application, bundle)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        } else super.create(modelClass)
    }
}