package com.cangjiedata.upload.bean

/**
 * Create by Judge at 1/29/21
 */
const val UPLOAD_SUCCESS = 1011
const val UPLOAD_FAILED = 1010

data class UploadResultBean(var code: Int) {
    var result: ArrayList<UploadFileBean>? = null
    var errorMsg: String? = null
}

const val DOWNLOAD_SUCCESS = 1111
const val DOWNLOAD_FAILED = 1110
const val DOWNLOAD_DURING = 1112

data class DownloadResultBean(var code: Int) {
    var downloadPath: String? = null
    var errorMsg: String? = null
    var current: Long = 0
    var total: Long = 0
}