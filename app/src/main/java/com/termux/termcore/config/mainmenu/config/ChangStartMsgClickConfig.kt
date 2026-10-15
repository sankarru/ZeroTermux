package com.termux.termcore.config.mainmenu.config

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import com.example.xh_lib.utils.UUtils
import com.termux.R
import com.termux.app.TermuxActivity
import com.termux.termcore.activity.EditTextActivity
import com.termux.termcore.code.CodeString
import com.termux.termcore.url.FileUrl
import java.io.File

class ChangStartMsgClickConfig: BaseMenuClickConfig() {
    override fun getIcon(context: Context?): Drawable? {
        return context?.getDrawable(R.mipmap.start_msg_ico)
    }

    override fun getString(context: Context?): String? {
        return context?.getString(R.string.start_msg)
    }

    override fun onClick(view: View?, context: Context?) {
        val intent = Intent(mContext, EditTextActivity::class.java)
        intent.putExtra("edit_path", FileUrl.smsMotdFile)
        mContext?.startActivity(intent)
    }
}
