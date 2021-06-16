package cn.ymade.module_home.ui

import android.Manifest
import android.util.Log
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ActivityExportBinding
import cn.ymade.module_home.vm.VMExportExcel
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.permissionx.guolindev.PermissionX
import com.zcxie.zc.model_comm.base.BaseActivity
import com.zcxie.zc.model_comm.callbacks.CallBack
import com.zcxie.zc.model_comm.util.CommUtil
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ExportActivity :BaseActivity<VMExportExcel, ActivityExportBinding>() {

    override fun getLayoutId(): Int {
        return R.layout.activity_export
    }

    override fun processLogic() {
        setTopTitle("导出")
        initBtmOnlyMind("制作excel")
        mViewModel!!.init(this)
        updateTime()
        initEvent()
        PermissionX.init(this)
            .permissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            .request { allGranted, _, _ ->
                if (allGranted) {
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
    }

    private fun initEvent() {
        mBinding!!.tvStartTime.setOnClickListener { //时间选择器
            val pvTime = TimePickerBuilder(this) { date, v -> mBinding!!.tvStartTime.text = CommUtil.getTime(
                date
            ) }.build()
            val calendar = Calendar.getInstance()
            val ymd = mBinding!!.tvStartTime.text.toString()
            calendar.time = CommUtil.ConverToDateDay(ymd)
            pvTime.setDate(calendar) //注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
            pvTime.show()
        }
        mBinding!!.tvStopTime.setOnClickListener { //时间选择器
            val pvTime = TimePickerBuilder(this) { date, v -> mBinding!!.tvStopTime.text = CommUtil.getTime(
                date
            ) }.build()
            pvTime.setDate(Calendar.getInstance()) //注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
            pvTime.show()
        }
        mBinding!!.btnSummarySelect.setOnClickListener { loading() }
    }
    //更新时间
    private fun updateTime() {
        val now = Calendar.getInstance()
        now.add(Calendar.DAY_OF_MONTH, -30)
        val startDate = SimpleDateFormat("yyyy-MM-dd").format(now.time)
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val nowdayTime = dateFormat.format(Date())
        mBinding!!.tvStartTime.text = startDate
        mBinding!!.tvStopTime.text = nowdayTime
    }
    override fun findViewModelClass(): Class<VMExportExcel> {
        return VMExportExcel::class.java
    }

    override fun onSearchAction(s: String) {
        super.onSearchAction(s)
        loading()
    }

    private fun loading() {
        showProgress("查询中..")
        val start = mBinding!!.tvStartTime.text.toString()
        val stop = mBinding!!.tvStopTime.text.toString()
        Log.i("TAG", "loading:getNum start start " + start + " stop " + stop + getTopSearchText())
        mViewModel!!.getNum(getTopSearchText(), start, stop,
            CallBack { data ->
                hideProgress()
                if (data == null) return@CallBack
                mBinding!!.tvGoodsSum.text = "" + data.goodsSum
                mBinding!!.tvLotCount.text = "" + data.lotCount
            })
    }

    override fun clickOnlyMind() {
        super.clickOnlyMind()

        mViewModel!!.export()
    }
}