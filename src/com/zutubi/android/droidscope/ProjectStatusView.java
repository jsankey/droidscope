package com.zutubi.android.droidscope;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zutubi.android.libpulse.ProjectStatus;

/**
 * Custom view to display a single project.
 */
public class ProjectStatusView extends LinearLayout
{
    private ImageView healthImage;
    private TextView nameText;
    private ProjectStatus status;

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

    public void setStatus(ProjectStatus status)
    {
        this.status = status;
        healthImage.setImageResource(UiUtils.healthToResourceId(status.getHealth()));
        healthImage.setAnimation(UiUtils.getProjectAnimation(status));
        nameText.setText(status.getProjectName());
    }

    public ProjectStatus getStatus()
    {
        return status;
    }
}