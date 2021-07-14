/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: BannerClickUtils
 * Author: 星河
 * Date: 2021/4/13 9:44
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.baselibrary.utils;

import android.content.Context;
import android.text.TextUtils;

import com.cangjiedata.baselibrary.bean.BannerBean;
import com.cangjiedata.lib_widget.AdFlyBanner;

import java.util.List;

/**
 * @ClassName: BannerClickUtils
 * @Description:banner 跳转
 * @Author: 星河
 * @Date: 2021/4/13 9:44
 */
public class BannerClickUtils {
    public static void setBannerClick(Context mContext, AdFlyBanner banner, List<BannerBean> banners) {
        banner.setOnItemClickListener(new AdFlyBanner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                BannerBean bannerBean = banners.get(position);
                if(!TextUtils.isEmpty(bannerBean.getUrl())){
                    UtilsKt.doIntent(mContext, UtilsKt.buildIntent(bannerBean.getUrl()));
                }
//                switch (bannerBean.getType()) {
//                    case "task"://任务
////                        TaskDetailActivity.startTaskDetailActivity(mContext, bannerBean.getRelationId());
//                        break;
//                    case "activity"://活动
////                        ConferenceDetailsActivity.startActivity(mContext, bannerBean.getRelationId());
//                        break;
//                    case "questionnaire"://问卷
////                        SurveyListActivity.startActivity(mContext, bannerBean.getRelationId());
//                        break;
//                    case "material"://材料
////                        MaterialDetailActivity.startActivity(mContext, bannerBean.getRelationId());
//                        break;
//                    case "products"://产品
////                        if (bannerBean.getJumpType().equals("1")) {
////                            CubeDetailActivity.startDetailActivity(mContext, bannerBean.getRelationId());
////                        } else if (bannerBean.getJumpType().equals("2")) {
////                            CubeProductActivity.go2Product(mContext, "", bannerBean.getKeyWords(), bannerBean.getKeyWords());
////                        }
//                        break;
//                    case "program"://方案
////                        CubeDetailActivity.startDetailActivity(mContext, bannerBean.getRelationId());
//                        break;
//                    case "exam":// 考试 跳转到考试说明
////                        ExaminationStartNewActivity.startActivity(mContext, Integer.parseInt(bannerBean.getRelationId()),1);
//                        break;
//                    case "other":
////                        WebActivity.startMillionPartnerActivity(mContext);
//                        break;
//                    default:
////                        if (!TextUtils.isEmpty(bannerBean.getUrl())) {
////                            WebActivity.StartWebActivity(mContext, bannerBean.getUrl());
////                        }
//                        break;
//                }
            }
        });
    }
}