package com.zutubi.android.droidscope;

import com.zutubi.android.libpulse.Health;
import com.zutubi.android.libpulse.ProjectStatus;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    
    /**
     * Custom view to display a single project.
     */
    private static class ProjectStatusView extends LinearLayout
    {
        private ImageView healthImage;
        private TextView nameText;

        public ProjectStatusView(Context context, ProjectStatus status)
        {
            super(context);
            
            setOrientation(HORIZONTAL);            
            healthImage = new ImageView(context);
            healthImage.setPadding(3, 3, 3, 3);
            healthImage.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
            addView(healthImage);
            
            nameText = new TextView(context);
            nameText.setTextSize(18);
            nameText.setPadding(3, 3, 3, 3);
            addView(nameText);
            
            setStatus(status);
        }

        private void setStatus(ProjectStatus status)
        {
            healthImage.setImageResource(healthToResourceId(status.getHealth()));
            nameText.setText(status.getProjectName());
        }

        private int healthToResourceId(Health health)
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
    }
}
