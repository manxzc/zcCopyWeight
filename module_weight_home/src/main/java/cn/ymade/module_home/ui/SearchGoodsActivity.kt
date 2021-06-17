package cn.ymade.module_home.ui

import android.content.Intent
import android.text.TextUtils
import android.view.View
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ActivitySearchGoodsBinding
import cn.ymade.module_home.db.beans.GoodsBean
import cn.ymade.module_home.db.beans.LotDataBean
import cn.ymade.module_home.homebase.ScanBaseActivity
import cn.ymade.module_home.vm.VMSearchGoods
import com.zcxie.zc.model_comm.base.BaseActivity

/**
 * @author zc.xie
 * @date 2021/6/14 0014.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class SearchGoodsActivity :ScanBaseActivity<VMSearchGoods,ActivitySearchGoodsBinding>() {
    override fun getLayoutId(): Int {
      return R.layout.activity_search_goods
    }
    var lotdata:LotDataBean?=null

    override fun processLogic() {
       initTopSearchBar()
        mViewModel!!.init(this)
        mBinding!!.llLot.setOnClickListener{
            lotdata?.let {
                startActivity(Intent(this,LotInfoActivity::class.java).putExtra("lotdata",lotdata))
            }

        }
    }

    override fun findViewModelClass(): Class<VMSearchGoods> {
        return VMSearchGoods::class.java
    }

    override fun loadCoded(scanCode: String) {
       mViewModel!!.search(scanCode)
    }

    override fun onSearchAction(s: String) {
        super.onSearchAction(s)
        mViewModel!!.search(s)
    }

    fun showGoods(data: GoodsBean?){
        if (data==null){
            mBinding!!.llGoodsInfo.visibility=View.GONE
            showLotInfo(null)
            return
        }
        mBinding!!.llGoodsInfo.visibility=View.VISIBLE
        mBinding?.let {
            it.etWeight.setText(data.weight.toString())
            it.tvNo.text=data.GoodsNO
            it.tvLotNumber.text=data.lotNumber
            if (!TextUtils.isEmpty(data.createDate)){
                it.tvDate.text="生产日期"
                it.etDate.setText(data.createDate)
            }else{
                it.tvDate.text="有效期"
                it.etDate.setText(data.shelflife)
            }
        }

    }
    fun showLotInfo(data: LotDataBean?){
        lotdata=data
        mBinding!!.llLot.visibility=if (data!=null)View.VISIBLE else View.GONE
        data?.let {
            mBinding!!.tvClient.text=it.lotName
            mBinding!!.tvExp.text=it.exp
            mBinding!!.tvUser.text=it.user
        }
    }

}