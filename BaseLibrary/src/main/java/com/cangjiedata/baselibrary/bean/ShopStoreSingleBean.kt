package com.cangjiedata.baselibrary.bean

import java.io.Serializable

class ShopStoreSingleBean(
    var storeName: String? = null,//店铺名称
    var locationAddr: String? = null,//店铺定位地址
    var lng: String? = null,//经度
    var lat: String? = null,//纬度
    var storeLogo: String? = null,//店铺logo
) : Serializable