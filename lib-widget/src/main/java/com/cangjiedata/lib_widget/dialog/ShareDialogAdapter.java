/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: ShareDialogAdapter
 * Author: 星河
 * Date: 2021/1/9 11:19
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.lib_widget.dialog;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cangjiedata.lib_widget.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

/**
 * @ClassName: ShareDialogAdapter
 * @Description:
 * @Author: 星河
 * @Date: 2021/1/9 11:19
 */
public class ShareDialogAdapter extends BaseQuickAdapter<DialogItemBean, BaseViewHolder> {

    public ShareDialogAdapter() {
        super(R.layout.item_share_icon);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, DialogItemBean dialogItemBean) {
        Glide.with(getContext()).load(dialogItemBean.getRes()).into((ImageView) baseViewHolder.getView(R.id.iv_icon));
        baseViewHolder.setText(R.id.tv_icon,dialogItemBean.getName());
    }
}