package cn.ymade.module_home.ui

import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ActivityGoodsListBinding
import cn.ymade.module_home.vm.VMGoodsList
import com.zcxie.zc.model_comm.base.BaseActivity

/**
 * @author zc.xie
 * @date 2021/6/10 0010.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class GoodsListActivity :BaseActivity<VMGoodsList,ActivityGoodsListBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_goods_list
    }

    override fun processLogic() {
        initTopSearchBar()
        initBtmOnlyMind("创建货品")
        mViewModel!!.init(this,mBinding!!.rvGoodsList)
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

    }
    fun reloadTitle(num: Int){
        mBinding!!.tvTopNum.setText("数量 ：$num")
    }
}