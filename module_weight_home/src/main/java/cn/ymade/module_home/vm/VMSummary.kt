package cn.ymade.module_home.vm

import android.text.TextUtils
import android.util.Log
import cn.ymade.module_home.db.database.DataBaseManager
import cn.ymade.module_home.model.SummaryData
import com.zcxie.zc.model_comm.base.BaseViewModel
import com.zcxie.zc.model_comm.callbacks.CallBack
import com.zcxie.zc.model_comm.util.CommUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author zc.xie
 * @date 2021/6/4 0004.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class VMSummary :BaseViewModel() {

fun getNum(key:String,starTime : String, stopTime : String, callback: CallBack<SummaryData>){
    val df2: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    var d1: Date? = null
    var d2: Date? = null
    try {
        d1 = df2.parse(starTime)
        d2 = df2.parse(stopTime)

        d2!!.hours=23
        d2!!.minutes=59
        d2!!.seconds=59
        Log.i(TAG, "getNum: parse d1 "+d1+" d2 "+d2)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    val d3 = (d2!!.time)/1000 - (d1!!.time)/1000
    val d4 = 60*60*24*31 //间隔日期

    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")

    var today=Date( CommUtil.getEndTime())
    val nowdayTime = dateFormat.format(today)
    val nowDate = dateFormat.parse(nowdayTime)

    if ((d2!!.time)/1000 > (nowDate!!.time)/1000) {
        callback.callBack(null)
        CommUtil.ToastU.showToast("查询时间不能超过今天")
        return
    }
    if (d3 > d4) {
        callback.callBack(null)
        CommUtil.ToastU.showToast("查询时间不能超过30天")
        return
    }
    if (d3 < 0) {
        callback.callBack(null)
        CommUtil.ToastU.showToast("结束时间不能小于开始时间")
        return
    }

    Observable.create<SummaryData> {
        Log.i(TAG, "getNum: d1.time "+d1.time+" d2.time "+d2.time+" key "+key)
        val lots = DataBaseManager.db.lotDao().loadAllInTimeAndKey(if (TextUtils.isEmpty(key) )null else key ,d1.time,d2.time) //所有数量
        Log.i(TAG, "getNum: lots "+lots)
        var lotCount=lots.size
        var goodsCountByLot=0
        for (lot in lots){
            goodsCountByLot+=lot.items
        }

        var homeTitleData= SummaryData(lotCount,goodsCountByLot)
        it.onNext(homeTitleData)
    }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            callback.callBack(it)
        }

}
}