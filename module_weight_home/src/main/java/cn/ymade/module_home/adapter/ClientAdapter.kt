package cn.ymade.module_home.adapter

import android.view.View
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ItemClientBinding
import cn.ymade.module_home.databinding.ItemLotListBinding
import cn.ymade.module_home.db.beans.ClientBean
import com.zcxie.zc.model_comm.base.BindBaseAdapter
import com.zcxie.zc.model_comm.base.BindBaseViewHolder
import com.zcxie.zc.model_comm.callbacks.CallBack

class ClientAdapter(val list: List<ClientBean>,val itemClick:CallBack<ClientBean>,val itemDeleteClick:CallBack<ClientBean>) :BindBaseAdapter<ClientBean>(list) {

    var enableDelete=false
    fun enableDelete(enable:Boolean){
        enableDelete=enable
    }
    override fun getLayoutId(): Int {
        return R.layout.item_client
    }

    override fun onBindViewHolder(holder: BindBaseViewHolder, position: Int) {
        var data=list[position]
        ( holder.binding as ItemClientBinding).bean=data
        ( holder.binding as ItemClientBinding).executePendingBindings()
        if (enableDelete){
            ( holder.binding as ItemClientBinding).deleteIv.visibility=View.VISIBLE
            ( holder.binding as ItemClientBinding).deleteIv.setOnClickListener {
                itemDeleteClick?.callBack(data)
            }
        }else{
            ( holder.binding as ItemClientBinding).deleteIv.visibility=View.GONE
        }

        holder.itemView.setOnClickListener {
            itemClick?.let {
                it.callBack(data)
            }
        }
    }
}