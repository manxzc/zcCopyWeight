package cn.ymade.module_home.vm

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.ymade.module_home.common.Constant
import cn.ymade.module_home.db.beans.DevInfoBean
import cn.ymade.module_home.db.database.DataBaseManager
import cn.ymade.module_home.model.Device
import cn.ymade.module_home.model.DeviceInfo
import cn.ymade.module_home.model.Version
import cn.ymade.module_home.net.DeviceInfoApi
import cn.ymade.module_home.ui.PrintActivity
import cn.ymade.module_home.ui.SettingsActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.zcxie.zc.model_comm.base.BaseApplication
import com.zcxie.zc.model_comm.base.BaseViewModel
import com.zcxie.zc.model_comm.net.RetrofitManager
import com.zcxie.zc.model_comm.util.AppConfig
import com.zcxie.zc.model_comm.util.CommUtil
import com.zcxie.zc.model_comm.util.LiveDataBus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author zc.xie
 * @date 2021/6/1 0001.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class VMSetting :BaseViewModel() {

    fun getUserInfo(): DevInfoBean? {
        return userInfo
    }


    private var userInfo:DevInfoBean? =null

    fun changeName(name:String){
        if (name==userInfo!!.Device)
            return

        Thread{
            userInfo!!.Device=name
            DataBaseManager.db.devinfoDao().insertAll(userInfo!!)
            LiveDataBus.get().with(Constant.LD_UP_HOME_TITLE).postValue(3)
             getSerDeviceINfo()
        }.start()
    }
    fun loginOut(){
        AppConfig.clearAll()
        DataBaseManager.closeAll()
        BaseApplication.getInstance()?.closeAllAct()
        ARouter.getInstance().build("/login/loginActivity").withString("dvIds",userInfo?.SN).navigation()
    }

    fun getSerDeviceINfo(){
        if (AppConfig.hasDevInfo.get()){
            Observable.create<DevInfoBean> {
                it.onNext(DataBaseManager.db.devinfoDao().getAll()[0])
            }.subscribeOn(Schedulers.io())
                .subscribe(object : Observer<DevInfoBean> {
                    override fun onNext(t: DevInfoBean?) {
//                        t?.let {
                            Log.i(TAG, "onNext: "+t.toString())
                            act!!.loadDevInfo(t!!)
                            userInfo=t

//                        }
                    }

                    override fun onError(e: Throwable?) {
                    }

                    override fun onSubscribe(d: Disposable?) {
                    }

                    override fun onComplete() {
                    }

                })
        }else{
            RetrofitManager.retrofit
                .create(DeviceInfoApi::class.java)
                .queryDvInfo(AppConfig.Token.get())
                .enqueue(object : Callback<DeviceInfo> {
                    override fun onResponse(call: Call<DeviceInfo>, response: Response<DeviceInfo>) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                if (it.code == 1) {
                                    if (it.Device.size > 0) {
                                        val dev: Device = it.Device[0]
                                        val ver: Version = it.Version[0]
                                        var dbBean = DevInfoBean(
                                            dev.UUID,
                                            dev.UUID,
                                            dev.Company,
                                            dev.CompanySN,
                                            dev.Device,
                                            dev.DeviceSN,
                                            dev.ExpiryDate,
                                            dev.RegDate,
                                            dev.SN,
                                            ver.Renew,
                                            ver.Url,
                                            ver.Version
                                        )
                                        act!!.loadDevInfo(dbBean)
                                        userInfo=dbBean
                                        Thread {
                                            DataBaseManager.db.devinfoDao().insertAll(dbBean)
                                            AppConfig.hasDevInfo.put(true)
                                            Log.i(
                                                TAG,
                                                "getDeviceINfo: " + DataBaseManager.db.devinfoDao()
                                                    .getAll().size
                                            )
                                        }.start()

                                    }
                                }

                            }
                        }

                        Log.i(TAG, "onResponse:getDeviceINfo  isSuccessful "+response.isSuccessful+" company "+ response.body()?.Device?.get(0)?.Company)
                    }

                    override fun onFailure(call: Call<DeviceInfo>, t: Throwable) {
                        Log.i(TAG, "getDeviceINfo onFailure: ")
                        CommUtil.ToastU.showToast("获取设备信息失败")
                    }

                })
        }
    }
    var act:SettingsActivity?=null
    fun init(act:SettingsActivity){
        this.act=act
        getSerDeviceINfo()
    }
    private val prnitStatus = MutableLiveData<String>()
    fun getPrnitStatus(): MutableLiveData<String> {
        return prnitStatus
    }
    fun toPrintPage() {
        act!!.startActivity(Intent(act!!, PrintActivity::class.java) )
    }
}