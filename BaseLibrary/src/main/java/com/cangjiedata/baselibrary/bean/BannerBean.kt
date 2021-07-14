/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: BannerBean
 * Author: 星河
 * Date: 2021/1/6 10:58
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
package com.cangjiedata.baselibrary.bean

/**
 * @ClassName: BannerBean
 * @Description:
 * @Author: 星河
 * @Date: 2021/1/6 10:58
 */
class BannerBean {
    constructor()

    constructor(imageUrl //图片地址
                : String? = null) {
        this.imageUrl = imageUrl
    }

    constructor(path: Int? = null) {
        this.path = path;
    }

    var createBy: String? = null
    var createTime: String? = null
    var updateBy: String? = null
    var updateTime: String? = null
    var adId: String? = null
    var cityId: String? = null
    var adslotId: String? = null
    var adName: String? = null
    var beginDate: String? = null
    var endDate: String? = null
    var url: String? = null
    var text: String? = null
    var imageUrl: String? = null
    var seq: String? = null
    var type: String? = null
    var relationId: String? = null
    var keyWords: String? = null
    var path: Int? = null
}