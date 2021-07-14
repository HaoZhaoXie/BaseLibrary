package com.cangjiedata.baselibrary.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.cangjiedata.baselibrary.view.BaseActivity

/**
 * Create by Judge at 1/19/21
 */
/** Android 6.0以上版本需要请求的权限信息(targetSdkVision >= 23)  */
const val KEY_FIRST_PERMISSION = "KEY_FIRST_PERMISSION"

class PermissionTool {
    companion object {
        private val needPermissions = ArrayList<String>() // 应用未授权的权限
        private val noAskPermissions = ArrayList<String>() // 用户默认拒绝的权限
        private var permissionList = ArrayList<String>()


        private const val PermissionRequestCode = 6554

        private fun permissionRequest(activity: Activity?, permissionList: ArrayList<String>) {
            activity?.let { act ->
                this.permissionList.addAll(permissionList)
                requestPermissionProcess(act)
            }
        }

        /** 获取AndroidManifest.xml中所有permission信息， 返回信息如{"android.permission.INTERNET", "android.permission.READ_PHONE_STATE"}  */
        private fun getPermissions(activity: Activity): Array<String?> {
            var permissions = arrayOf<String?>()
            try {
                val packageManager: PackageManager = activity.packageManager
                val packageName = activity.packageName
                val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                permissions = packageInfo.requestedPermissions
            } catch (e: Exception) {
            }
            return permissions
        }

        /** 请求所需权限 如： String[] permissions = { Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE };  */
        private fun requestPermissionProcess(activity: Activity) {
            Handler(Looper.getMainLooper()).post {
                // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
                val sdkVersion = activity.applicationInfo.targetSdkVersion
                if (Build.VERSION.SDK_INT >= 23 && sdkVersion >= 23) {
                    // 检查该权限是否已经获取
                    val needAsk = ArrayList<String>()
                    val asked = ArrayList<String>()
                    for (permission in permissionList) {
                        if (!TextUtils.isEmpty(permission)) {
                            try {
                                val ret = activity.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid())
                                // 权限是否已经 授权 GRANTED---授权 DENIED---拒绝
                                if (ret != PackageManager.PERMISSION_GRANTED && !needAsk.contains(permission)) {
                                    needAsk.add(permission)
                                }else{
                                    asked.add(permission)
                                }
                            } catch (ex: java.lang.Exception) {
                                loge("是否已授权,无法判断权限：$permission")
                            }
                        }
                    }
                    permissionList.removeAll(asked)

                    // 请求没有的权限
                    if (needAsk.size > 0) {
                        val permission: Array<String> = needAsk.toArray(arrayOfNulls(needAsk.size))
                        activity.requestPermissions(permission, PermissionRequestCode) // 从权限请求返回
//                        permissionSetting(activity, needAsk[0]) // 对话框提示跳转设置界面，添加权限
                    } else {
                        loge("应用所需权限，均已授权。")
                        onPermissionCallBak(false)
                    }
                } else {
                    onPermissionCallBak(false)
                }
            }
        }

