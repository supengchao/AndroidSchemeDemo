package com.example.supengchao.mylibrary;

import android.app.Activity;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * activity堆栈管理器
 * 
 * @author BradfordLiu
 * @since 2014-12-10
 * */
public class ActivityController {
    private static ActivityController controller;

    public static ActivityController getInstance() {
        if (controller == null)
            controller = new ActivityController();
        return controller;
    }

    private ActivityController() {

    }

    private ConcurrentHashMap<Integer, SoftReference<Activity>> actMap = new ConcurrentHashMap<Integer, SoftReference<Activity>>();
    SoftReference<Activity> currentResumeActivity;

    public boolean contains(Class clazz){
        if (actMap == null || clazz == null || TextUtil.isEmpty(clazz.getName())){
            return false;
        }

        boolean contain = false;
        Iterator<SoftReference<Activity>> it = actMap.values().iterator();
        while (it.hasNext()) {
            SoftReference<Activity> sf = it.next();
            if (sf == null)
                continue;
            Activity ac = sf.get();
            if (ac == null)
                continue;
            if(ac.getClass().getName().equals(clazz.getName())){
                contain = true;
                break;
            }
        }
        return contain;
    }

    public void addAcitivty(Activity ac) {
        if (actMap == null || ac == null)
            return;
        actMap.put(ac.hashCode(), new SoftReference<Activity>(ac));
    }

    public void removeActivity(Activity ac) {
        if (actMap == null || ac == null)
            return;
        actMap.remove(ac.hashCode());
    }

    public void setTopActivity(Activity temp){
        Log.d("","NotificationService setTopActivity argu -->"+temp.getClass().getSimpleName());
        currentResumeActivity = new SoftReference<Activity>(temp);
        Log.d("", "NotificationService setTopActivity -->" + currentResumeActivity.get().getClass().getSimpleName());
    }

    public void setTopActivityNull(Activity temp){
        Log.d("","NotificationService setTopActivityNull -->"+temp.getClass().getSimpleName());
        if(getTopActivity() != null){
            if(!TextUtil.equals(getTopActivity().getClass().getSimpleName(),temp.getClass().getSimpleName())){
                return;
            }
            currentResumeActivity = null;
        }

    }

    public Activity getTopActivity(){
        if(currentResumeActivity == null){
            return null;
        }
        return currentResumeActivity.get();
    }

    public void clearActivity() {
        Iterator<SoftReference<Activity>> it = actMap.values().iterator();
        while (it.hasNext()) {
            SoftReference<Activity> sf = it.next();
            if (sf == null)
                continue;
            Activity ac = sf.get();
            if (ac == null)
                continue;
            ac.finish();

        }
        actMap.clear();
    }

    public boolean isClear() {
        if (actMap == null || actMap.size() == 0)
            return true;
        return false;
    }
}
