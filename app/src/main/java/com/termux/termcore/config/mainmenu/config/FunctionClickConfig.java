package com.termux.termcore.config.mainmenu.config;

import static com.termux.termcore.config.mainmenu.MainMenuConfig.CODE_ZT_FEATURES;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.termux.R;
import com.termux.termcore.dialog.BoomTermuxDialog;

public class FunctionClickConfig extends BaseMenuClickConfig {
    @Override
    public int getType() {
        return CODE_ZT_FEATURES;
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getDrawable(R.mipmap.term_fun_ico);
    }

    @Override
    public String getString(Context context) {
        return context.getString(R.string.Termux功能);
    }

    @Override
    public void onClick(View view, Context context) {
        BoomTermuxDialog boomTermuxDialog = new BoomTermuxDialog(context);
        boomTermuxDialog.show();
        boomTermuxDialog.setCancelable(true);
    }
}
