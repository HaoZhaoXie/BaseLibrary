package com.cangjiedata.upload.utils


import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import com.cangjiedata.baselibrary.BaseApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Create by Judge at 2021/5/31
 */
/**
 * Created by jingzz on 2020/7/9.
 */
typealias DOWNLOAD_ERROR = (Throwable) -> Unit
typealias DOWNLOAD_PROCESS = (downloadedSize: Long, length: Long, process: Float) -> Unit
typealias DOWNLOAD_SUCCESS = (uri: Uri) -> Unit

suspend fun download(response: Response<ResponseBody>, block:DownloadBuild.()->Unit):DownloadBuild{
    val build = DownloadBuild(response)
    build.block()
    return build
}

class DownloadBuild(val response: Response<ResponseBody>) {
    private var error: DOWNLOAD_ERROR = {} //错误贺词
    private var process: DOWNLOAD_PROCESS = { downloadedSize, filsLength, process -> } //进度
    private var success: DOWNLOAD_SUCCESS = {} //下载完成
    private val context: Context = BaseApplication.get() //全局context
    var setUri: () -> Uri? = { null } //设置下载的uri
    var setFileName: () -> String? = { null } //设置文件名

    fun process(process: DOWNLOAD_PROCESS) {
        this.process = process
    }

    fun error(error: DOWNLOAD_ERROR) {
        this.error = error
    }

    fun success(success: DOWNLOAD_SUCCESS) {
        this.success = success
    }

    suspend fun startDownload() {

        withContext(Dispatchers.Main){
            //使用流获取下载进度
            flow.flowOn(Dispatchers.IO)
                .collect {
                    when(it){
                        is DownloadStatus.DownloadError -> error(it.t)
                        is DownloadStatus.DownloadProcess -> process(it.currentLength,it.length,it.process)
                        is DownloadStatus.DownloadSuccess -> success(it.uri)
                    }
                }
        }
    }
    val flow = flow<DownloadStatus> {
        try {
            val body = response.body() ?: throw RuntimeException("下载出错")
            //文件总长度
            val length = body.contentLength()
            //文件minetype
            val contentType = body.contentType()?.toString()
            val ios = body.byteStream()
            var uri: Uri? = null
            var file: File? = null
            val ops = kotlin.run {
                setUri()?.let {
                    //url转OutPutStream
                    uri = it
                    context.contentResolver.openOutputStream(it)
                } ?: kotlin.run {
                    val fileName = setFileName() ?: kotlin.run {
                        //如果连文件名都不给，那就自己生成文件名
                        "${System.currentTimeMillis()}.${MimeTypeMap.getSingleton()
                            .getExtensionFromMimeType(contentType)}"
                    }
                    file =
                        File("${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}${File.separator}$fileName")
                    FileOutputStream(file)
                }
            }
            //下载的长度
            var currentLength: Int = 0
            //写入文件
            val bufferSize = 1024 * 8
            val buffer = ByteArray(bufferSize)
            val bufferedInputStream = BufferedInputStream(ios, bufferSize)
            var readLength: Int = 0
            while (bufferedInputStream.read(buffer, 0, bufferSize)
                    .also { readLength = it } != -1
            ) {
                ops.write(buffer, 0, readLength)
                currentLength += readLength
                emit(DownloadStatus.DownloadProcess(currentLength.toLong(),length,currentLength.toFloat() / length.toFloat()))
            }
            bufferedInputStream.close()
            ops.close()
            ios.close()
            if (uri != null) {
                emit(DownloadStatus.DownloadSuccess(uri!!))
            } else if (file != null) {
                emit(DownloadStatus.DownloadSuccess(Uri.fromFile(file)))
            }

        } catch (e: Exception) {
            emit(DownloadStatus.DownloadError(e))
        }
    }



}

sealed class DownloadStatus{
    class DownloadProcess(val currentLength:Long,val length:Long,val process:Float):DownloadStatus()
    class DownloadError(val t:Throwable):DownloadStatus()
    class DownloadSuccess(val uri:Uri):DownloadStatus()
}