package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;

import com.ssmomonga.ssflicker.set.DeviceSettings;

/**
 * AppShortcutInfo
 */
public class AppShortcutInfo {

    private Context context;
    private ShortcutInfo shortcutInfo;

    private LauncherApps launcherApps;
    private String longLabel;
    private Drawable activityIcon;

    /**
     * Constructor
     * @param context
     * @param shortcutInfo
     */
    public AppShortcutInfo(Context context, ShortcutInfo shortcutInfo) {
        this.context = context;
        this.shortcutInfo = shortcutInfo;
        launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
    }

    /**
     * getShortcutInfo()
     *
     * @return
     */
    public ShortcutInfo getShortcutInfo() {
        return shortcutInfo;
    }

    /**
     * getId()
     *
     * @return
     */
//    public String getId() {
//        return shortcutInfo.getId();
//    }

    /**
     * getRawLabel()
     *
     * @return
     */
    public String getRawLabel() {
        return (String) shortcutInfo.getShortLabel();
    }

    /**
     * getRawIcon()
     *
     * @return
     */
    public Drawable getRawIcon() {
        return launcherApps.getShortcutBadgedIconDrawable(shortcutInfo, (int) DeviceSettings.getDensity(context));
    }

    /**
     * getLongLabel()
     *
     * @return
     */
    public String getLongLabel() {
        if (longLabel == null) longLabel = (String) shortcutInfo.getLongLabel();
        if (longLabel == null) longLabel = getRawLabel();
        return longLabel;
    }

    /**
     * getActivityIcon()
     *
     * @return
     */
    public Drawable getActivtyIcon() {
        if (activityIcon == null) {
            PackageManager pm = context.getPackageManager();
            try {
                activityIcon = pm.getActivityIcon(shortcutInfo.getActivity());
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return activityIcon;
    }

}
