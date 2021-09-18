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
import cn.ymade.module_home.model.RuleData
import cn.ymade.module_home.ui.LotInfoActivity
import com.zcxie.zc.model_comm.base.BaseViewModel
import com.zcxie.zc.model_comm.callbacks.CallBack
import com.zcxie.zc.model_comm.util.CommUtil
import cn.ymade.module_home.utils.PrintCenterManager
import com.google.gson.Gson
import com.zcxie.zc.model_comm.util.AppConfig
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
    var ruleJson:String?=""
    var ruleData:RuleData?=null
    fun init(act:LotInfoActivity, rv:RecyclerView, lotDataBean: LotDataBean){
        lotdata=lotDataBean
        activity=act
        ruleJson=AppConfig.ruleStr.get()
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
        activity!!.showProgress("加载中")
        loadData()

    }
    fun loadData(){
        list.clear()

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
            val s: String = ddf1.format(tw).replace(",","")
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
                    activity!!.resetRvTitle(
                        "件数 (" + lotdata!!.items + ")",
                        "重量 (" + lotdata!!.weight + ")"
                    )
                    activity!!.setLotNumber(lotdata!!.lotNo)
                    activity!!.hideProgress()
                }

                override fun onError(e: Throwable?) {
                    activity!!.hideProgress()
                    Log.i(TAG, "onError: e "+e.toString())
                    CommUtil.ToastU.showToast("失败~")
                }

                override fun onComplete() {
                    activity!!.hideProgress()
                }
            })

    }
    fun showDelet(boolean: Boolean){
        adapter.showDelete(boolean)
        adapter.notifyDataSetChanged()
    }
    fun addWeight(goodsBean: GoodsBean){
        Thread{
            DataBaseManager.db.goodsDao().insertAll(goodsBean)
        }.start()
    }
    var hpList= mutableListOf<RuleData.RuleBean>() //货品
    var ldList= mutableListOf<RuleData.RuleBean>() //量度
    var rqList= mutableListOf<RuleData.RuleBean>() //日期
    var phList= mutableListOf<RuleData.RuleBean>() //批号
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

            if (TextUtils.isEmpty(ruleJson)){
                CommUtil.ToastU.showToast("请先同步规则！")
                return
            }
            if (ruleData==null){
                ruleData=Gson().fromJson(ruleJson,RuleData::class.java)
                for (index in 0 until ruleData!!.rule.size){
                    var ruleBean=ruleData!!.rule[index]
                    if (ruleBean.status==1) {
                        when (ruleBean.category) {
                            "货品" -> {
                                hpList.add(ruleBean)
                            }
                            "量度" -> {
                                ldList.add(ruleBean)
                            }
                            "日期" -> {
                                rqList.add(ruleBean)
                            }
                            "批号" -> {
                                phList.add(ruleBean)
                            }
                        }
                    }
                }
            }
            if (ruleData==null){
                CommUtil.ToastU.showToast("规则数据错误！")
                return
            }
            Log.i(TAG, "parCode: list 货品 规则数量 "+hpList.size+" 量度 规则数量 "+ldList.size+" 日期 规则数量 "+rqList.size+" 批号 规则数量 "+phList.size)
            Log.i(TAG, "parCode: endCode $endCode")


            huopin=""
            var sub=""
            var unit=""
            for (index in 0 until hpList.size){
                var hp=hpList[index]
                Log.i(TAG, "parCode: hp rule "+hp.strKey+" start "+hp.strStart+" length "+hp.strLength)
                if (endCode.startsWith(hp.strKey)){
                    Log.i(TAG, "parCode: start with "+hp.strKey)
                    huopin=endCode.substring(hp.strStart+hp.strKey.length,hp.strKey.length+hp.strStart+hp.strLength)
                    sub=endCode.substring(hp.strKey.length+hp.strStart+hp.strLength,endCode.length)
                    break
                }
            }
            Log.i(TAG, "parCode: huopin $huopin sub= $sub")

            if (TextUtils.isEmpty(sub)){
                CommUtil.ToastU.showToast("当前规则找不到货号")
                return
            }
            var dateStr:String=""
            var ldIndex=0;
            var ldKeyLength=0
            for (index in 0 until ldList.size){
                var ld=ldList[index]
                Log.i(TAG, "parCode: ld "+ld.strKey)
                ldIndex=sub.indexOf(ld.strKey)
                Log.i(TAG, "parCode: ldIndex "+ldIndex)
                if (ldIndex>-1) {
                    ldKeyLength=ld.strKey.length;
                    unit=ld.strKey
                    weightStr = sub.substring(
                        ldIndex + ld.strKey.length,
                        ldIndex + ld.strStart + ld.strLength + ld.strKey.length
                    )
                    break
                }
            }

            if (ldIndex>-1)
            dateStr=sub.replaceRange(ldIndex,ldIndex+weightStr.length+ldKeyLength,"")
            else{
                weightStr
            }

            Log.i(TAG, "parCode: weightStr "+weightStr+" dateStr "+dateStr)

            if (dateStr.length>8) {
                var mon = dateStr.substring(4, 6)
                Log.i(TAG, "parCode: mon $mon date $dateStr")
                var date = dateStr.substring(2, 8)
                if (mon.toInt() <= 12) {
                    for (index in 0 until rqList.size) {
                        var rq = rqList[index]
                        Log.i(TAG, "parCode: rq.strKey " + rq.strKey)
                        if (dateStr.startsWith(rq.strKey)) {
                            lotNumber = dateStr.substring(
                                rq.strKey.length + rq.strStart,
                                rq.strKey.length + rq.strLength
                            )
                            if (dateStr.startsWith("17")) {
                                shelfLife = date
                                Log.i(TAG, "parCode: 这是 有效期 $shelfLife")
                            } else if (dateStr.startsWith("13")) {
                                packageDate = date
                                Log.i(TAG, "parCode: 这是 包装日期 $createDate")
                            } else if (dateStr.startsWith("11")) {
                                createDate = date
                                Log.i(TAG, "parCode: 这是 生产日期 $createDate")
                            }
                            break
                        }

                    }
                }
            }


            Log.i(TAG, "parCode: weightStr $weightStr date $dateStr")

            Log.i(TAG, "parCode: lotNumber $lotNumber")

            var t:Float=if (unit.startsWith("3102")) 100.00f
            else if (unit.startsWith("3100")) 1f
            else if (unit.startsWith("3101"))10.0f else 100.00f
            weitht=weightStr.toInt()/t
        }catch (e:Exception){
            Log.i(TAG, "parCode: error "+e.toString())
        }
