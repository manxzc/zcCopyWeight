package cn.ymade.module_home.ui

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ActivityGoodsListBinding
import cn.ymade.module_home.vm.VMGoodsList
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.zcxie.zc.model_comm.base.BaseActivity
import com.zcxie.zc.model_comm.callbacks.CallBack
import com.zcxie.zc.model_comm.util.CommUtil

/**
 * @author zc.xie
 * @date 2021/6/10 0010.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class GoodsListActivity :BaseActivity<VMGoodsList,ActivityGoodsListBinding>() {
    var selectGoodsCategory=false
    override fun getLayoutId(): Int {
        return R.layout.activity_goods_list
    }

    override fun processLogic() {
        selectGoodsCategory= intent.getIntExtra("selectClient",0)==1
        initTopSearchBar()
        initBtmOnlyMind("创建货品")
        registItemMenuDeleteListener(mBinding!!.rvGoodsList,object :CallBack<Int>{
            override fun callBack(data: Int) {
                mViewModel!!.deleteItem(data)
            }

        })
        mViewModel!!.init(this,mBinding!!.rvGoodsList,selectGoodsCategory)
    }

    override fun findViewModelClass(): Class<VMGoodsList> {
        return VMGoodsList::class.java
    }

    override fun onSearchAction(s: String) {
        super.onSearchAction(s)
        mViewModel!!.searchData(s)
    }

    override fun clickOnlyMind() {
        super.clickOnlyMind()

        createDialog("","")
    }
    fun reloadTitle(num: Int){
        mBinding!!.tvTopNum.setText("数量 ：$num")
    }
    private var niceDialog: NiceDialog? = null

    fun createDialog(no:String,name:String) {
        var oldNo=no;
        niceDialog = NiceDialog.init().setLayoutId(R.layout.dialog_create)
        niceDialog?.setConvertListener(object : ViewConvertListener() {
            override fun convertView(holder: ViewHolder, dialog: BaseNiceDialog) {
                holder.getView<TextView>(R.id.tv_p1).text="货号"
                holder.getView<TextView>(R.id.tv_p2).text="名称"
                holder.getView<EditText>(R.id.ed_p1).setText(no+"")
                holder.getView<EditText>(R.id.ed_p1).inputType=EditorInfo.TYPE_CLASS_NUMBER
                holder.getView<EditText>(R.id.ed_p2).setText(name+"")
                holder.getView<View>(R.id.dialog_promapt_cancle).setOnClickListener {
                    niceDialog?.dismiss()
                }
                holder.getView<View>(R.id.dialog_promapt_ack).setOnClickListener {

                    var etNo=holder.getView<EditText>(R.id.ed_p1).text.toString()
                    var etName=holder.getView<EditText>(R.id.ed_p2).text.toString()
                    if (etNo.isNullOrEmpty()||etName.isNullOrEmpty()){
                        CommUtil.ToastU.showToast("请输入完整信息~！")
                        return@setOnClickListener
                    }
                    mViewModel!!.createGooodsCategory(etNo,etName,oldNo)
                    niceDialog?.dismiss()
                }
            }
        })?.setMargin(60)
            ?.show(supportFragmentManager)
    }
}