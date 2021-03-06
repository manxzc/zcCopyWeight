package cn.ymade.module_home.adapter

import android.util.Log
import android.view.View
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ItemGoodsBinding
import cn.ymade.module_home.db.beans.GoodsBean
import cn.ymade.module_home.db.beans.GoodsCatrgoryBeanN
import com.zcxie.zc.model_comm.base.BindBaseAdapter
import com.zcxie.zc.model_comm.base.BindBaseViewHolder
import com.zcxie.zc.model_comm.callbacks.CallBack

/**
 * @author zc.xie
 * @date 2021/6/10 0010.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
class GoodsListAdapter(var list: List<GoodsBean>, val itemCallback:CallBack<GoodsBean>) : BindBaseAdapter<GoodsBean>(list) {
    var showDelete=false
    var itemDeleteCallback:CallBack<Int>?=null
    fun showDelete(show:Boolean){
        showDelete=show
    }
    override fun getLayoutId(): Int {
        return R.layout.item_goods
    }

    override fun onBindViewHolder(holder: BindBaseViewHolder, position: Int) {
        var data=list[position]
        ( holder.binding as ItemGoodsBinding).bean=data
        ( holder.binding as ItemGoodsBinding).executePendingBindings()
        holder.itemView.setOnClickListener {
            itemCallback?.callBack(data)
        }
        if (showDelete){
            ( holder.binding as ItemGoodsBinding).assetSelectDelectIv.visibility=View.VISIBLE
        }else{
            ( holder.binding as ItemGoodsBinding).assetSelectDelectIv.visibility=View.GONE
        }
        ( holder.binding as ItemGoodsBinding).assetSelectDelectIv.setOnClickListener {
            Log.i("TAG", "onBindViewHolder: delete "+position+" itemDeleteCallback "+itemDeleteCallback)
            itemDeleteCallback?.callBack(position)
        }

    }
}