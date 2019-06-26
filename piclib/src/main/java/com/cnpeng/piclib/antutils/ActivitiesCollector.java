package com.cnpeng.piclib.antutils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ActivitiesCollector {
    public static List<Activity> activitieslist = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activitieslist.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activitieslist.remove(activity);
    }

    /**
     * CnPeng 16-12-26 使用迭代器避免并发修改异常 如果使用上面的方法，概率性出现同一个账号在多个设备同时登录时，无法互踢，也不崩溃，只是在log中会提示finishAll()并发修改异常
     */
    public static void finishAll() {
        ListIterator iterator = activitieslist.listIterator();
        while (iterator.hasNext()) {
            Activity activity = (Activity) iterator.next();
            if (!activity.isFinishing()) {
                activity.finish();
            }
            iterator.remove();
        }
    }
}