        /** 处理权限请求结果逻辑，再次调用请求、或提示跳转设置界面  */
        fun onPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            if (requestCode == PermissionRequestCode) {
                needPermissions.clear()
                noAskPermissions.clear()
                var needCallBack = true
                for (i in permissions.indices) {
                    val permission = permissions[i]
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        try {
                            // 用户点了默认拒绝权限申请，这时候就得打开自定义dialog，让用户去设置里面开启权限
                            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    !activity.shouldShowRequestPermissionRationale(permission)
                                } else {
                                    true
                                }
                            ) {
                                logi("permissionList Size：" + permissionList.size)
                                if (permissionList.contains(permission)) {
                                    noAskPermissions.add(permission)
                                } else {
                                    if(needCallBack){
                                        needCallBack = false
                                    }
                                    logi("自动允许或拒绝权限：$permission")
                                }
                            } else {
                                // 记录需要请求的权限信息
                                needPermissions.add(permission)
                            }
                        } catch (ex: java.lang.Exception) {
                            loge("自动允许或拒绝权限,无法判断权限：$permission")
                        }
                    }
                }
                when {
                    needPermissions.size > 0 -> {
                        permissionSetting(activity, needPermissions[0]) // 对话框提示跳转设置界面，添加权限
                    }
                    noAskPermissions.size > 0 -> {
                        permissionSetting(activity, noAskPermissions[0]) // 对话框提示跳转设置界面，添加权限
                    }
                    else -> {
                        onPermissionCallBak(needCallBack)
                    }
                }
            }
        }

        /** 在手机设置中打开的应用权限  */
        private fun permissionSetting(activity: Activity, permission: String) {
            if (permission.trim { it <= ' ' } == "") return

            // 获取权限对应的标题和详细说明信息
            var permissionLabel: String
            val permissionDescription: String
            try {
                val packageManager = activity.packageManager
                val permissionInfo: PermissionInfo = packageManager.getPermissionInfo(permission, 0)
                permissionLabel = permissionInfo.loadLabel(packageManager).toString()
                permissionDescription = permissionInfo.loadDescription(packageManager).toString()
            } catch (ex: java.lang.Exception) {
                return
            }

            // 自定义Dialog弹窗，显示权限请求
            permissionLabel = "应用需要权限：$permissionLabel\r\n$permission"
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setCancelable(false)
            builder.setTitle(permissionLabel)
            builder.setMessage(permissionDescription)
            builder.setPositiveButton("去添加 权限") { dialog, _ ->
                dialog.dismiss()

                // 打开应用对应的权限设置界面
                val action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val intent = Intent(action)
                val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivityForResult(intent, permissionResultCode) // 从应用设置界面返回时执行OnActivityResult
            }
            builder.setNegativeButton("拒绝则 将影响正常使用") { dialog, _ ->
                dialog.dismiss()
                onPermissionCallBak(true)
                // 若拒绝了所需的权限请求，则退出应用
            }
            builder.show()
        }

        const val permissionResultCode = 6555

        /** Activity执行结果，回调函数  */
        fun onPermissionActivityResult(activity: Activity?, requestCode: Int) {
            if (requestCode == permissionResultCode) // 从应用权限设置界面返回
            {
                permissionRequest(activity, permissionList) // 再次进行权限请求（若存在未获取到的权限，则会自动申请）
            }
        }

        /** 执行权限请求回调逻辑  */
        private fun onPermissionCallBak(needBack: Boolean) {
            if (CallInstance != null && needBack) {
                if (noAskPermissions.size > 0 || needPermissions.size > 0) {
                    CallInstance!!.onReject()
                } else {
                    CallInstance!!.onSuccess()
                }
            }
        }

        /** 权限请求回调  */
        abstract class PermissionCallBack {
            /** 权限请求成功  */
            abstract fun onSuccess()

            abstract fun onReject()
        }

        private var CallInstance: PermissionCallBack? = null

        /** 请求权限, 请求成功后执行回调逻辑  */
        private fun permissionRequest(activity: Activity, permissionList: ArrayList<String>, Call: PermissionCallBack?) {
            CallInstance = Call
            Handler(Looper.getMainLooper()).postDelayed({ onPermissionCallBak(false) }, 30 * 1000) // 30秒后自动执行回调逻辑。确保回调会被调用。
            permissionRequest(activity, permissionList) // 执行权限请求逻辑
        }

        fun checkNeedPermission(activity: Activity, permissions: MutableList<String>, needRequest:Boolean = false): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val needAsk: ArrayList<String> = java.util.ArrayList()
                permissions.forEach {
                    if (PermissionChecker.PERMISSION_GRANTED != PermissionChecker.checkSelfPermission(activity, it) || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, it)) {
                        needAsk.add(it)
                    }
                }
                if (needAsk.size != 0) {
                    if(needRequest) {
                        permissionRequest(activity, needAsk, object : Companion.PermissionCallBack() {
                            override fun onSuccess() {
                                if (activity is BaseActivity<*>) activity.doPermissionSuccess(permissions)
                            }

                            override fun onReject() {
                                if (activity is BaseActivity<*>) activity.doPermissionReject(permissions)
                            }
                        })
                    }
                    return false
                }
            }
            return true
        }
    }
}