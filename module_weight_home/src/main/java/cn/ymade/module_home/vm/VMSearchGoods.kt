package cn.ymade.module_home.vm

import android.text.TextUtils
import android.util.Log
import cn.ymade.module_home.db.beans.GoodsBean
import cn.ymade.module_home.db.beans.LotDataBean
import cn.ymade.module_home.db.database.DataBaseManager
import cn.ymade.module_home.ui.SearchGoodsActivity
import com.zcxie.zc.model_comm.base.BaseViewModel
import com.zcxie.zc.model_comm.util.CommUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.NumberFormat

/**
 * @author zc.xie
 * @date 2021/6/14 0014.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class VMSearchGoods :BaseViewModel() {
    var act:SearchGoodsActivity?=null
    var search:String=""

    fun init(activity: SearchGoodsActivity){
        act=activity
    }
    fun search(code:String){
        if (code.isNullOrEmpty()||code==search){
            return
        }
        search=code
        loadData()

    }
    fun loadData(){
        act!!.showProgress("加载货品中")
        Observable.create<List<GoodsBean>?> {
            val datas= DataBaseManager.db.goodsDao().loadAllByCode(search)
            it.onNext(datas)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<GoodsBean>> {
                override fun onSubscribe(d: Disposable?) {
                }
                override fun onNext(t: List<GoodsBean>) {
                    Log.i(TAG, "onNext: initData " + t?.size)
                    act!!.hideProgress()
                    if (t.isEmpty()){
                        CommUtil.ToastU.showToast("查无数据")
                        act!!.showGoods(null)
                    }else{
                        act!!.showGoods(t[0])
                        loadLotByGoods(t[0])
                    }
                }

                override fun onError(e: Throwable?) {
                }

                override fun onComplete() {
                }
            })
    }
    fun  loadLotByGoods(gb:GoodsBean){
        act!!.showProgress("加载单据中。")
        Observable.create<List<LotDataBean>?> {
            val datas= DataBaseManager.db.lotDao().loadAllById(gb.lotId)

            it.onNext(datas)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<LotDataBean>> {
                override fun onSubscribe(d: Disposable?) {
                }
                override fun onNext(t: List<LotDataBean>) {
                    Log.i(TAG, "onNext: initData " + t?.size)
                    act!!.hideProgress()
                    if (t.isEmpty()){
                        CommUtil.ToastU.showToast("没有查询到相关订单")
                        act!!.showLotInfo(null)
                    }else{
                        act!!.showLotInfo(t[0])
                    }
                }

                override fun onError(e: Throwable?) {
                }

                override fun onComplete() {
                }
            })

    }
}