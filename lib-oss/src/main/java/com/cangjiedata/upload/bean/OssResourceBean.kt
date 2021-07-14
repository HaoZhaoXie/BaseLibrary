package com.cangjiedata.upload.bean

/**
 * Create by Judge at 2020/7/9
 */
data class OssResourceBean(var accessKeyId: String, var accessKeySecret: String, var token: String, var endpoint: String) {
    /**
     * accessKeyId : STS.NUMJdJbfKwEFRnpWfquq3VFKS
     * bucketName : woneng-oss
     * accessKeySecret : GW5uhXcrL6Su6HxJwQ1tEjfJC9YnehSCqf5MZDWi5PxM
     * endpoint : http://oss-cn-hangzhou.aliyuncs.com
     * folder : 20200709
     * type : pic
     * token : CAISjwJ1q6Ft5B2yfSjIr5b4Ad7+j7lqwIetUEjBs2Ykef4fuYPgsTz2IHlJf3FuA+8Ztf8ylWxR5v0SlrNsTJlIQ0PsYtZ3651WtwSnao7AqtGzq7cDjcV36Yli0FipsvXJasDVEfnvGJ70GW2m+wZ3xbzlD0LAO3WuLZyOj7N+c90TRXPWRDFaBdBQVGQAxcgBLimTT7XPVCTnmW3NFkFllxNhgGdkk8SFz9ab9wDVgS+dqIocrJ+jJYO/PYs+fsU9ca/shLYoJvWbiHMBukQWrfYttsEep2eb5OP6KkJK/hCLP9DT9tBSNwJjbsA4YfUd8aOky6cn5beIytWrkkYWbfs7TCPZSYav0CIWg0k141dQGoABCuFamsKhLwdH2hKD9SbS1oJEtWVDAoXBGSB/GY6grkqOglcmqYfDyQ2zNGhIrZRh/P7sHwXTvNhrobsLMfV75MH1cZ4bG1GzeHKwd9TCgwKt0sHc/7imjyPdKsQ13I5WmCuLeP/a1TOIS1nd6ghg8bG3Ktz/p4ozvIj/y8JkklU=
     */
    var bucketName: String? = null
    var folder: String? = null
}

data class UploadFileBean(var path:String, var type:String? = null){
    var uploadPath:String? = null
    var compress:Boolean = false
}

const val STATE_UNDO = 231
const val STATE_DONE = 232
internal data class UploadTaskBean(var time:Long, var state:Int, var files: ArrayList<UploadFileBean>)
internal data class DownloadTaskBean(var time:Long, var state:Int, var url: String)