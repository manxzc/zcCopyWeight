package cn.ymade.module_home.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ymade.module_home.R
import cn.ymade.module_home.adapter.PrintBluetoothAdapter
import cn.ymade.module_home.databinding.ActivityPrintBinding
import cn.ymade.module_home.vm.VMPrint
import com.dothantech.printer.IDzPrinter.PrinterAddress
import com.permissionx.guolindev.PermissionX
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.zcxie.zc.model_comm.base.BaseActivity
import com.zcxie.zc.model_comm.callbacks.CallBack
import com.zcxie.zc.model_comm.util.CommUtil
import com.zcxie.zc.model_comm.util.LiveDataBus
import cn.ymade.module_home.utils.PrintCenterManager

/**
 * @author zc.xie
 * @date 2021/6/15 0015.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class PrintActivity :BaseActivity<VMPrint,ActivityPrintBinding>() {
    override fun getLayoutId(): Int {
        return  R.layout.activity_print
    }
    val REQUEST_CODE_BLUETOOTH = 99
    var pairedPrinters = mutableListOf<PrinterAddress>()

     var    adapter =  PrintBluetoothAdapter(pairedPrinters,object :CallBack<PrinterAddress>{
        override fun callBack(data: PrinterAddress?) {
            PrintCenterManager.getInstance().connectingPrint(data, object : CallBack<Any>{
               override fun callBack(obj: Any?) {
                    mBinding!!.currentPrint.setText(data!!.shownName + " " + "连接中…")
                    showProgress("打印机连接中")
                }
            })
        }
     })

    override fun processLogic() {
      setTopTitle("蓝牙")
        mBinding!!.printMationRv.setLayoutManager(LinearLayoutManager(this))
        mBinding!!.printMationRv.setAdapter(adapter)
        if (PrintCenterManager.getInstance().isPrinterConnected()) {
            mBinding!!.currentPrint.setText(
                PrintCenterManager.getInstance().getCurrentPrint().shownName
            )
        } else {
            mBinding!!.currentPrint.setText("无")
        }

        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    initBlueTooth()
                } else {
                    CommUtil.ToastU.showToast(
                        """
                你已拒绝核心权限权限，
                请确认授权或前往手机权限管理手动授权
                """.trimIndent()
                    )
                    finish()
                }
            }

        mBinding!!.smartRefresh.setOnRefreshListener(OnRefreshListener { refreshLayout ->
            refreshLayout.finishRefresh()
            initBlueTooth()
        })
        LiveDataBus.get().with(
            "printConnect",
            Boolean::class.java
        ).observe(this, object : Observer<Boolean> {
            override fun onChanged(aBoolean: Boolean) {
                hideProgress()
                if (aBoolean) {
                    mBinding!!.currentPrint.setText(
                        PrintCenterManager.getInstance().getCurrentPrint().shownName
                    )
                    CommUtil.ToastU.showToast("打印机连接成功")
                } else {
                    CommUtil.ToastU.showToast("打印机连接失败,请确认设备是否打开")
                }
            }
        })
    }
    private fun initBlueTooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            CommUtil.ToastU.showToast("当前设备不支持蓝牙功能！")
            return
        }
        if (!bluetoothAdapter.isEnabled) {
            openBlueTooth()
        } else {
            pairedPrinters.clear()
            pairedPrinters.addAll(PrintCenterManager.getInstance().getAllPrint())
            for (pn in pairedPrinters ){
                Log.i(TAG, "initBlueTooth:pn  "+pn.shownName)
            }
            adapter!!.notifyDataSetChanged()
            if (pairedPrinters.size < 1) {
                mBinding!!.printEmpty.setVisibility(View.VISIBLE)
            } else {
                mBinding!!.printEmpty.setVisibility(View.GONE)
            }
        }
    }

    private fun openBlueTooth() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, REQUEST_CODE_BLUETOOTH)
    }
    override fun findViewModelClass(): Class<VMPrint> {
        return VMPrint::class.java
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            initBlueTooth()
        } else {
            NiceDialog.init().setLayoutId(R.layout.dialog_chose_promapt)
                .setConvertListener(object : ViewConvertListener() {
                    override fun convertView(holder: ViewHolder, dialog: BaseNiceDialog) {
                        holder.setText(R.id.dialog_tittle_tv, "温馨提示")
                        holder.setText(
                            R.id.dialog_content_tv,
                            "要连接蓝牙打印机，请打开蓝牙并保持蓝牙处于连接状态。关闭蓝牙将无法使用打印功能"
                        )
                        holder.getView<View>(R.id.dialog_promapt_cancle)
                            .setOnClickListener { dialog.dismiss() }
                        holder.getView<View>(R.id.dialog_promapt_ack).setOnClickListener {
                            dialog.dismiss()
                            openBlueTooth()
                        }
                    }
                }).setMargin(60)
                .setOutCancel(false)
                .show(supportFragmentManager)
        }
    }
}