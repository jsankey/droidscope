package com.zutubi.android.droidscope;

import com.zutubi.android.libpulse.ProjectStatus;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * A custom adapter that creates views showing project status.
 */
public class ProjectStatusAdapter extends ArrayAdapter<ProjectStatus>
{
    public ProjectStatusAdapter(Context context)
    {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ProjectStatus status = getItem(position);
        ProjectStatusView view;
        if (convertView == null)
        {
            view = new ProjectStatusView(getContext(), status);
        }
        else
        {
            view = (ProjectStatusView) convertView;
            view.setStatus(status);
        }

        return view;
    }
}
