package com.zutubi.android.droidscope;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.zutubi.android.libpulse.Health;
import com.zutubi.android.libpulse.ProjectStatus;

/**
 * Simple utilities for UI code that have no other home.
 */
public class UiUtils
{    
    /**
     * Converts a project health value into the resource id for a corresponding
     * icon.  For example, the health {@link Health#OK} returns the id of a
     * green icon.
     * 
     * @param health the health to get an icon id for
     * @return resource id for an icon to use to represent the given health
     */
    public static int healthToResourceId(Health health)
    {
        switch (health)
        {
            case BROKEN:
                return R.drawable.health_broken;
            case OK:
                return R.drawable.health_ok;
            case UNKNOWN:
            default:
                return R.drawable.health_unknown;
        }
    }

    /**
     * Returns an animation that can be applied to views to reflect the status
     * of a project.  For building projects, the animation will give a sense of
     * activity.  For idle projects no animation is returned.
     * 
     * @param status the project status to base the animation on
     * @return an animation that reflects the given status, may be null if the
     *         project is idle
     */
    public static Animation getProjectAnimation(ProjectStatus status)
    {
        if (status.getRunningBuild() == null)
        {
            return null;
        }
        else
        {
            AlphaAnimation animation = new AlphaAnimation(1, 0.3f);
            animation.setDuration(1000);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            return animation;
        }
    }
}
