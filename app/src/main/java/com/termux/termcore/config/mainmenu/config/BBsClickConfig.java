package com.termux.termcore.config.mainmenu.config;

import static com.termux.termcore.config.mainmenu.MainMenuConfig.CODE_ONLINE_FEATURES;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.termux.R;
import com.termux.termcore.activity.WebViewActivity;
import com.termux.termcore.http.HTTPIP;

public class BBsClickConfig extends BaseMenuClickConfig {
    @Override
    public int getType() {
        return CODE_ONLINE_FEATURES;
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getDrawable(R.mipmap.bbs_termux);
    }

    @Override
    public String getString(Context context) {
        return context.getString(R.string.termux_forum);
    }

    @Override
    public void onClick(View view, Context context) {
        Intent intent2 = new Intent(context, WebViewActivity.class);
        intent2.putExtra("title", "Termux 论坛");
        intent2.putExtra("content", HTTPIP.ZERO_BBS);
        context.startActivity(intent2);
    }
}
