package com.cnpeng.piclib.antutils;


import android.app.Activity;

import java.util.HashSet;
import java.util.Set;

/**
 * 某个Activity获取焦点之后就存储到该集合中
 */
public class ActivitiesOnResumeCollector {
    //获得执行onresume的activity，用来显示个人聊天的新消息提醒
    private static Set<Activity> activities = new HashSet<>();

    public static Set<Activity> getActivities() {
        return activities;
    }

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        for (Activity acty : activities) {
            if (acty != null && !acty.isFinishing()) {
                acty.finish();
            }
        }
        activities.clear();
    }

    /**
     * 结束某个Activity
     *
     * @param classObj Activity的class对象
     */
    public static void finishActivity(Class<?>... classObj) {
        for (Class<?> obj : classObj) {
            for (Activity acty : activities) {
                if (acty != null && acty.getClass() == obj && !acty.isFinishing()) {
                    acty.finish();
                    break;
                }
            }
        }
    }
}
