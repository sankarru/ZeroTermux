package com.termux.termcore.dialog.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xh_lib.utils.UUtils
import com.termux.R
import com.termux.app.TermuxActivity
import com.termux.termcore.bean.RunCommandBean
import com.termux.termcore.dialog.LoadingDialog
import com.termux.termcore.dialog.view_holder.BoomTermuxViewHolder
import com.termux.termcore.url.FileUrl.mainHomeUrl
import java.io.File

class BoomTermuxAdapter : RecyclerView.Adapter<BoomTermuxViewHolder> {

    private var mList:ArrayList<RunCommandBean>? = null

    private var mActivity:Activity? = null


    constructor(mList:ArrayList<RunCommandBean>?,mActivity:Activity) : super(){

        this.mList = mList
        this.mActivity = mActivity

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoomTermuxViewHolder {
       return BoomTermuxViewHolder(UUtils.getViewLayViewGroup(R.layout.item_boom_termux,parent))
    }

    override fun onBindViewHolder(holder: BoomTermuxViewHolder, position: Int) {

        val runCommandBean = mList!![position]

        if(runCommandBean.type == 0){

            if(runCommandBean.isShow){
                //标题
                holder.msg_card!!.visibility = View.GONE
                holder.title!!.visibility = View.VISIBLE
                holder.title!!.text = runCommandBean.title
            }else{

                holder.msg_card!!.visibility = View.GONE
                holder.title!!.visibility = View.GONE
            }


        }else{
            //内容

            holder.msg_card!!.visibility = View.VISIBLE
            holder.title!!.visibility = View.GONE
            holder.msg!!.text = runCommandBean.name

            holder.msg_card!!.setOnClickListener {

                mDissListener?.close()

                if(runCommandBean.isHttpCommand){

                    com.termux.termcore.utils.SingletonCommunicationUtils.getInstance().getmSingletonCommunicationListener().sendTextToTerminal(runCommandBean.runCommand)

                }else{

                    val loadingDialog = LoadingDialog(mActivity!!)
                    loadingDialog.show()

                    UUtils.runOnThread {


                        UUtils.writerFile(runCommandBean.assetsName, File(mainHomeUrl, "/${runCommandBean.fileName}"))

                        mActivity!!.runOnUiThread {

                            loadingDialog.dismiss()

                            com.termux.termcore.utils.SingletonCommunicationUtils.getInstance().getmSingletonCommunicationListener().sendTextToTerminal(runCommandBean.runCommand)

                            if(runCommandBean.runCommit != null){
                                runCommandBean.runCommit.run()
                            }



                        }

                    }

                }







            }

            holder.msg_card!!.setOnLongClickListener {


                val intent = Intent()
                intent.data = Uri.parse(runCommandBean.address) //Url 就是你要打开的网址
                intent.action = Intent.ACTION_VIEW
                mActivity!!.startActivity(intent) //启动浏览器


                true
            }


        }


    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    private var mDissListener:DissListener? = null

    public fun setDissListener(mDissListener:DissListener){

        this.mDissListener = mDissListener

    }

    public interface DissListener{


        fun close()


    }
}
