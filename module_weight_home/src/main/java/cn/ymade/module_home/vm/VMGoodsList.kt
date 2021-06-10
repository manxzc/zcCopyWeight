package cn.ymade.module_home.vm

import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ymade.module_home.adapter.GoodsListAdapter
import cn.ymade.module_home.db.beans.GoodsBean
import cn.ymade.module_home.db.database.DataBaseManager
import cn.ymade.module_home.ui.GoodsListActivity
import com.zcxie.zc.model_comm.base.BaseViewModel
import com.zcxie.zc.model_comm.callbacks.CallBack
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @author zc.xie
 * @date 2021/6/10 0010.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class VMGoodsList :BaseViewModel() {
    var lastSearch:String=""
    var activity:GoodsListActivity?=null
    var datas= mutableListOf<GoodsBean>()

    var adapter=GoodsListAdapter(datas,object :CallBack<GoodsBean>{
        override fun callBack(data: GoodsBean) {
            Log.i(TAG, "callBack: "+data)
        }
    })

    fun init(act:GoodsListActivity,rv:RecyclerView){
        activity=act
        rv.layoutManager=LinearLayoutManager(act)
        rv.adapter=adapter
        loadData()
    }


    fun searchData(string: String){
        if (string!=lastSearch) {
            lastSearch = string
            loadData()
        }
    }
    private fun loadData(){
        datas.clear()
        Observable.create<List<GoodsBean>> {

           it.onNext(DataBaseManager.db.goodsDao().loadAllByNos(lastSearch))

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<GoodsBean>> {
                override fun onSubscribe(d: Disposable?) {
                }
                override fun onNext(t: List<GoodsBean>?) {
                    Log.i(TAG, "onNext: initData " + t?.size)
                    datas.addAll(t!!)
                    adapter.notifyDataSetChanged()
                    activity!!.reloadTitle( t.size)
                }

                override fun onError(e: Throwable?) {
                }

                override fun onComplete() {
                }
            })
    }
}