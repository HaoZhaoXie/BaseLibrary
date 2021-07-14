/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: QrBean
 * Author: 星河
 * Date: 2021/2/26 10:58
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.baselibrary.qr;

import java.io.Serializable;

/**
 * @ClassName: QrBean
 * @Description:
 * @Author: 星河
 * @Date: 2021/2/26 10:58
 */
public class QrBean implements Serializable {
    private String type;
    private String avatar;
    private String name;
    private String id;

    public QrBean(String type, String avatar, String name, String id) {
        this.type = type;
        this.avatar = avatar;
        this.name = name;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}