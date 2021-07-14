/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: RouterActivityPath
 * Author: 星河
 * Date: 2021/1/4 16:56
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
package com.cangjiedata.baselibrary.constant

/**
 * Main组件
 */
object Main {
    private const val MAIN = "/module_main"

    /**
     * 主页面
     */
    const val PAGER_MAIN = "$MAIN/Main"

    /**
     * hiCity
     */
    const val FRAGMENT_MAIN = "$MAIN/hiCity"

    /**
     * 生活
     */
    const val FRAGMENT_LIVE = "$MAIN/live"

    /**
     * 我的收藏
     */
    const val CollectActivity = "$MAIN/my/CollectActivity"//我的收藏

    /**
     * 我的城事
     */
    const val MyCityLife = "$MAIN/my/cityLife"


    const val PersonalDetailsActivity = "$MAIN/PersonalActivity"//好友名片

    const val MyAttentionActivity = "$MAIN/my/MyAttentionActivity"//关注

    const val SearchAllActivity = "$MAIN/searchAllActivity"

    const val AboutActivity = "$MAIN/AboutActivity"
    const val PrivacyActivity = "$MAIN/PrivacyActivity"
}

object WEB {
    private const val MAIN_WEB = "/module_web"
    const val PAGER_WEB = "$MAIN_WEB/WebActivity"
    const val WEB_FRAGMENT = "$MAIN_WEB/WebFragment"
}

/**
 * 会议
 */
object Meeting {
    private const val MEETING = "/module_meeting"

    //会议主页
    const val MEETING_MAIN: String = "$MEETING/main"

    //会议列表
    const val MEETING_LIST: String = "$MEETING/list"

    //会议详情
    const val MEETING_DETAILS: String = "$MEETING/details"

    //选择票种
    const val MEETING_CHOICE_TICKET: String = "$MEETING/choice_ticket"

    //填写报名信息
    const val MEETING_APPLY_TICKET: String = "$MEETING/apply/ticket/info"

    //确认订单
    const val MEETING_CONFIRM_ORDER: String = "$MEETING/ConfirmOrder"

    //收藏
    const val MEETING_COLLECT = "$MEETING/collect"

    //活动购买票务信息
    const val MEETING_MY_TICKET_DETAIL = "$MEETING/myTicketDetail"

    //活动购买人员信息
    const val MEETING_MY_TICKET_USER_DETAIL = "$MEETING/ApplyMeetingUserInfoAc"
    const val OrderVerificationActivity = "$MEETING/OrderVerificationActivity"

    //我的活动票列表
    const val MyMeetingActivity = "$MEETING/my/MyMeetingActivity"//活动

    /**
     * 开放接口
     */
    const val OpenMeetingService = "$MEETING/OpenService"
    const val TicketSignInActivity = "$MEETING/TicketSignInActivity"
    const val SearchMeeting = "$MEETING/SearchMeeting"

    /**
     * 常用参会人
     */
    const val MeetingContactActivity = "$MEETING/MeetingContactActivity"

    /**
     * 活动行助手
     */
    const val ConferenceAssistant = "$MEETING/ConferenceAssistantActivity"

    /**
     * 签到详情
     */
    const val ConferenceCheckIn = "$MEETING/ConferenceCheckInActivity"
}

/**
 * 卡包
 */
object Card {
    private const val CARD = "/module_card"

    /**
     * 卡包
     */
    const val PAGE_CARD = "$CARD/main"

    //会员卡详情
    const val CARD_DETAIL = "$CARD/CARD_DETAIL"

    //使用说明
    const val InstructionsActivity = "$CARD/InstructionsActivity"
}

/**
 * 城市合伙
 */
object City {
    private const val CITY = "/module_city"

    /**
     * 城市合伙人
     */
    const val PAGE_CITY = "$CITY/main"
}

/**
 * 就业易
 */
object Recruit {
    private const val RECRUIT = "/module_Recruit"

