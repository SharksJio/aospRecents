package com.custom.recents;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.List;

/**
 * Main Recents Activity - displays recent tasks
 * This is the entry point when user opens recents from Home or SystemUI
 */
public class RecentsActivity extends Activity implements View.OnClickListener {
    
    private LinearLayout mRecentsContainer;
    private View mOverviewActions;
    private View mRecentsDock;
    
    // Action buttons
    private Button mScreenshotButton;
    private Button mSelectButton;
    private Button mSplitButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make it fullscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        
        setContentView(R.layout.recents_activity);
        
        // Initialize views
        mRecentsContainer = findViewById(R.id.recents_container);
        mOverviewActions = findViewById(R.id.overview_actions_view);
        mRecentsDock = findViewById(R.id.recents_dock);
        
        // Initialize action buttons
        mScreenshotButton = findViewById(R.id.action_screenshot);
        mSelectButton = findViewById(R.id.action_select);
        mSplitButton = findViewById(R.id.action_split);
        
        // Set click listeners
        if (mScreenshotButton != null) mScreenshotButton.setOnClickListener(this);
        if (mSelectButton != null) mSelectButton.setOnClickListener(this);
        if (mSplitButton != null) mSplitButton.setOnClickListener(this);
        
        // Load and display recent tasks
        loadRecentTasks();
    }
    
    /**
     * Load recent tasks from ActivityManager
     */
    private void loadRecentTasks() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (am != null) {
            try {
                List<ActivityManager.RecentTaskInfo> tasks = 
                    am.getRecentTasks(20, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
                
                // TODO: Populate RecentsView with tasks
                // For now, we'll just log the count
                System.out.println("Loaded " + tasks.size() + " recent tasks");
            } catch (SecurityException e) {
                Toast.makeText(this, "Permission denied: Cannot access recent tasks", 
                              Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    public void onClick(View v) {
        int id = v.getId();
        
        if (id == R.id.action_screenshot) {
            Toast.makeText(this, "Screenshot action", Toast.LENGTH_SHORT).show();
            // TODO: Implement screenshot functionality
        } else if (id == R.id.action_select) {
            Toast.makeText(this, "Select action", Toast.LENGTH_SHORT).show();
            // TODO: Implement select functionality
        } else if (id == R.id.action_split) {
            Toast.makeText(this, "Split screen action", Toast.LENGTH_SHORT).show();
            // TODO: Implement split screen functionality
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh tasks when activity resumes
        loadRecentTasks();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
