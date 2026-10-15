package com.termux.termcore.config.mainmenu.config

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import com.example.xh_lib.utils.UUtils
import com.termux.R
import com.termux.termcore.utils.PackageMsg

class UnInstallClickConfig: BaseMenuClickConfig() {
    override fun getIcon(context: Context?): Drawable? {
        return context?.getDrawable(R.mipmap.uninstall)
    }

    override fun getString(context: Context?): String? {
        return context?.getString(R.string.term_uninstall)
    }

    override fun onClick(view: View?, context: Context?) {
        PackageMsg.unInstallALLApk(mContext as Activity)
    }
}
