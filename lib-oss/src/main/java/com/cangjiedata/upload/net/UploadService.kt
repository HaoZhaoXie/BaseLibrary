package com.cangjiedata.upload.net

import com.cangjiedata.baselibrary.bean.BaseResp
import com.cangjiedata.baselibrary.bean.BusinessResp
import com.cangjiedata.upload.bean.OssResourceBean
import retrofit2.http.GET

/**
 * Create by Judge at 1/28/21
 */
interface UploadService {

    @GET(getOssToken) //
    suspend fun getOssToken(): BaseResp<BusinessResp<OssResourceBean>>
}