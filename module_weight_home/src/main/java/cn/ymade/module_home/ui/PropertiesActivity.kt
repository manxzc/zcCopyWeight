package cn.ymade.module_home.ui

import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ymade.module_home.R
import cn.ymade.module_home.adapter.RuleAdapter
import cn.ymade.module_home.databinding.ActivityPropertiesBinding
import cn.ymade.module_home.model.RuleData
import cn.ymade.module_home.net.DeviceInfoApi
import cn.ymade.module_home.vm.VMProperties
import com.google.gson.Gson
import com.zcxie.zc.model_comm.base.BaseActivity
import com.zcxie.zc.model_comm.net.RetrofitManager
import com.zcxie.zc.model_comm.util.AppConfig
import com.zcxie.zc.model_comm.util.CommUtil
import kotlinx.android.synthetic.main.activity_properties.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PropertiesActivity :BaseActivity<VMProperties,ActivityPropertiesBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_properties
    }
    var ruleData:RuleData?=null
    var adapter:RuleAdapter?=null
    override fun processLogic() {
        setTopTitle("配置规则")
        mBinding?.let {
            it.swItem.isChecked=AppConfig.swItem.get()
            it.swLot.isChecked=AppConfig.swLot.get()
            it.sw3102.isChecked=AppConfig.sw3102.get()
            it.sw3100.isChecked=!AppConfig.sw3102.get()
            it.swCreate.isChecked=AppConfig.swCreate.get()
            it.swPackage.isChecked=AppConfig.swPackage.get()
            it.swEnd.isChecked=AppConfig.swEnd.get()

            it.swItem.setOnCheckedChangeListener {
                    _, isChecked ->  AppConfig.swItem.put(isChecked)
            }
            it.swLot.setOnCheckedChangeListener {
                    _, isChecked ->  AppConfig.swLot.put(isChecked)
            }
            it.sw3102.setOnCheckedChangeListener {
                    _, isChecked ->
                run {
                    AppConfig.sw3102.put(isChecked)
                    mBinding!!.sw3100.isChecked = !isChecked
                }
            }
            it.sw3100.setOnCheckedChangeListener {
                    _, isChecked ->
                run {
                    AppConfig.sw3102.put(!isChecked)
                    mBinding!!.sw3102.isChecked = !isChecked
                }
            }
            it.swCreate.setOnCheckedChangeListener {
                    _, isChecked ->  AppConfig.swCreate.put(isChecked)
            }
            it.swPackage.setOnCheckedChangeListener {
                    _, isChecked ->  AppConfig.swPackage.put(isChecked)
            }
            it.swEnd.setOnCheckedChangeListener {
                    _, isChecked ->  AppConfig.swEnd.put(isChecked)
            }

            it.sync.setOnClickListener {
                getRule()
            }
            it.rvRule.layoutManager=LinearLayoutManager(this)
            var jsonStr=AppConfig.ruleStr.get()
            if (!TextUtils.isEmpty(jsonStr)){
                ruleData=Gson().fromJson(jsonStr,RuleData::class.java)
                if (ruleData!=null)
                adapter=RuleAdapter(ruleData!!)
                mBinding?.rvRule!!.adapter=adapter
            }else{
                getRule()
            }


        }
    }

    private fun getRule() {
        RetrofitManager.retrofit
            .create(DeviceInfoApi::class.java)
            .getRule(AppConfig.Token.get())
            .enqueue(object : Callback<RuleData> {
                override fun onResponse(call: Call<RuleData>, response: Response<RuleData>) {
                    Log.i(
                        TAG,
                        "onResponse: rule response " + response.isSuccessful + " " + response.body()
                    )
                    if (response.isSuccessful && response.body() != null) {
                        var data = Gson().toJson(response.body())
                        Log.i(TAG, "onResponse: data " + data + " \n " + response.toString())
                        AppConfig.ruleStr.put(data)
                        ruleData=response.body()
                        adapter=RuleAdapter(ruleData!!)
                        mBinding?.rvRule!!.adapter=adapter
                    }
                }

                override fun onFailure(call: Call<RuleData>, t: Throwable) {
                    Log.i(TAG, "onFailure: " + t.message)
                    CommUtil.ToastU.showToast("失败")
                }

            })
    }

    override fun findViewModelClass(): Class<VMProperties> {
        return VMProperties::class.java
    }
}