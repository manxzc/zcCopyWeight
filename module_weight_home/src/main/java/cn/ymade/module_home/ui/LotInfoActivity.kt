package cn.ymade.module_home.ui

import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import cn.ymade.module_home.R
import cn.ymade.module_home.common.Constant
import cn.ymade.module_home.databinding.ActivityLotinfoBinding
import cn.ymade.module_home.db.beans.GoodsBean
import cn.ymade.module_home.db.beans.LotDataBean
import cn.ymade.module_home.db.database.DataBaseManager
import cn.ymade.module_home.homebase.ScanBaseActivity
import cn.ymade.module_home.vm.VMLotInfo
import com.zcxie.zc.model_comm.util.CommUtil
import com.zcxie.zc.model_comm.util.LiveDataBus
import cn.ymade.module_home.utils.PrintCenterManager
import kotlinx.android.synthetic.main.activity_lotinfo.*

/**
 * @author zc.xie
 * @date 2021/6/12 0012.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class LotInfoActivity :ScanBaseActivity<VMLotInfo,ActivityLotinfoBinding> (){
    var enableEdit=false
    var lotdata:LotDataBean?=null
    override fun getLayoutId(): Int {
        return R.layout.activity_lotinfo
    }

    override fun processLogic() {
        setTopTitle("详情")
        initBtmOnlyMind("打印票据")
//        showTopEdit(true)
        mBinding!!.llWeight.visibility=View.GONE
        lotdata=intent.getSerializableExtra("lotdata")as LotDataBean
        if (lotdata==null){
            finish()
            return
        }

        loadLotUI()
        mViewModel!!.init(this,mBinding!!.rvGoods,lotdata!!)
        initEvent()

    }

    private fun initEvent() {
        mBinding!!.tvLeft.setOnClickListener {
            if (selectBean==null)
                return@setOnClickListener
            var changheW=selectBean!!.weight-1
            if (changheW<0)
                changheW=0f
            selectBean!!.weight=changheW
            showGoods(selectBean!!)
        }

        mBinding!!.tvRight.setOnClickListener {
            if (selectBean==null)
                return@setOnClickListener
            var changheW=selectBean!!.weight+1
            selectBean!!.weight=changheW
            showGoods(selectBean!!)
        }
        mBinding!!.imgDelete.setOnClickListener {
            if (selectBean==null)
                return@setOnClickListener
            selectBean=null
            mBinding!!.etWeight.setText("0")
            mBinding!!.goodsNo.setText("")
            mBinding!!.tvGoodsName.setText("")
        }
    }
    fun setLotNumber(text:String?){
        mBinding!!.tvLotNumber.text=text+""
    }

    fun loadLotUI() {

         mBinding!!.tvClient.setText(lotdata!!.lotName)
         mBinding!!.etExp.setText(lotdata!!.exp)
         mBinding!!.etUser.setText(lotdata!!.user)
         mBinding!!.tvTime.setText(CommUtil.getTimebyStamp(lotdata!!.createTime))


        if (lotdata!!.status == "完成") {
            showBottomOnly(true)
            mBinding!!.llBottomTwoParent.visibility=View.GONE
        }else{
            showBottomOnly(false)
            mBinding!!.llBottomTwoParent.visibility=View.VISIBLE
            mBinding!!.bottomLeftTv.setText("完成")
            mBinding!!.bottomRightTv.setText("抄重")
            mBinding!!.bottomLeftTv.setOnClickListener {
                if (mBinding!!.bottomLeftTv.text == "完成") {  //保存单据
                    Thread{
                        lotdata!!.status="完成"
                        DataBaseManager.db.lotDao().insertAll(lotdata!!)
                      runOnUiThread {
                          loadLotUI()
                      }
                    }.start()
                } else {    //修改单据
                    mViewModel!!.showDelet(true)
                }

            }
            mBinding!!.bottomRightTv.setOnClickListener{
                if (mBinding!!.bottomRightTv.text=="抄重"){  //抄重
//                    showTopEdit(true)
                    mBinding!!.bottomLeftTv.setText("修改")
                    mBinding!!.bottomRightTv.setText("保存")
                    mBinding!!.llLotInfo.visibility=View.GONE
                    mBinding!!.llWeight.visibility=View.VISIBLE
                    mBinding!!.imgDelete.setOnClickListener {
                        mBinding!!.etWeight.setText("0")
                        mBinding!!.goodsNo.setText("")
                        mBinding!!.tvGoodsName.setText("")
                    }
                }else{                                  //保存返回
                    mBinding!!.llLotInfo.visibility=View.VISIBLE
                    mBinding!!.llWeight.visibility=View.GONE
                    mBinding!!.bottomLeftTv.setText("完成")
                    mBinding!!.bottomRightTv.setText("抄重")
                    mViewModel!!.showDelet(false)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        LiveDataBus.get().with("reloadLot").postValue(1)
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveDataBus.get().with(Constant.LD_UP_HOME_TITLE).postValue(1)
    }

    override fun onclickTopEdit() {
        super.onclickTopEdit()
//        test代码
       for (index in 10..1100){
           loadCoded("(01)99414637511176(11)210218(3102)001951(21)1113$index")  //(01)99414637511176(11)210218(3102)001951(21)111300"
       }

//        showTopEdit(false)
//        mViewModel!!.showDelet(false)
//        mBinding!!.llLotInfo.visibility=View.VISIBLE
//        mBinding!!.llWeight.visibility=View.GONE
    }

    override fun findViewModelClass(): Class<VMLotInfo> {
        return VMLotInfo::class.java
    }

    override fun loadCoded(scanCode: String) {
        Log.i(TAG, "loadCoded: String "+scanCode+" enableAdd "+(mBinding!!.llWeight.visibility==View.VISIBLE))
        if ( mBinding!!.llWeight.visibility==View.VISIBLE) {
            mBinding!!.tvGoodsName.setText(scanCode)
            mViewModel!!.parCode(scanCode)
        }
    }

    var selectBean:GoodsBean?=null
    var handler:Handler= object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what==1){
                mViewModel!!.loadData()
            }
        }
    }

    fun showGoods(gb:GoodsBean){   //直接添加 修改
        showProgress("添加中")
        selectBean=gb;

        mBinding!!.etWeight.setText(gb.weight.toString())
        mBinding!!.goodsNo.text=gb.GoodsNO
        mBinding!!.tvGoodsName.text=gb.GoodsName
        mViewModel!!.addWeight(selectBean!!)

        handler.removeMessages(1)
        handler.sendEmptyMessageDelayed(1,500)

    }
    fun resetRvTitle(count:String,weight:String){
        mBinding!!.rvtitleCount.text=count
        mBinding!!.rvtitleWeight.text=weight
    }

    override fun clickOnlyMind() {
        super.clickOnlyMind()
        if (!PrintCenterManager.getInstance().isPrinterConnected()){
            CommUtil.ToastU.showToast("打印机未连接 请链接打印机~！")
            startActivity(Intent(this,PrintActivity::class.java))
        }else{
            mViewModel!!.printLot()
        }
    }
}