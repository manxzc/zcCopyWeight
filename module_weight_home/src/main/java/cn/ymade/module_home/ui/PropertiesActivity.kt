package cn.ymade.module_home.ui

import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ActivityPropertiesBinding
import cn.ymade.module_home.vm.VMProperties
import com.zcxie.zc.model_comm.base.BaseActivity
import com.zcxie.zc.model_comm.util.AppConfig

class PropertiesActivity :BaseActivity<VMProperties,ActivityPropertiesBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_properties
    }

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
        }
    }

    override fun findViewModelClass(): Class<VMProperties> {
        return VMProperties::class.java
    }
}