    /**
     * 就业易
     */
    const val PAGE_RECRUIT = "$RECRUIT/main"

    /**
     * 收藏岗位
     */
    const val COLLECT_PAGE = "$RECRUIT/collect"

    /**
     * 招募类型
     */
    const val RECRUITING = "$RECRUIT/recruiting"

    /**
     * 招募详情
     */
    const val RECRUIT_DETAIL = "$RECRUIT/detail"

    /**
     * 搜索能聘
     */
    const val RECRUIT_SEARCH = "$RECRUIT/search"

    /**
     * 职位管理
     */
    const val RECRUIT_MANAGE = "$RECRUIT/manage"

    /**
     * 编辑职位
     */
    const val RECRUIT_EDIT = "$RECRUIT/editDetail"

    /**
     * 编辑职位
     */
    const val RECRUIT_DESCRIPTION = "$RECRUIT/description"

    /**
     * 职位标签
     */
    const val RECRUIT_TAGS = "$RECRUIT/tags"

    /**
     *职位筛选条件
     */
    const val RECRUIT_SIFT = "$RECRUIT/sift"

    /**
     *沟通过
     */
    const val RECRUIT_CONNECTED = "$RECRUIT/connected"

    /**
     *感兴趣
     */
    const val RECRUIT_FOLLOWED = "$RECRUIT/followed"

    /**
     *更多岗位
     */
    const val RECRUIT_MORE_RECRUIT = "$RECRUIT/moreRecruit"
}

/**
 * 登录组件
 */
object Login {
    private const val LOGIN = "/module_login"

    /**
     * 登录页
     */
    const val PAGER_LOGIN = "$LOGIN/Login"
    const val PAGER_LOGOUT = "$LOGIN/logiout"
    const val PAGE_BIND_PHONE = "$LOGIN/bindphone"

    /**
     * 我的
     */
    const val FRAGMENT_MINE = "$LOGIN/mine"

    /**
     * 服务介绍
     */
    const val FRAGMENT_INTRODUCE_SERVICE = "$LOGIN/introduceService"
    const val FRAGMENT_PUBLIC_SERVICE = "$LOGIN/publicService"

    /**
     * 福利介绍
     */
    const val FRAGMENT_INTRODUCE_WELFARE = "$LOGIN/introduceWelfare"

    /**
     * 城市福利
     */
    const val FRAGMENT_CityWELFARE = "$LOGIN/cityWelfare"

    /**
     * 证件
     */
    const val FRAGMENT_CERTIFICATE = "$LOGIN/certificate"

    /**
     * 数字身份
     */
    const val FRAGMENT_IDENTITY_CARD = "$LOGIN/identityCard"

    /**
     * 数字身份
     */
    const val PAGE_IDENTITY_CARD = "$LOGIN/identityCardActivity"


}


object Mine {
    private const val MINE = "/module_mine"
    const val wallet = "$MINE/wallet"
    const val GROWTH = "$MINE/grow_up"
    const val USERINFO = "$MINE/my/UserInfoActivity"//用户信息
    const val EDIT_USER = "$MINE/my/EditUserNameActivity"//修改用户名字
    const val USER_INTRODUCTION = "$MINE/my/IntroductionActivity"//自我介绍
    const val USER_WORK = "$MINE/my/UserWorkActivity"//添加工作
    const val USER_EDU = "$MINE/my/UserEduActivity"//添加教育经历
    const val WorkExperienceActivity = "$MINE/my/WorkExperienceActivity"//工作经历列表
    const val EditMailActivity = "$MINE/my/EditMailActivity"//修改邮箱
    const val MyFansActivity = "$MINE/my/MyFansActivity"//我的粉丝
    const val SettingActivity = "$MINE/my/MySettingActivity"//设置
    const val EditWorkInfo = "$MINE/my/EditWorkInfoActivity"//修改工作经历
    const val ExperienceTagActivity = "$MINE/my/ExperienceTagActivity"//标签
}


