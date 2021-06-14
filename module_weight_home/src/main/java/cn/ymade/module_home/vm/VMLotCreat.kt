package cn.ymade.module_home.vm

import android.content.Intent
import android.util.Log
import cn.ymade.module_home.db.beans.LotDataBean
import cn.ymade.module_home.db.database.DataBaseManager
import cn.ymade.module_home.ui.LotCreateActivity
import cn.ymade.module_home.ui.LotInfoActivity
import com.zcxie.zc.model_comm.base.BaseViewModel
import com.zcxie.zc.model_comm.util.LiveDataBus

/**
 * @author zc.xie
 * @date 2021/6/11 0011.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class VMLotCreat :BaseViewModel() {

    fun cretaLot(act:LotCreateActivity,client:String,exp:String,user:String,time:Long){
        Thread{
            var lotDataBean=LotDataBean()
            lotDataBean.lotId=System.currentTimeMillis()
            lotDataBean.exp=exp
            lotDataBean.items=0
            lotDataBean.lotName=client
            lotDataBean.user=user
            lotDataBean.createTime=time
            lotDataBean.lotNo=""
            Log.i(TAG, "cretaLot: getNum createTime "+time)
            DataBaseManager.db.lotDao().insertAll(lotDataBean)
            Log.i(TAG, "cretaLot: "+  DataBaseManager.db.lotDao().getAll().size)
            LiveDataBus.get().with("createLot").postValue(1)
            act.startActivity(Intent(act,LotInfoActivity::class.java).putExtra("lotdata",lotDataBean))
            act.finish()
        }.start()
    }
}