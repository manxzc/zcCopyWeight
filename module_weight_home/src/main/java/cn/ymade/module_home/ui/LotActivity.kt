package cn.ymade.module_home.ui

import android.content.Intent
import android.util.Log
import androidx.lifecycle.Observer
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ActivityLotBinding
import cn.ymade.module_home.vm.VMLot
import com.zcxie.zc.model_comm.base.BaseActivity
import com.zcxie.zc.model_comm.util.LiveDataBus

/**
 * @author zc.xie
 * @date 2021/6/6 0006.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class LotActivity : BaseActivity<VMLot,ActivityLotBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_lot
    }

    override fun processLogic() {
        initTopSearchBar()
        initBtmOnlyMind("新建单据")
        mBinding?.let { binding->
            binding.mViewPager.adapter= mViewModel?.initFragment(this)
            binding.mViewPager.offscreenPageLimit=3
            binding.mTabLayout.setupWithViewPager(binding.mViewPager)
        }
        initEnven()
    }

    private fun initEnven() {
        LiveDataBus.get().with("createLot",Int::class.java).observe(this,object :Observer<Int>{
            override fun onChanged(t: Int?) {
                LiveDataBus.get().with("reloadLot").postValue(1)
            }
        })
    }

    override fun clickOnlyMind() {
        super.clickOnlyMind()
        Log.i(TAG, "clickOnlyMind: 创建新的")
       startActivity(Intent(this,LotCreateActivity::class.java))
    }
    override fun findViewModelClass(): Class<VMLot> {
        return VMLot::class.java
    }

    override fun onSearchAction(s: String) {
        super.onSearchAction(s)
        LiveDataBus.get().with("searchLot").postValue(s)
    }
    fun refreshTab(index: Int, count: String?) {
        mBinding?.mTabLayout!!.getTabAt(index)?.setText(count)
    }
}