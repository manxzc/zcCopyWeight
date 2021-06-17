package cn.ymade.module_home.vm

import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ymade.module_home.adapter.GoodsListAdapter
import cn.ymade.module_home.db.beans.GoodsBean
import cn.ymade.module_home.db.beans.GoodsCatrgoryBeanN
import cn.ymade.module_home.db.beans.LotDataBean
import cn.ymade.module_home.db.database.DataBaseManager
import cn.ymade.module_home.ui.LotInfoActivity
import com.zcxie.zc.model_comm.base.BaseViewModel
import com.zcxie.zc.model_comm.callbacks.CallBack
import com.zcxie.zc.model_comm.util.CommUtil
import cn.ymade.module_home.utils.PrintCenterManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.NumberFormat


/**
 * @author zc.xie
 * @date 2021/6/12 0012.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class VMLotInfo :BaseViewModel() {
    var lotdata:LotDataBean?=null
    var activity:LotInfoActivity?=null
    var list= mutableListOf<GoodsBean>()

    var adapter= GoodsListAdapter(list,object : CallBack<GoodsBean> {
        override fun callBack(data: GoodsBean) {
            Log.i(TAG, "callBack: "+data)
        }
    })

    fun init(act:LotInfoActivity, rv:RecyclerView, lotDataBean: LotDataBean){
        lotdata=lotDataBean
        activity=act
        adapter.itemDeleteCallback=         //删除
            object :CallBack<Int>{
                override fun callBack(postion: Int) {
                    Log.i(TAG, "init: delete postion "+postion)
                    Thread{
                        var data=list.removeAt(postion)
                        DataBaseManager.db.goodsDao().delete(data)

                        act.runOnUiThread(object :Runnable{
                            override fun run() {
                             loadData()
                            }
                        })
                    }.start()
                }

            }

        rv.layoutManager=LinearLayoutManager(act)
        rv.adapter=adapter
        loadData()

    }
    fun loadData(){
        list.clear()
        activity!!.showProgress("加载中")
        Observable.create<List<GoodsBean>> {
            val datas=DataBaseManager.db.goodsDao().loadAllByLotId(lotdata!!.lotId)

                var tw=0.00f
                var no="-"
                for ( w in datas){
                    tw+=w.weight
                    if ("-"==no&&!TextUtils.isEmpty(w.GoodsNO))
                        no=w.GoodsNO
                    Log.i(TAG, "loadData: w "+w.GoodsNO+" w "+w.GoodsName+" w.weight "+w.weight)
                }
            Log.i(TAG, "loadData: tw "+tw)
                lotdata!!.lotNo=no
                Log.i(TAG, "loadData: lotNo "+ lotdata!!.lotNo+datas)
            val ddf1: NumberFormat = NumberFormat.getNumberInstance()

            ddf1.setMaximumFractionDigits(2)
            val s: String = ddf1.format(tw)
            Log.i(TAG, "loadData: tw "+tw+" s "+s)
                lotdata!!.weight=s.toFloat()
                lotdata!!.items=datas.size
                DataBaseManager.db.lotDao().insertAll(lotdata!!)

            it.onNext(datas)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<GoodsBean>> {
                override fun onSubscribe(d: Disposable?) {
                }
                override fun onNext(t: List<GoodsBean>?) {
                    Log.i(TAG, "onNext: initData " + t?.size)
                    list.addAll(t!!)
                    adapter.notifyDataSetChanged()
                    activity!!.resetRvTitle("件数 ("+lotdata!!.items+")","重量 ("+lotdata!!.weight+")")
                    activity!!.hideProgress()
                }

                override fun onError(e: Throwable?) {
                }

                override fun onComplete() {
                }
            })

    }
    fun showDelet(boolean: Boolean){
        adapter.showDelete(boolean)
        adapter.notifyDataSetChanged()
    }
    fun addWeight(goodsBean: GoodsBean){
        activity!!.showProgress("添加中")
        Thread{
            DataBaseManager.db.goodsDao().insertAll(goodsBean)

            activity!!.runOnUiThread {
                loadData()
            }
        }.start()
    }
    fun parCode(code:String){
        Log.i(TAG, "parCode: code "+code+" 长度 "+code.length)

        var endCode=   code.replace("(","").replace(")","")

        var createDate=""
        var shelfLife=""
        var packageDate=""
        var lotNumber=""
        var huopin="-"
        var weightStr=""
        var weitht=0f
        try {
            Log.i(TAG, "parCode: endCode $endCode")
            huopin=endCode.substring(10,15)
            var sub=endCode.substring(16,endCode.length)
            Log.i(TAG, "parCode: huopin $huopin sub= $sub")
            var dateStr:String=""

            if (sub.startsWith("31")){
                Log.i(TAG, "parCode: 这是 重量 $sub")
                weightStr=sub.substring(0,10);
                dateStr=sub.substring(10,18)
            }else if (sub.startsWith("17")||sub.startsWith("13")||sub.startsWith("11")) {
                dateStr=sub.substring(0,8)
                weightStr=sub.substring(8,18)
            }
            var mon=dateStr.substring(4,6)
            Log.i(TAG, "parCode: mon $mon date $dateStr")
            var date=dateStr.substring(2,8)

            if (mon.toInt()<=12){
                if (dateStr.startsWith("17")){
                    shelfLife=date
                    Log.i(TAG, "parCode: 这是 有效期 $shelfLife")
                } else if (dateStr.startsWith("13")){
                    packageDate=date
                    Log.i(TAG, "parCode: 这是 包装日期 $createDate")
                }else if (dateStr.startsWith("11")){
                    createDate=date
                    Log.i(TAG, "parCode: 这是 生产日期 $createDate")
                }
            }
            Log.i(TAG, "parCode: weightStr $weightStr date $dateStr")

            lotNumber=sub.substring(20,sub.length)
            Log.i(TAG, "parCode: lotNumber $lotNumber")

            var t:Float=if (weightStr.startsWith("3102")) 100.00f
            else if (weightStr.startsWith("3100")) 1f
            else if (weightStr.startsWith("3101"))10.0f else 100.00f
            weitht=weightStr.substring(4,weightStr.length).toInt()/t
        }catch (e:Exception){

        }

        /**
         * 解析完毕将数据放入ui 这里不需要存
         */
        Thread{
            var list=   DataBaseManager.db.goodsDao().loadAllByName(code)
            if (list.size>0){
                activity!!.runOnUiThread {
                    CommUtil.ToastU.showToast("此条码已存在此单据或其他单据中~！")
                }
                return@Thread
            }


            var goodsBean=  DataBaseManager.db.goodsCategoryDao().loadSingleByNos(huopin)
            if (goodsBean==null){
                goodsBean= GoodsCatrgoryBeanN()
                goodsBean.goodsNo=huopin
                DataBaseManager.db.goodsCategoryDao().insertAll(goodsBean)
            }

            var gb= GoodsBean()
            gb.GoodsName=code
            gb.lotNumber=lotNumber
            gb.lotId=lotdata!!.lotId!!
            gb.packageDate=packageDate
            gb.createDate=createDate
            gb.shelflife=shelfLife
            gb.GoodsNO=if (goodsBean==null) "$huopin -" else goodsBean.goodsNo+"-"+goodsBean.goodsName

            gb.weight=weitht
            Log.i(TAG, "parCode:  gb.weight "+ gb.weight)

            activity!!.runOnUiThread {
                activity!!. showGoods(gb)
            }
        }.start()
    }
    fun printLot(){

        PrintCenterManager.getInstance().printLot(lotdata,list)
    }
}