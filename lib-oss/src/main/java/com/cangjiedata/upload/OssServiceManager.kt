package com.cangjiedata.upload

import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import com.alibaba.sdk.android.oss.*
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken
import com.alibaba.sdk.android.oss.model.GetObjectRequest
import com.alibaba.sdk.android.oss.model.GetObjectResult
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.cangjiedata.baselibrary.BaseApplication.Companion.get
import com.cangjiedata.baselibrary.utils.loge
import com.cangjiedata.upload.bean.OssResourceBean
import java.io.File
import java.io.FileOutputStream


class OssServiceManager private constructor() {
    private var bucketName: String? = null
    private var path: String? = null
    private var endpoint: String? =null

    inner class STSGetter(ossSTSBean: OssResourceBean) : OSSFederationCredentialProvider() {
        private val ossFederationToken: OSSFederationToken? = null
        var ak: String = ossSTSBean.accessKeyId
        var sk: String = ossSTSBean.accessKeySecret
        var token: String = ossSTSBean.token
        var expiration: String = ossSTSBean.endpoint
        override fun getFederationToken(): OSSFederationToken {
            return OSSFederationToken(ak, sk, token, expiration)
        }

    }

    companion object {
        var oss: OSS? = null
            private set
        private var credentialProvider: OSSCredentialProvider? = null
        private var conf: ClientConfiguration? = null

        @Volatile
        private var ossUtils: OssServiceManager? = null
        val instance: OssServiceManager?
            get() {
                if (ossUtils == null) {
                    synchronized(OssServiceManager::class.java) {
                        if (ossUtils == null) {
                            ossUtils = OssServiceManager()
                        }
                    }
                }
                return ossUtils
            }
    }

    //初始化使用参数
    fun init(ossSTSBean: OssResourceBean) {
        path = ossSTSBean.folder
        bucketName = ossSTSBean.bucketName
        endpoint = ossSTSBean.endpoint
        credentialProvider = STSGetter(ossSTSBean)
        conf = ClientConfiguration()
        conf!!.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
        conf!!.socketTimeout = 15 * 1000 // socket超时，默认15秒
        conf!!.maxConcurrentRequest = 5 // 最大并发请求书，默认5个
        conf!!.maxErrorRetry = 2 // 失败后最大重试次数，默认2次
        oss = OSSClient(get(), ossSTSBean.endpoint, credentialProvider, conf)
    }

    fun uploadFile(fileName: String, filePath: String, methodSuccess: (resultUrl: String?) -> Unit, methodFailed: (errorMsg: String) -> Unit) {
        // 构造上传请求
        val put = PutObjectRequest(bucketName, fileName, filePath)
// 文件元信息的设置是可选的
// ObjectMetadata metadata = new ObjectMetadata();
// metadata.setContentType("application/octet-stream"); // 设置content-type
// metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath)); // 校验MD5
// put.setMetadata(metadata);
        val task = oss?.asyncPutObject(put, object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
            override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult?) {
                methodSuccess(oss?.presignPublicObjectURL(bucketName, fileName))
            }

            override fun onFailure(request: PutObjectRequest?, clientException: ClientException?, serviceException: ServiceException?) {
                clientException?.printStackTrace()
                serviceException?.printStackTrace()
                methodFailed("${clientException?.message}${serviceException?.message}")
            }
        })
        task?.waitUntilFinished()
    }

    fun downloadFile(url: String, methodTuring: (current: Long, total: Long) -> Unit, methodSuccess: (resultUrl: String?) -> Unit, methodFailed: (errorMsg: String) -> Unit) {
        var downloadUrl = url
        if(Uri.parse(downloadUrl).schemeSpecificPart.replace("//","").startsWith("$bucketName.${Uri.parse(endpoint).schemeSpecificPart.replace("//","")}/")){
            downloadUrl = Uri.parse(downloadUrl).schemeSpecificPart.replace("//","").replace("$bucketName.${Uri.parse(endpoint).schemeSpecificPart.replace("//","")}/", "")
        }
        loge(downloadUrl)
        val get = GetObjectRequest(bucketName, downloadUrl)
        get.setProgressListener { request, currentSize, totalSize ->
            if (TextUtils.equals(request.objectKey, downloadUrl)) {
                methodTuring(currentSize, totalSize)
            }
        }
        val task = oss?.asyncGetObject(get, object : OSSCompletedCallback<GetObjectRequest, GetObjectResult> {
            override fun onSuccess(request: GetObjectRequest?, result: GetObjectResult?) {
                result?.let {
                    //开始读取数据。
                    val length = result.contentLength
                    val buffer = ByteArray(length.toInt())
                    var readCount = 0
                    while (readCount < length) {
                        try {
                            readCount += result.objectContent.read(buffer, readCount, length.toInt() - readCount)
                        } catch (e: Exception) {
                            OSSLog.logInfo(e.toString())
                        }
                    }
                    //将下载后的文件存放在指定的本地路径。
                    try {
                        val file = File(getDownloadPath() + File.separator + request!!.objectKey)
                        if (!file.exists()) {
                            file.parentFile?.mkdirs()
                            file.createNewFile()
                        }
                        val outStream = FileOutputStream(file)
                        outStream.write(buffer)
                        outStream.close()
                        methodSuccess(file.path)
                    } catch (e: Exception) {
                        OSSLog.logInfo(e.toString())
                        methodFailed("存储失败")
                    }
                }
            }

            override fun onFailure(request: GetObjectRequest?, clientException: ClientException?, serviceException: ServiceException?) {
                clientException?.printStackTrace()
                serviceException?.printStackTrace()
                methodFailed("${clientException?.message}${serviceException?.message}")
            }
        })
        task?.waitUntilFinished()
    }

    private fun getDownloadPath(): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
            if (!File(get().getExternalFilesDir("Download")!!.path).exists()) {
                File(get().getExternalFilesDir("Download")!!.path).mkdirs()
            }
            get().getExternalFilesDir("Download")!!.path
        } else {
            get().cacheDir.path
        }
    }
}