object Im {
    private const val TUIIM = "/module_tuikit"

    /**
     * 消息
     */
    const val FRAGMENT_IM = "$TUIIM/im"// 消息
    const val UNDEAL = "$TUIIM/unDeal"// 待处理
    const val MESSAGELIST = "$TUIIM/messageList"// 提醒
    const val CHAT = "$TUIIM/chat"// 聊天页面
    const val CHATSETTING = "$TUIIM/chat_setting"// 聊天设置页面
    const val GROUPFRIENDLIST = "$TUIIM/GroupFriendList"// 圈子列表
    const val GROUPFRIENDALLLIST = "$TUIIM/GroupFriendAllList"// 圈子列表
    const val FRIENDLIST = "$TUIIM/FriendList"// 朋友列表

    const val GROUPMANAGE = "$TUIIM/group_manage"// 群组管理
    const val INVITEFRIEND = "$TUIIM/invite_friend"// 邀请好友

    const val CREATECIRCLE_1 = "$TUIIM/create_circle_1"// 创建圈子第一步
    const val CREATECIRCLE_2 = "$TUIIM/create_circle_2"// 创建圈子第2步
    const val CREATECIRCLE_DETAIL = "$TUIIM/create_circle_detail"// 创建圈子之圈子详情
    const val CREATECIRCLE_UPDATE_NAME = "$TUIIM/create_circle_update_name"// 创建圈子之改名
    const val CREATECIRCLE_UPDATE_OTHER = "$TUIIM/create_circle_update_other"// 创建圈子之口号和介绍修改
    const val INTRO_DETAILS = "$TUIIM/intro_details"//详细介绍
    const val ADD_FRIEND = "$TUIIM/my/AddFriendActivity"//添加朋友
    const val VerifyFriendActivity = "$TUIIM/my/VerifyFriendActivity"//验证添加朋友信息
    const val VerifyApplyCircleActivity = "$TUIIM/my/VerifyApplyCircleActivity"//验证添加圈子信息
    const val CircleFragment = "$TUIIM/CircleFragment"//圈子
    const val SquareFragment = "$TUIIM/SquareFragment"//圈子广场
    const val ContactsFragment = "$TUIIM/ContactsFragment"//圈子通讯录
    const val CHOOSELOCATION = "$TUIIM/ChooseLocationActivity"//选择位置
    const val LOCATION = "$TUIIM/LocationActivity"//选择位置

    // 群内通知用
    const val CREATE_NOTICE = "$TUIIM/CreateNoticeActivity" //  发布通知
    const val NOTICE_LIST = "$TUIIM/NoticeListActivity" //  通知列表

    const val NOTICE_DETAIL = "$TUIIM/NoticeDetailActivity" //  通知详情
    const val UPDATE_NOTICE = "$TUIIM/UpdateNoticeActivity" //  更新通知

    const val ApplyFriendList = "$TUIIM/ApplyFriendList" //  好友申请列表
    const val ApplyCircleList = "$TUIIM/ApplyCircleList" //  圈子申请列表

    const val SearchUserPage = "$TUIIM/SearchUserPage" //  单搜索用户
    const val SearchCirclePage = "$TUIIM/SearchCirclePage" //  单搜索圈子
    const val FindFriendCircleActivity = "$TUIIM/FindFriendCircleActivity" //  查找好友圈子

    const val FRIEND_CHAT_SETTING = "$TUIIM/FriendConversationSettingActivity"// 好友聊天设置页面
    const val CIRCLE_CHAT_SETTING = "$TUIIM/CircleConversationSettingActivity"// 圈子聊天设置页面
    const val CIRCLE_TYPE = "$TUIIM/CircleChooseTypeActivity" //  圈子类型选择

    const val MaybeKnownActivity = "$TUIIM/MaybeKnownActivity" //  可能认识的人

