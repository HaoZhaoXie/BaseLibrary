package com.cangjiedata.baselibrary.bean

import java.io.Serializable

data class UserBean(
    /**
     * access_token : 9d6b7853-4924-4de2-949e-dd3df8d3e777
     * token_type : bearer
     * refresh_token : ff93e3d4-c5a2-4459-b811-0c94aefc824a
     * expires_in : 2591999
     * scope : server
     * tenant_id : 1
     * license : made by gds
     * user_type : 0
     * user_id : 45
     * manage_city_id : ""
     * active : true
     * tx_token : eJyrVgrxCdZLrSjILEpVsjI0NTU1MjAw0AGLlqUWKVkpGekZKEH4xSnZiQUFmSlAdSYGBibmpsbmUJWZKal5JZlpmWANJqYw9ZnpQG6SUZBfRop+cVCAhbGHcWihV4iluVm2a0SAf7BHYoiXr1GgR1WoYVB+UGigLVRjSWYuyDFmhoamZqaGZsa1ADHULqo=
     * dept_id : ""
     * username : 18883989501
     */
    var access_token: String = "",
    var token_type: String = "",
    var refresh_token: String = "",
    var expires_in: String = "",
    var scope: String = "",
    var tenant_id: String = "",
    var license: String = "",
    var user_type: String = "",
    var manage_city_id: String = "",
    var active: Boolean = false,
    var tx_token: String = "",
    var dept_id: String = "",
    var username: String? = "",
    var isAuth: Boolean = false,
    var fansNum: Int? =0,//粉丝数
    var likesNum:Int?=0,//获赞数
    var circleNum:Int?=0,//圈子数
    var redness:Int?=1,//红度值
    var followNum:Int?=0,//关注数
    var rank:Int?=1,//合伙人等级
    var integrity:String?="",//资料完善度
    var currentId:String?="",//通行证ID
    var msg:String?="",//错误信息
    var merchant_id:String="0",//商家Id
    var businessCode:Int?=0,
) : BaseUserBean(), Serializable

open class BaseUserBean(open var user_id: String = "", open var realName: String? = "", open var avatar: String? = "") : Serializable

class LikeUserBean(var userId: String = "", var realName: String? = "", var avatar: String? = "") : Serializable
