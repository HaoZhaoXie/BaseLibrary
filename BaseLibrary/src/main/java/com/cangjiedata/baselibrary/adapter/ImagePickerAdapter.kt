package com.cangjiedata.baselibrary.adapter

import android.text.BoringLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cangjiedata.baselibrary.R
import com.cangjiedata.baselibrary.utils.isNumber
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lzy.imagepicker.bean.ImageItem

/**
 * Create by Judge at 12/21/20
 * 图片选择适配器
 */
class ImagePickerAdapter(maxCount: Int, horizontal: Boolean = false, var add:Boolean = true) :
    BaseQuickAdapter<ImageItem, BaseViewHolder>(if(horizontal){R.layout.item_horizontal_picker_image}else{R.layout.item_picker_image}) {
    private var maxCount: Int = 9
    var realData: ArrayList<ImageItem> = ArrayList()
        private set

    override fun setNewData(data: MutableList<ImageItem>?) {
        realData = ArrayList(data)
        if (data == null || data.isEmpty()) {
            data!!.add(addImage)
        } else if (data.size < maxCount) {
            data.add(addImage)
        }
        setList(data)
    }

    override fun convert(holder : BaseViewHolder, item: ImageItem) {
        val imageView = holder.getView<ImageView>(R.id.ivIcon)

        if(isNumber(item.path)){
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.setImageResource(item.path.toInt())
            holder.setGone(R.id.ivDelete, true)
            holder.getView<ImageView>(R.id.ivDelete).setOnClickListener {  }
        }else{
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(context)
                .load(if (isNumber(item.path)) item.path.toInt() else item.path)
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存全尺寸
                .error(R.mipmap.pic_loading_9)
                .placeholder(R.drawable.shape_f9f9f9_10)
                .into((holder.getView(R.id.ivIcon)))
            holder.setGone(R.id.ivDelete, false)
            holder.getView<ImageView>(R.id.ivDelete).setOnClickListener {
                realData.remove(item)
                setNewData(realData)
            }
        }
    }

    private val addImage: ImageItem
        get() {
            val imageItem = ImageItem()
            imageItem.path = if(add){R.drawable.pic_picker_image_tianjia.toString()}else{R.mipmap.pic_picker_image_tianjia1.toString()}
            return imageItem
        }

    init {
        if (maxCount > 0) {
            this.maxCount = maxCount
        }
        setNewData(ArrayList())
    }
}