    const val SearchFriendCircleActivity = "$TUIIM/my/SearchFriendCircleActivity"//搜索页
    const val SearchMoreFriendUserActivity = "$TUIIM/my/SearchMoreFriendUserActivity"//搜索更多关系好友
    const val SearchMoreFriendCircleActivity = "$TUIIM/my/SearchMoreFriendCircleActivity"//搜索更多关系圈子
    const val SearchStrangerUserCircleActivity = "$TUIIM/my/SearchStrangerUserCircleActivity"//搜索陌生圈子和好友页
    const val SearchStrangerUserActivity = "$TUIIM/SearchStrangerUserActivity" //  单搜索陌生用户
    const val SearchStrangerCircleActivity = "$TUIIM/SearchStrangerCircleActivity" //  单搜索陌生圈子

    const val JoinCircleApply = "$TUIIM/JoinCircleApply"//申请加入圈子

    const val CIRCLE_PHOTO_ALBUM = "$TUIIM/PhotoAlbumListActivity"

    const val PersonalActivity = "$TUIIM/FriendCardActivity"//个人主页
    const val SetFriendAliasActivity = "$TUIIM/SetFriendAliasActivity"//设置好友别名
    const val InvitedUserListActivity = "$TUIIM/InvitedUserListActivity"//被邀请人列表
    const val FindWithConditionActivity = "$TUIIM/FindWithConditionActivity"//按条件查找
    const val FindWithConditionPageActivity = "$TUIIM/FindWithConditionPageActivity"//按条件查找
    const val PhoneContactActivity = "$TUIIM/PhoneContactActivity"//手机通讯录
    const val NotificationSettingActivity = "$TUIIM/NotificationSettingActivity" // 通知提醒消息设置界面
}

/**
 * 城事打卡组件
 */
object Travel {
    private const val TRAVEL = "/module_travel"

    /**
     * 城市打卡
     */
    const val PAGE_TRAVEL = "$TRAVEL/main"

    /**
     * 城市打卡详情
     */
    const val PAGE_SCENERY_DETAIL = "$TRAVEL/mainDetail"

    /**
     * 发布城事打卡
     */
    const val PAGE_PUBLISH_SCENERY = "$TRAVEL/publish"

    /**
     * 城市打卡话题选择
     */
    const val PAGE_CHOOSE_SCENERY_SUBJECT = "$TRAVEL/chooseSubject"

    /**
     * 城市游记收藏
     */
    const val CollectTravel = "$TRAVEL/CollectTravel"

    /**
     * 城市游记开放接口
     */
    const val OpenService = "$TRAVEL/OpenService"

    /**
     * 城市话题类型page
     */
    const val PAGE_SUBJECT = "$TRAVEL/pageSubject"

    /**
     * 我的游记
     */
    const val PAGE_MY_SCENERY = "$TRAVEL/myScenery"

    /**
     * 风采页游记
     */
    const val PAGE_STYLE_SCENERY = "$TRAVEL/styleScenery"

    /**
     * 搜索
     */
    const val SearchTravel = "$TRAVEL/searchTravel"
}

/**
 * 城市热议
 */
object Topic {
    private const val TOPIC = "/module_topic"

    /**
     *城市热议收藏
     */
    const val CollectTopic = "$TOPIC/CollectTopic"

    /**
     * 城市热议首页
     */
    const val TopicMain = "$TOPIC/TopicMain"

    /**
     * 城市热议详情
     */
    const val TopicDetail = "$TOPIC/TopicDetail"

    /**
     * 参与城市热议
     */
    const val JoinTopic = "$TOPIC/JoinTopic"

    /**
     * 发布城市热议评论列表
     */
    const val PAGE_TopicComment = "$TOPIC/PAGE_TopicComment"

    /**
     * 我的游记
     */
    const val PAGE_MY_TOPIC = "$TOPIC/myTopic"

