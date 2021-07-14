package com.cangjiedata.upload.interImpl

import android.content.Context
import cn.hutool.crypto.digest.MD5
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.template.IProvider
import com.cangjiedata.baselibrary.BaseApplication
import com.cangjiedata.baselibrary.constant.Login
import com.cangjiedata.baselibrary.constant.NetErrorMsg
import com.cangjiedata.baselibrary.constant.Oss
import com.cangjiedata.baselibrary.utils.buildIntent
import com.cangjiedata.baselibrary.utils.doIntent
import com.cangjiedata.baselibrary.vm.SingleSourceLiveData
import com.cangjiedata.baselibrary.vm.getService
import com.cangjiedata.upload.OssServiceManager
import com.cangjiedata.upload.bean.*
import com.cangjiedata.upload.net.UploadService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import retrofit2.HttpException
import top.zibin.luban.Luban
import java.io.File

/**
 * Create by Judge at 1/19/21
 */

interface OpenOssService : IProvider {
    suspend fun uploadFile(file: UploadFileBean, uploadResult: SingleSourceLiveData<UploadResultBean>, time: Long = System.currentTimeMillis())

    suspend fun uploadFiles(files: ArrayList<UploadFileBean>, uploadResult: SingleSourceLiveData<UploadResultBean>, time: Long = System.currentTimeMillis())

    suspend fun downloadFile(url: String, downloadResult: SingleSourceLiveData<DownloadResultBean>, time: Long = System.currentTimeMillis())
}

@Route(path = Oss.OpenService)
class OpenOssServiceImpl : OpenOssService {
    private var mContext:Context? = null
    private var ossResourceBean: OssResourceBean? = null
    private var ossTokenTime: Long = 0

    //预上传文件池
    private var uploadTasks = ArrayList<UploadTaskBean>()

    //上传反馈池
    private var uploadTaskResults = HashMap<Long, ArrayList<UploadFileBean>?>()

    //上传数据回调池
    private var uploadResults = HashMap<Long, SingleSourceLiveData<UploadResultBean>>()

    //预下载文件池
    private var downloadTasks = ArrayList<DownloadTaskBean>()

    //下载数据回调池
    private var downloadResults = HashMap<Long, SingleSourceLiveData<DownloadResultBean>>()

    private var ossServiceManager: OssServiceManager? = null

    override suspend fun uploadFile(file: UploadFileBean, uploadResult: SingleSourceLiveData<UploadResultBean>, time: Long) {
        val fileList = ArrayList<UploadFileBean>()
        fileList.add(file)
        uploadFiles(fileList, uploadResult, time)
    }

    override suspend fun uploadFiles(files: ArrayList<UploadFileBean>, uploadResult: SingleSourceLiveData<UploadResultBean>, time: Long) {
        uploadTasks.add(UploadTaskBean(time, STATE_UNDO, files))
        uploadResults[time] = uploadResult
        if (ossResourceBean == null || System.currentTimeMillis() - ossTokenTime >= 800000) {//80000服务器设置过期时间
            initUpload()
        } else {
            startUpload()
        }
    }

    override suspend fun downloadFile(url: String, downloadResult: SingleSourceLiveData<DownloadResultBean>, time: Long) {
        downloadTasks.add(DownloadTaskBean(time, STATE_UNDO, url))
        downloadResults[time] = downloadResult
        if (ossResourceBean == null|| System.currentTimeMillis() - ossTokenTime >= 800000) {
            initDownload()
        } else {
            startDownload()
        }
    }

    override fun init(context: Context?) {
        mContext = context;
    }

    private suspend fun initUpload() {
        initOss({
            startUpload()
        }, {
            uploadResults.forEach { source ->
                source.value.postValue(UploadResultBean(UPLOAD_FAILED).apply { errorMsg = NetErrorMsg })
            }
        })
    }

    private suspend fun initDownload() {
        initOss({
            startDownload()
        }, {
            downloadResults.forEach { source ->
                source.value.postValue(DownloadResultBean(DOWNLOAD_FAILED).apply { errorMsg = NetErrorMsg })
            }
        })
    }

    private suspend fun initOss(methodSuccess: () -> Unit, methodError: () -> Unit) {
        val ossInit = GlobalScope.async {
            getService(UploadService::class.java).getOssToken()
        }
        ossInit.invokeOnCompletion {
            it?.let {
                if(it is HttpException && it.code() == 401){
                    BaseApplication.get().doIntent(buildIntent(Login.PAGER_LOGIN))
                }else {
                    methodError()
                }
            }
        }
        ossInit.await().also {
            if (it.code == 0 && it.data.businessCode == 1000) {
                ossTokenTime = System.currentTimeMillis()
                ossResourceBean = it.data.data
                ossServiceManager = OssServiceManager.instance
                ossServiceManager?.init(ossResourceBean!!).also {
                    methodSuccess()
                }
            } else {
                methodError()
            }
        }
    }

