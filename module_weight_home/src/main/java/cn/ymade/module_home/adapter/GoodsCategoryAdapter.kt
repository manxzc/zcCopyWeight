package cn.ymade.module_home.adapter

import android.view.View
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ItemGoodsCategoryBinding
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
class GoodsCategoryAdapter(var list: List<GoodsCatrgoryBeanN>, val itemCallback:CallBack<GoodsCatrgoryBeanN>) : BindBaseAdapter<GoodsCatrgoryBeanN>(list) {
    var showDelete=false
    var itemDeleteCallback:CallBack<GoodsBean>?=null
    fun showDelete(show:Boolean){
        showDelete=show
    }
    override fun getLayoutId(): Int {
        return R.layout.item_goods_category
    }

    override fun onBindViewHolder(holder: BindBaseViewHolder, position: Int) {
        var data=list[position]
        ( holder.binding as ItemGoodsCategoryBinding).bean=data
        ( holder.binding as ItemGoodsCategoryBinding).executePendingBindings()
        holder.itemView.setOnClickListener {
            itemCallback?.callBack(data)
        }
        if (showDelete){
            ( holder.binding as ItemGoodsCategoryBinding).assetSelectDelectIv.visibility=View.VISIBLE
        }else{
            ( holder.binding as ItemGoodsCategoryBinding).assetSelectDelectIv.visibility=View.GONE
        }
        ( holder.binding as ItemGoodsCategoryBinding).assetSelectDelectIv.setOnClickListener {
            itemDeleteCallback?.let {
                callBack -> list[position]
            }
        }

    }
}