    /**
     * 全城热议开放接口
     */
    const val OpenService = "$TOPIC/OpenService"

    /**
     * 搜索全城热议
     */
    const val SearchTopic = "$TOPIC/SearchTopic"

    /**
     * 选择全城热议
     */
    const val ChooseTopic = "$TOPIC/ChooseTopic"
}

/**
 * 消费通组件
 */
object Consumption {
    private const val Consumption = "/module_Consumption"

    /**
     * 消费通
     */
    const val Main = "$Consumption/Main"


    const val CityBei = "$Consumption/CityBei"

    /**
     * 账单列表
     */
    const val CityTaskBillActivity = "$Consumption/CityTaskBillActivity"

    /**
     * 城贝商品详情
     */
    const val GoodsDetails = "$Consumption/GoodsDetails"

    /**
     * 抢券
     */
    const val TakeTicket = "$Consumption/TakeTicket"

    /**
     * 花城贝
     */
    const val ExpendBeiFragment = "$Consumption/ExpendBeiFragment"

    /**
     * 赚城贝
     */
    const val MakeBeiFragment = "$Consumption/MakeBeiFragment"

    /**
     * 消费通fragment
     */
    const val Fragment_Consumption = "$Consumption/Fragment_Consumption"

    /**
     * 好券等你
     */
    const val TicketTab = "$Consumption/ticketTab"

    /**
     * 搜券
     */
    const val SearchTicket = "$Consumption/searchTicket"

    /**
     * 券详情
     */
    const val TicketDetail = "$Consumption/TicketDetail"

    /**
     *我的优惠券列表
     */
    const val MyTicket = "$Consumption/MyTicketList"

    /**
     * 开放服务器
     */
    const val OpenConsumptionService = "$Consumption/openService"

    /**
     * 购买优惠券
     */
    const val BuyTicket = "$Consumption/BuyTicket"

    /**
     * 优惠券订单详情
     */
    const val TicketOrder = "$Consumption/TicketOrder"

    /**
     * 优惠券支付反馈
     */
    const val PayResult = "$Consumption/PayResult"

    /**
     * 城贝商城兑换结果
     */
    const val ExchangeResultActivity = "$Consumption/ExchangeResultActivity"

    /**
     * 商品列表
     */
    const val CityGoodsListActivity = "$Consumption/CityGoodsListActivity"

    /**
     * 店铺详情
     */
    const val ShopStoreActivity = "$Consumption/ShopStoreActivity"

    /**
     * 店铺详情
     */
    const val TicketRules = "$Consumption/TicketRules"

    /**
     * 店铺地图
     */
    const val StoreMap = "$Consumption/StoreMap"

    /**
     * 使用记录
     */
    const val UsedTicket = "$Consumption/UsedTicket"

    /**
     * 所有店铺评论
     */
    const val AllStoreComment = "$Consumption/AllStoreComment"

    /**
     * 评论店铺
     */
    const val CommentStore = "$Consumption/CommentStore"

}

object DictionaryLib {
    private const val DictionaryLib = "/module_lib_dictionary"

    /**
     * 服务
     */
    private const val Service = "$DictionaryLib/Service"

    /**
     * 地图服务
     */
    const val LocationService = "$Service/location"

    /**
     * 字典服务
     */
    const val DictionaryService = "$Service/dictionaryService"

    /**
     * 字典选择服务
     */
    private const val Dictionary = "$DictionaryLib/Dictionary"

    /**
     *城市选择器
     */
    const val ChooseCity = "$Dictionary/chooseCity"

    /**
     *附近城市选择器
     */
    const val ChooseNearbyCity = "$Dictionary/ChooseNearbyCity"

    /**
     * 选择行业
     */
    const val ChooseIndustryActivity = "$Dictionary/ChooseIndustryActivity"

    /**
     *城市地址选择器
     */
    const val ChooseAddress = "$Dictionary/ChooseAddress"

