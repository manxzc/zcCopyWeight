package cn.ymade.module_home.ui

import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ActivityClientBinding
import cn.ymade.module_home.vm.VMClient
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.zcxie.zc.model_comm.base.BaseActivity
import com.zcxie.zc.model_comm.callbacks.CallBack
import com.zcxie.zc.model_comm.util.CommUtil

class ClientActivity :BaseActivity<VMClient,ActivityClientBinding>() {
    var selectClient=false
    override fun getLayoutId(): Int {
        return R.layout.activity_client
    }

    override fun processLogic() {
        initBtmOnlyMind("新建客户")
        initTopSearchBar()
        selectClient= intent.getIntExtra("selectClient",0)==1
        registItemMenuDeleteListener(mBinding!!.rvClient,object :CallBack<Int>{
            override fun callBack(data: Int) {
                mViewModel!!.delete(data)
            }
        })
        mViewModel!!.initData(this,mBinding!!.rvClient,selectClient)
    }

    override fun findViewModelClass(): Class<VMClient> {
        return VMClient::class.java
    }
    override fun clickOnlyMind() {
        super.clickOnlyMind()
        Log.i(TAG, "clickOnlyMind: 创建新的")
        createDialog("","")
    }


    override fun onSearchAction(s: String) {
        super.onSearchAction(s)
       mViewModel!!.search(s)
    }
    fun reloadTitle(num: Int){
        mBinding!!.tvTopNum.text = "数量 ：$num"
    }
    private var niceDialog: NiceDialog? = null
    fun createDialog(name:String,phone:String) {
        var oldName=name;
        niceDialog = NiceDialog.init().setLayoutId(R.layout.dialog_create)
        niceDialog?.setConvertListener(object : ViewConvertListener() {
            override fun convertView(holder: ViewHolder, dialog: BaseNiceDialog) {

                holder.getView<EditText>(R.id.ed_p1).setText(name+"")
                holder.getView<EditText>(R.id.ed_p2).inputType= EditorInfo.TYPE_CLASS_PHONE
                holder.getView<EditText>(R.id.ed_p2).setText(phone+"")

                holder.getView<View>(R.id.dialog_promapt_cancle).setOnClickListener {
                    niceDialog?.dismiss()
                }
                holder.getView<View>(R.id.dialog_promapt_ack).setOnClickListener {

                    var name=holder.getView<EditText>(R.id.ed_p1).text.toString()
                    var phone=holder.getView<EditText>(R.id.ed_p2).text.toString()
                    if (name.isNullOrEmpty()){
                        CommUtil.ToastU.showToast("请输入完整信息~！")
                        return@setOnClickListener
                    }
                    mViewModel!!.createClient(name,phone,oldName)
                    niceDialog?.dismiss()
                }
            }
        })?.setMargin(60)
                ?.show(supportFragmentManager)
    }
}