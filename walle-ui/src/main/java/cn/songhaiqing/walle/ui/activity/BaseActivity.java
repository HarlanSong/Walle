package cn.songhaiqing.walle.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.songhaiqing.walle.ui.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    protected void initTitle(final BaseActivity activity, String title, Integer optionImage) {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        View titleBar = LayoutInflater.from(this).inflate(R.layout.walle_ui_view_title_bar, null, false);
        View viewStatus = titleBar.findViewById(R.id.view_status);
        viewStatus.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statusBarHeight));
        LinearLayout linearLayout =  (LinearLayout)((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        linearLayout.addView(titleBar, 0);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            viewStatus.setVisibility(View.GONE);
        }
        if(activity  != null){
            findViewById(R.id.ib_title_bar_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        }else{
            findViewById(R.id.ib_title_bar_back).setVisibility(View.GONE);
        }
        ((TextView) findViewById(R.id.tv_title_bar_title)).setText(title);
        ImageButton ibOption = findViewById(R.id.ib_option);
        if (optionImage != null) {
            ibOption.setVisibility(View.VISIBLE);
            Drawable drawable = ContextCompat.getDrawable(getBaseContext(), optionImage);
            ibOption.setImageDrawable(drawable);
        } else {
            ibOption.setVisibility(View.GONE);
        }
    }
}
