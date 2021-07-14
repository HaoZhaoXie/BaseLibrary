/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: DialogItemBean
 * Author: 星河
 * Date: 2021/1/9 11:20
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.lib_widget.dialog;

/**
 * @ClassName: DialogItemBean
 * @Description:
 * @Author: 星河
 * @Date: 2021/1/9 11:20
 */
public class DialogItemBean {
    private int res;
    private String name;



    public DialogItemBean(int res, String name) {
        this.res = res;
        this.name = name;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}