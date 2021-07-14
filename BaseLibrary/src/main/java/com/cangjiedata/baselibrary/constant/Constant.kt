package com.cangjiedata.baselibrary.constant

/**
 * Create by Judge at 1/20/21
 */
/**
 * 默认第一页
 */
const val PAGE_DEFAULT: Int = 1

/**
 * 城市字典
 */
const val DICTIONARY_CITY = "city"

/**
 * 岗位字典
 */
const val DICTIONARY_STATION = "station"

/**
 * 行业字典
 */
const val DICTIONARY_INDUSTRY = "industry"
const val KEY_OFFICIAL_ID = "officialId"
const val KEY = "key"
const val PATH = "Route_path"
const val EXT = "ext"
const val USER_ID = "user_id"
const val MERCHANT_ID = "merchant_id"
const val USER_BEAN = "user_bean"
const val ORG_ID = "org_id"
const val KEY_DATA = "key_data"
const val KEY_TYPE = "key_type"
const val ACCESS_TOKEN = "access_token"
const val IS_LOGIN = "isLogin"
const val IS_AUTH = "isAuth"
const val IS_MERCHANT_APP = "isMerchantApp"
const val Key_Location = "Location"
const val ORDER_TYPE_MEETING = "1";//活动订单
const val ORDER_TYPE_QR = "2"//亮码订单
const val DELETE_ORDER = "delete"//删除
const val CANCEL_ORDER = "cancel"//取消
const val ORDER_PAY_SUCCESS = "success"//支付成功
const val UPDATE_BEI = "UPDATE_BEI"//更新城贝

const val MERCHANT_PAY = "MERCHANT_PAY";//商家发起支付
const val USER_PAY_SUCCESS = "USER_PAY_SUCCESS";//支付成功
const val USER_PAY_FAIL = "USER_PAY_FAIL";//支付失敗
const val MERCHANT_PAY_TO_BE_PAID = "MERCHANT_PAY_TO_BE_PAID";//待支付(收款中)
const val MERCHANT_PAY_TIME_OUT = "MERCHANT_PAY_TIME_OUT";//超时取消
const val FREE_ORDER_SUCCESS = "FREE_ORDER_SUCCESS"//亮码订单 免费生成订单成功


const val defaultLocation = "{\"id\":2570,\"parentId\":1,\"regionCode\":\"110000\",\"regionLvl\":3,\"regionName\":\"北京市\",\"letter\":\"B\"}"

/**
 * 网络请求错误
 */
const val NetErrorMsg = "网络好像开了一点小差，请稍后～"
const val AuthErrorMsg = "您的登录信息已失效，请重新登录"

//登录拦截器extas
const val LOGIN_INTERCEPTOR = 101
const val MERCHANT_INTERCEPTOR = 102
const val KEY_WEB_URL = "webUrl"
const val KEY_ID = "key_id"
const val KEY_CHOOSE_CITY = "chooseCity"

const val ACTION_UPDATE_SINGLE_DATA = "data.action.ACTION_UPDATE_SINGLE_DATA"
const val ACTION_NEED_GPS = "data.action.ACTION_NEED_GPS"
const val KEY_TARGET = "target"
const val KEY_TARGET_DATA = "target_data"

const val ACTION_TRAVEL_PRAISE = "ACTION_TRAVEL_PRAISE"
const val ACTION_NEWS_PRAISE = "ACTION_NEWS_PRAISE"
const val ACTION_DELETE_TRAVEL = "ACTION_DELETE_TRAVEL"
const val ACTION_TOPIC_PRAISE = "ACTION_TOPIC_PRAISE"
const val ACTION_STORE_PRAISE = "ACTION_STORE_PRAISE"
const val ACTION_DELETE_TOPIC = "ACTION_DELETE_TOPIC"
const val ACTION_DELETE_COMMENT = "ACTION_DELETE_COMMENT"
const val ACTION_OFFICIAL_PRAISE = "ACTION_OFFICIAL_PRAISE"
const val ACTION_UPDATE_RECRUIT = "ACTION_UPDATE_RECRUIT"
const val ACTION_DELETE_RECRUIT = "ACTION_DELETE_RECRUIT"
const val ACTION_RECEIVE_TICKET = "ACTION_RECEIVE_TICKET"
const val ACTION_TICKET_NOTHING = "ACTION_TICKET_NOTHING"
const val ACTION_COMMENT_STORE = "ACTION_COMMENT_STORE"
const val ACTION_JOIN_CIRCLE_SUCCESS = "ACTION_JOIN_CIRCLE_SUCCESS"
const val ACTION_EXIT_CIRCLE_SUCCESS = "ACTION_EXIT_CIRCLE_SUCCESS"

const val LISTNOTIFY = "list_notify"
const val WXAPPID = "wx7f3f19a01265db3f"// 用户端微信appid
const val WXAPPIDMERCHANT = "wxc56d89a1c0564880" // 商户端微信appid
const val WXAPPSECRETMERCHANT = "de02964a06d1ecab9655a0e95552abb8" // 商户端AppSecret
const val WXMINIProgramId = "gh_995ee341f272" // 用原始id
const val QQAppID = "1111402946"
//private String weixinAppID = "wx7f3f19a01265db3f";//微信AppId
//        private String weixinAppSecret = "6e90c9a6387f4d42b837e0e2a2241e31";//微信秘钥
//        private String qqAppId = "1111402946";//QQAppId
//        private String qqAppSecret = "";//QQ秘钥
//        private String miniProgramId = "wx621b97776ce310ed";//小程序ID



