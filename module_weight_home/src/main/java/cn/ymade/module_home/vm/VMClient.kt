package cn.ymade.module_home.vm

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ymade.module_home.adapter.ClientAdapter
import cn.ymade.module_home.db.beans.ClientBean
import cn.ymade.module_home.db.beans.LotDataBean
import cn.ymade.module_home.db.database.DataBaseManager
import cn.ymade.module_home.ui.ClientActivity
import com.zcxie.zc.model_comm.base.BaseViewModel
import com.zcxie.zc.model_comm.callbacks.CallBack
import com.zcxie.zc.model_comm.util.LiveDataBus
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class VMClient :BaseViewModel() {

    var act:ClientActivity?=null
    var list= mutableListOf<ClientBean>()
    var lastSearch=""
    var selectClient=false
    val itemClick=object :CallBack<ClientBean>{
        override fun callBack(data: ClientBean?) {
            Log.i(TAG, "callBack: itemClick "+data!!.clientName)
            if (selectClient){
                LiveDataBus.get().with("selectedClient",).postValue(data.clientName+" "+data.clientPhone)
                act!!.finish()
                return
            }
            act!!.createDialog(data.clientName,data.clientPhone)
        }
    }
    val itemDeleteClick=object :CallBack<ClientBean>{
        override fun callBack(data: ClientBean?) {
            Log.i(TAG, "callBack: itemDeleteClick "+data!!.clientName)
        }
    }
    val adapter =ClientAdapter(list,itemClick,itemDeleteClick)

    fun delete(position:Int){
      var deletbean=  list.removeAt(position)
        adapter.notifyDataSetChanged()
        act!!.reloadTitle(list.size)
        Thread{
            DataBaseManager.db.clientDao().delete(deletbean)
        }.start()
    }

    fun initData(act:ClientActivity,rv:RecyclerView,selectClient:Boolean){
        this.selectClient=selectClient
        this.act=act;
        rv.layoutManager=LinearLayoutManager(act)
        rv.adapter=adapter
        loadData()
    }

    fun  search(str:String){
        if (lastSearch!=str){
            lastSearch=str
            loadData()
        }
    }
    fun  createClient(name:String,phone:String){
        Thread{
            DataBaseManager.db.clientDao().insertAll(ClientBean(name,phone))
            loadData()
        }.start()
    }

    fun loadData(){
        list.clear()
        Observable.create<List<ClientBean>> {
         it.onNext(DataBaseManager.db.clientDao().loadAllByname(if (lastSearch.isNullOrEmpty())null else lastSearch))
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<ClientBean>> {
                    override fun onSubscribe(d: Disposable?) {
                    }

                    override fun onNext(t: List<ClientBean>?) {
                        Log.i(TAG, "onNext: initData " + t?.size)
                        list.addAll(t!!)
                        adapter.notifyDataSetChanged()
                        act!!.reloadTitle(t.size)
                    }

                    override fun onError(e: Throwable?) {
                    }

                    override fun onComplete() {
                    }
                })
    }

}