    /**
     *职位岗位选择器
     */
    const val ChoosePosition = "$Dictionary/ChoosePosition"

    /**
     * 举报
     */
    const val Accusation = "$Dictionary/Accusation"
}

object News {
    private const val News = "/news"

    /**
     * 资讯开放接口
     */
    const val OpenNewsService = "$News/OpenService"

    /**
     * 资讯首页
     */
    const val NewsMain = "$News/NewsMain"

    /**
     * 资讯page
     */
    const val NewsFragment = "$News/NewsFragment"

    /**
     * 资讯详情
     */
    const val NewsDetail = "$News/detail"

    /**
     * 资讯收藏
     */
    const val COLLECT_PAGE = "$News/COLLECT_PAGE"
}

object Official {
    private const val Official = "/Official"

    /**
     * 官方首页
     */
    const val OfficialMain = "$Official/main"

    /**
     * 官方page
     */
    const val OfficialFragment = "$Official/OfficialFragment"

    /**
     * 官方消息开放接口
     */
    const val OpenOfficialService = "$Official/openService"

    /**
     * 官方消息详情
     */
    const val OfficialDetail = "$Official/detail"

    /**
     * 官方消息搜索
     */
    const val SearchOfficial = "$Official/SearchOfficial"

    /**
     * 标签资讯页
     */
    const val OfficialWithTag = "$Official/OfficialWithTag"
}

object Banner {
    private const val Banner = "/banner"

    /**
     * banner开放接口
     */
    const val OpenBannerService = "$Banner/openService"
}

object Comment {
    private const val Comment = "/comment"

    /**
     * 评论开放接口
     */
    const val OpenCommentService = "$Comment/openService"

    //个人评论页
    const val PersonalComment = "$Comment/personalComment"
}

object Base {
    private const val BASE = "/base"

    /**
     * 路由错误
     */
    const val RouteError = "$BASE/RouteError"

    /**
     * 外部链接地址
     */
    const val OtherAppPath = "$BASE/OtherAppPath"

    const val SCANQR = "$BASE/qrDetails"// 二维码

    /**
     * 二维码扫描
     */
    const val PAGE_SCAN_QR = "$BASE/scanQr"
}

object Oss {
    private const val OSS = "/ossLibrary"

    /**
     * OSS上传下载服务
     */
    const val OpenService = "$OSS/openService"
}

/**
 * 商户端路由管理
 */
object Merchant {
    private const val Merchant = "/Merchant"

    /**
     * 商户首页
     */
    const val MERCHANT_HOME = "$Merchant/MainActivity"

    /**
     * 设置页
     */
    const val MERCHANT_SETTING = "$Merchant/SettingActivity"

    /**
     * 账户列表
     */
    const val MERCHANT_ACCOUNT_LIST = "$Merchant/AccountListActivity"

    /**
     * s收款记录
     */
    const val MERCHANT_ACCOUNT_RECEIPT = "$Merchant/ReceiptRecordListActivity"

    /**
     * 账户详情
     */
    const val MERCHANT_ACCOUNT_DETAIL = "$Merchant/AccountDetailActivity"


    /**
     * 商户列表
     */
    const val MERCHANT_STORES_LIST = "$Merchant/MyStoresListActivity"

    /**
     * 收银员列表
     */
    const val MERCHANT_CASHIER_LIST = "$Merchant/MyCashierListActivity"

    /**
     * 添加 or 修改收银员
     */
    const val MERCHANT_ADD_CASHIER = "$Merchant/AddOrEditCashierActivity"

    /**
     * 应用权限
     */
    const val MERCHANT_PRIVACY = "$Merchant/PrivacyActivity"

    /**
     * 应用权限
     */
    const val MERCHANT_ABOUT = "$Merchant/AboutUsActivity"

    /**
     * 店铺详情
     */
    const val MERCHANT_STORE_DETAIL = "$Merchant/StoreDetailActivity"

