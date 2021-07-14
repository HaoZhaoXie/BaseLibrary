package com.cangjiedata.baselibrary.view

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cangjiedata.baselibrary.constant.Base.OtherAppPath
import com.cangjiedata.baselibrary.constant.KEY_WEB_URL
import com.cangjiedata.baselibrary.databinding.ActivityOtherAppRouterBinding
import com.cangjiedata.baselibrary.utils.copyToClip
import com.cangjiedata.baselibrary.utils.setOnClickListenerSingle

/**
 * Create by Judge at 1/13/21
 */
@Route(path = OtherAppPath)
class OtherAppRouterActivity : BaseActivity<ActivityOtherAppRouterBinding>() {

    @Autowired(name = KEY_WEB_URL)
    @JvmField
    var webUrl: String? = null

    override fun initViews(savedInstanceState: Bundle?) {
        viewBinding.tvUrl.text = webUrl
    }

    override fun initOnClick() {
        viewBinding.tvCopy.setOnClickListenerSingle {
            copyToClip("$webUrl")
        }
    }

    override fun initData() {

    }
}