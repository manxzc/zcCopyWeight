package cn.ymade.module_home.ui

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.EditText
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ActivitySettingBinding
import cn.ymade.module_home.db.beans.DevInfoBean
import cn.ymade.module_home.vm.VMSetting
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.zcxie.zc.model_comm.base.BaseActivity
import com.zcxie.zc.model_comm.util.CommUtil
import cn.ymade.module_home.utils.PrintCenterManager

/**
 * @author zc.xie
 * @date 2021/6/1 0001.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class SettingsActivity :BaseActivity<VMSetting,ActivitySettingBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun processLogic() {
        setTopTitle("设备信息")

        mViewModel!!.init(this)

        mBinding!!.toPrint.setOnClickListener {
            startActivity(Intent(this, PrintActivity::class.java) )
        }
    }

    fun loadDevInfo(dev: DevInfoBean){
        Log.i(TAG, "loadDevInfo: "+dev)
//        dev?.let {
            mBinding!!.tvCompany.text=dev.Company
            mBinding!!.tvCompanySN.text=dev.CompanySN
            mBinding!!.tvDevice.text=dev.Device
            mBinding!!.tvDeviceSN.text=dev.DeviceSN
            mBinding!!.tvExpiryDate.text=dev.ExpiryDate
            mBinding!!.tvRegDate.text=dev.RegDate
            mBinding!!.tvSN.text=dev.SN
                mBinding!!.versiontip.visibility=if (CommUtil.getPackageName()!=dev?.Version) View.VISIBLE else View.GONE

                mBinding!!.btnLogout.setOnClickListener {
                    createLoginOutDialog()
                }
                mBinding!!.deviceName.setOnClickListener{
                    createChangeName(mViewModel!!.getUserInfo()!!.Device!!)
                }


    }

    override fun onResume() {
        super.onResume()
        mBinding!!.tvStatus.text= if (PrintCenterManager.getInstance()
                .isPrinterConnected()
        ) PrintCenterManager.getInstance().getCurrentPrint().shownName else "未连接"

    }
    override fun findViewModelClass(): Class<VMSetting> {
        return VMSetting::class.java
    }
    private var niceDialog: NiceDialog? = null
    fun createLoginOutDialog() {
        niceDialog = NiceDialog.init().setLayoutId(R.layout.dialog_chose_promapt)
        niceDialog?.setConvertListener(object : ViewConvertListener() {
            override fun convertView(holder: ViewHolder, dialog: BaseNiceDialog) {
                holder.setText(R.id.dialog_tittle_tv, "设备注销")
                holder.setText(R.id.dialog_content_tv, "确定注销当前设备？ ")
                holder.getView<View>(R.id.dialog_promapt_cancle)
                    .setOnClickListener { niceDialog?.dismiss() }
                holder.getView<View>(R.id.dialog_promapt_ack).setOnClickListener {
                    niceDialog?.dismiss()
                    mViewModel?.loginOut()
                }
            }
        })?.setMargin(60)
            ?.show(supportFragmentManager)
    }
    private var changName: NiceDialog? = null
    fun createChangeName(name:String) {
        changName = NiceDialog.init().setLayoutId(R.layout.dialog_changename)
        changName?.setConvertListener(object : ViewConvertListener() {
            override fun convertView(holder: ViewHolder, dialog: BaseNiceDialog) {
                holder.getView<EditText>(R.id.ed_p1).setText(name+"")
                holder.getView<View>(R.id.dialog_promapt_cancle)
                    .setOnClickListener { changName?.dismiss() }
                holder.getView<View>(R.id.dialog_promapt_ack).setOnClickListener {
                    changName?.dismiss()
                    var nameChanged=holder.getView<EditText>(R.id.ed_p1).text.toString()
                    mBinding!!.tvDevice.text=nameChanged
                    mViewModel!!.changeName(nameChanged)
                }
            }
        })?.setMargin(60)
            ?.show(supportFragmentManager)
    }
}