    /**
     * 全部券列表
     */
    const val MERCHANT_COUPON_LIST = "$Merchant/AllCouponListActivity"

    /**
     * 店铺基础设置
     */
    const val STORE_SETTING = "$Merchant/StoreSettingActivity"

    /**
     * 店铺介绍
     */
    const val STORE_UPDATE_INTRODUCE = "$Merchant/StoreUpdateIntroduceActivity"

    const val STORE_COMMENT = "$Merchant/StoreCommentActivity"

    /**
     * 发券
     */
    const val MERCHANT_COUPON_CREATE = "$Merchant/CreateCouponActivity"

    /**
     * 优惠券详情
     */
    const val MERCHANT_COUPON_DETAILS = "$Merchant/CouponDetailsActivity"

    /**
     * 优惠券领取列表
     */
    const val MERCHANT_COUPON_RECEIVED_LIST = "$Merchant/CouponReceivedListActivity"

    /**
     * 优惠券领取和核销详情
     */
    const val MERCHANT_COUPON_CHECKED_DETAIL = "$Merchant/CheckedCouponDetailsActivity"

    /**
     * 统计的web
     */
    const val MERCHANT_STATISTICS_WEB = "$Merchant/StatisticsFragment"

    /**
     * 绑定账号提示
     */
    const val MERCHANT_BIND_ACCOUNT_HINT = "$Merchant/BindAccountManageActivity"

    /**
     * 绑定个人微信
     */
    const val MERCHANT_BIND_PERSON_WEICHAT = "$Merchant/BindPersonWeiChatActivity"

    /**
     * 绑定商户微信号
     */
    const val MERCHANT_BIND_STORE_WEICHAT = "$Merchant/BindStoreWeiChatActivity"

    /**
     * 收款账户管理
     */
    const val MERCHANT_INCOME_ACCOUNT_MANAGE = "$Merchant/IncomeAccountManageActivity"

    /**
     * 收益明细列表
     */
    const val MERCHANT_EARNING_LIST = "$Merchant/EarningsListActivity"

    /**
     * 待结算列表
     */
    const val MERCHANT_SETTLEMENT_AWAIT_LIST = "$Merchant/SettlementAwaitListActivity"


    /**
     * 收钱吧
     */
    const val CollectionBillActivity = "$Merchant/CollectionBillActivity"
    const val FilterActivity = "$Merchant/FilterActivity"
    const val BusinessClaimActivity = "$Merchant/BusinessClaimActivity"
    const val MerchantLoginActivity = "$Merchant/MerchantLoginActivity"
    const val MerchantReceiveSuccessActivity = "$Merchant/MerchantReceiveSuccessActivity"


}

object Order {
    private const val ORDER = "/order"
    const val OrderMainActivity = "$ORDER/OrderMainActivity"

    //订单详情
    const val OrderDetailActivity = "$ORDER/OrderDetailActivity"

    /**
     * 活动订单详情
     */
    const val MeetingOrderDetailActivity = "$ORDER/MeetingOrderDetailActivity"

    //活动支付页面
    const val PAY_ORDER: String = "$ORDER/payorder"

    //活动订单支付结果页
    const val ORDER_RESULT: String = "$ORDER/order_result"

    //用户被扫码后的详情
    const val USER_SCAN_PAY_ORDER_DETAILS = "$ORDER/user_scan_pay_order"

    //商家收款的结果
    const val MerchantBillDetailActivity = "$ORDER/MerchantBillDetailActivity"

    //用户使用消费券结果
    const val UserBillDetailsActivity = "$ORDER/UserBillDetailsActivity"

    //亮码订单列表
    const val TicketOrderList = "$ORDER/TicketOrderList"

    /**
     * 券更多订单详情
     */
    const val ORDER_MORE_DETAIL = "${ORDER}/OrderMoreDetailActivity"
}