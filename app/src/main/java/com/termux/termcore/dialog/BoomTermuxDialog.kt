package com.termux.termcore.dialog

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blockchain.ub.util.custom.dialog.BaseDialogDown
import com.termux.R
import com.termux.termcore.bean.RunCommandBean
import com.termux.termcore.data.LinuxCommandData
import com.termux.termcore.dialog.adapter.BoomTermuxAdapter
import com.termux.termcore.popuwindow.NginxPopuWindow
import com.termux.termcore.popuwindow.WebStartPopuWindow

class BoomTermuxDialog : BaseDialogDown {

    private var recycler_view:RecyclerView? = null


    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    override fun initViewDialog(mView: View) {

        recycler_view = mView.findViewById(R.id.recycler_view)



        recycler_view!!.layoutManager = GridLayoutManager(mContext,4)

        var mList:ArrayList<RunCommandBean> = LinuxCommandData.getRunCommandBeanData()


        val boomTermuxAdapter = BoomTermuxAdapter(mList, mContext as Activity)

        boomTermuxAdapter.setDissListener(object : BoomTermuxAdapter.DissListener{
            override fun close() {
                dismiss()
            }
        })

        recycler_view!!.adapter = boomTermuxAdapter
    }

    override fun getContentView(): Int {

        return R.layout.dialog_termux
    }


}