//        return
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

//    if (sub.startsWith(ld.strKey)){ //符合 量度的数据
//        Log.i(TAG, "parCode: ld rule "+ld.strKey+" start "+ld.strStart+" length "+ld.strLength)
//        if (TextUtils.isEmpty(ld.strKey)){
//            Log.i(TAG, "parCode:ld  ld strKey  empty ")
//            weightStr=sub.substring(ld.strStart,ld.strStart+ld.strLength)
//            dateStr=sub.substring(ld.strStart+ld.strLength)
//            for (dateIndex in 0 until rqList.size){
//                var rq=rqList[dateIndex]
//                if (dateStr.startsWith(rq.strKey)){
//                    dateStr=dateStr.substring(rq.strKey.length+rq.strStart,rq.strKey.length+rq.strLength)
//                    break
//                }
//            }
//            break
//        }
//        else if (sub.startsWith(ld.strKey)){
//            Log.i(TAG, "parCode:ld  start with "+ld.strKey)
//            weightStr=sub.substring(ld.strStart+ld.strKey.length,ld.strKey.length+ld.strStart+ld.strLength)
//            dateStr=sub.substring(ld.strStart+ld.strKey.length+ld.strLength)
//            for (dateIndex in 0 until rqList.size){
//                var rq=rqList[dateIndex]
//                if (dateStr.startsWith(rq.strKey)){
//                    dateStr=dateStr.substring(rq.strKey.length+rq.strStart,rq.strKey.length+rq.strLength)
//                    break
//                }
//            }
//            break
//        }
//    }else{ //不符合 检查时间日期
//
//    }
}