    private fun getSubmitFileName(fileName: String): String {
        return ossResourceBean!!.folder.toString() + File.separator + MD5.create().digestHex(System.currentTimeMillis().toString() + fileName) + fileName.substring(fileName.lastIndexOf("."))
    }

    private fun getFileNameFromPath(filePath: String): String {
        return if (filePath.lastIndexOf(File.separator) > 0) {
            filePath.substring(filePath.lastIndexOf(File.separator))
        } else {
            filePath
        }
    }

    private fun startUpload() {
        ossServiceManager?.let { manager ->
            uploadTasks.forEach { task ->
                try {
                    if (task.state == STATE_UNDO) {
                        task.files.forEach { file ->
                            // 忽略不压缩图片的大小
                            manager.uploadFile(getSubmitFileName(getFileNameFromPath(file.path)), if(file.compress){Luban.with(mContext).load(file.path).ignoreBy(100).get(file.path).path}else{file.path}, { resultUrl ->
                                if (uploadTaskResults[task.time] == null) {
                                    uploadTaskResults[task.time] = ArrayList()
                                }
                                file.uploadPath = resultUrl
                                uploadTaskResults[task.time]!!.add(file)

                                if (uploadTaskResults[task.time]!!.size == task.files.size) {
                                    uploadResults[task.time]?.postValue(UploadResultBean(UPLOAD_SUCCESS).apply { result = uploadTaskResults[task.time] })
                                    removeUploadTask(task, true)
                                }
                            }, { errorMsg ->
                                uploadResults[task.time]?.postValue(UploadResultBean(UPLOAD_FAILED).apply { this.errorMsg = errorMsg })
                                removeUploadTask(task, false)
                            })
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    uploadResults[task.time]?.postValue(UploadResultBean(UPLOAD_FAILED).apply { errorMsg = "上传失败" })
                    removeUploadTask(task, false)
                }
            }
        }
    }

    private fun removeUploadTask(task: UploadTaskBean, success: Boolean) {
        task.state = STATE_DONE
        if (!success) {
            uploadTaskResults[task.time]?.clear()
        }
        uploadResults.remove(task.time)
    }

    private fun startDownload() {
        ossServiceManager?.let { manager ->
            downloadTasks.forEach { task ->
                try {
                    if (task.state == STATE_UNDO) {
                        if (task.state == STATE_UNDO) {
                            manager.downloadFile(task.url, { c, t ->
                                downloadResults[task.time]?.postValue(DownloadResultBean(DOWNLOAD_DURING).apply {
                                    current = c
                                    total = t
                                })
                            }, { resultUrl ->
                                downloadResults[task.time]?.postValue(DownloadResultBean(DOWNLOAD_SUCCESS).apply { downloadPath = resultUrl })
                                removeDownloadTask(task, true)
                            }, { errorMsg ->
                                downloadResults[task.time]?.postValue(DownloadResultBean(DOWNLOAD_FAILED).apply { this.errorMsg = errorMsg })
                                removeDownloadTask(task, false)
                            })
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    downloadResults[task.time]?.postValue(DownloadResultBean(DOWNLOAD_FAILED).apply { errorMsg = "下载失败" })
                    removeDownloadTask(task, false)
                }
            }
        }
    }

    private fun removeDownloadTask(task: DownloadTaskBean, success: Boolean) {
        task.state = STATE_DONE
        if (!success) {
            uploadTaskResults[task.time]?.clear()
        }
        uploadResults.remove(task.time)
    }

//    /**
//     * 鲁班压缩
//     */
//    fun luBan(context: Context, path: String) {
//
//        Observable.create<MutableList<File>> {
//            val fileList: MutableList<File> = arrayListOf()
//            for (path in paths) {
//                fileList.add(File(path))
//            }
//            it.onNext(fileList)
//        }.observeOn(Schedulers.io())
//            .flatMap {
//                Observable.just(Luban.with(context).load(it).ignoreBy(100) // 忽略不压缩图片的大小
//                    .get())
//            }
//            .observeOn(Schedulers.io())
//            .flatMap {
//                val pathList: MutableList<String> = arrayListOf()
//                for (file in it) {
//                    pathList.add(file.path)
//                }
//
//                Observable.just(pathList)
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                l.invoke(it)
//            }, {}
//            )
//    }

}