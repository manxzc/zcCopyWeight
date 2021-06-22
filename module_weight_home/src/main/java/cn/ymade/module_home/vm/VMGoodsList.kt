package cn.ymade.module_home.vm

import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ymade.module_home.adapter.GoodsCategoryAdapter
import cn.ymade.module_home.adapter.GoodsListAdapter
import cn.ymade.module_home.db.beans.GoodsBean
import cn.ymade.module_home.db.beans.GoodsCatrgoryBeanN
import cn.ymade.module_home.db.database.DataBaseManager
import cn.ymade.module_home.ui.GoodsListActivity
import com.zcxie.zc.model_comm.base.BaseViewModel
import com.zcxie.zc.model_comm.callbacks.CallBack
import com.zcxie.zc.model_comm.util.CommUtil
import com.zcxie.zc.model_comm.util.LiveDataBus
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
    var datas= mutableListOf<GoodsCatrgoryBeanN>()
    var selectGoodsCategory=false

    var adapter= GoodsCategoryAdapter(datas,object :CallBack<GoodsCatrgoryBeanN>{
        override fun callBack(data: GoodsCatrgoryBeanN) {
            Log.i(TAG, "callBack: "+data)
            if (selectGoodsCategory){
                LiveDataBus.get().with("selectGoodsCategory",).postValue(data) //货品选择
                activity!!.finish()
                return
            }
            activity!!.createDialog(data.goodsNo!!,data.goodsName!!)
        }
    })

    fun init(act:GoodsListActivity,rv:RecyclerView,selectGoodsCategory:Boolean){
        this.selectGoodsCategory=selectGoodsCategory
        activity=act
        rv.layoutManager=LinearLayoutManager(act)
        rv.adapter=adapter
        loadData()
    }
    fun deleteItem(position:Int){
       var deletBean= datas.removeAt(position)
        activity!!.reloadTitle( datas.size)
        Thread{
            DataBaseManager.db.goodsCategoryDao().delete(deletBean)
        }.start()
    }

    fun createGooodsCategory(no:String,name:String,oldNo:String){
        Thread{
            if (DataBaseManager.db.goodsCategoryDao().loadAllByNos(no).isNotEmpty()&&TextUtils.isEmpty(oldNo)){
                activity!!.runOnUiThread {
                    CommUtil.ToastU.showToast("此货号已存在")
                }
                return@Thread
            }
            if (!TextUtils.isEmpty(oldNo)){
                var delete=GoodsCatrgoryBeanN()
                delete.goodsNo=oldNo
                DataBaseManager.db.goodsCategoryDao().delete(delete)
            }
            var createBean=GoodsCatrgoryBeanN()
            createBean.goodsName=name
            createBean.goodsNo=no
            createBean.lastTime=System.currentTimeMillis()
            DataBaseManager.db.goodsCategoryDao().insertAll(createBean)
            loadData()
        }.start()


    }

    fun searchData(string: String){
        if (string!=lastSearch) {
            lastSearch = string
            loadData()
        }
    }
    private fun loadData(){
        datas.clear()
        Observable.create<List<GoodsCatrgoryBeanN>> {
            it.onNext(DataBaseManager.db.goodsCategoryDao().loadAllByNos(if (lastSearch.isNullOrEmpty()) null else lastSearch))
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<GoodsCatrgoryBeanN>> {
                override fun onSubscribe(d: Disposable?) {
                }
                override fun onNext(t: List<GoodsCatrgoryBeanN>?) {
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