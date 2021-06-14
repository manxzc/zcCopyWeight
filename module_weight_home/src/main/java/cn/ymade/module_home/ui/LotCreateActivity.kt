package cn.ymade.module_home.ui

import android.content.Intent
import androidx.lifecycle.Observer
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ActivityLotCreateBinding
import cn.ymade.module_home.vm.VMLotCreat
import com.zcxie.zc.model_comm.base.BaseActivity
import com.zcxie.zc.model_comm.util.CommUtil
import com.zcxie.zc.model_comm.util.LiveDataBus

/**
 * @author zc.xie
 * @date 2021/6/11 0011.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class LotCreateActivity :BaseActivity<VMLotCreat,ActivityLotCreateBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_lot_create
    }

    var time=System.currentTimeMillis()
    override fun processLogic() {
        setTopTitle("创建单据")
        initBtmOnlyMind("创建单据")
        mBinding!!.tvTime.text = CommUtil.getTimebyStamp(time)
        mBinding!!.flClient.setOnClickListener{

            startActivity(Intent(this,ClientActivity::class.java).putExtra("selectClient",1))
        }
        initEvent()
    }

    private fun initEvent() {
        LiveDataBus.get().with("selectedClient",String::class.java).observe(this,object :Observer<String>{
            override fun onChanged(t: String) {
                mBinding!!.tvClient.text=t
            }

        })
    }

    override fun findViewModelClass(): Class<VMLotCreat> {
        return VMLotCreat::class.java
    }

    override fun clickOnlyMind() {
        super.clickOnlyMind()
        var client=mBinding!!.tvClient.text.toString()
        var exp=mBinding!!.etExp.text.toString()
        var user=mBinding!!.etUser.text.toString()
//        var time= mBinding!!.tvTime.text.toString()
//                ||exp.isNullOrEmpty()
        if (client.isNullOrEmpty()||user.isNullOrEmpty()){
            CommUtil.ToastU.showToast("请完善信息")
            return
        }
        mViewModel!!.cretaLot(this,client,exp,user,time)
    }
}