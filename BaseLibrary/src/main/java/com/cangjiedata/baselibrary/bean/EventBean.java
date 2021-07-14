/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: EventBean
 * Author: 星河
 * Date: 2021/3/5 15:42
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.baselibrary.bean;

public class EventBean implements java.io.Serializable {
    private String action;
    private Object obj1;
    private Object obj2;
    private Object obj3;
    private Object obj4;

    public String getAction() {
        return action;
    }

    public Object getObj1() {
        return obj1;
    }

    public Object getObj2() {
        return obj2;
    }

    public Object getObj3() {
        return obj3;
    }

    public Object getObj4() {
        return obj4;
    }

    public EventBean(String action) {
        this.action = action;
    }

    public EventBean(String action, Object obj1) {
        this.action = action;
        this.obj1 = obj1;
    }

    public EventBean(String action, Object obj1, Object obj2) {
        this.action = action;
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public EventBean(String action, Object obj1, Object obj2, Object obj3) {
        this.action = action;
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.obj3 = obj3;
    }

    public EventBean(String action, Object obj1, Object obj2, Object obj3, Object obj4) {
        this.action = action;
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.obj3 = obj3;
        this.obj4 = obj4;
    }
}
