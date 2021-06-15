package cn.ymade.module_home.adapter

import android.view.View
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.ItemClientBinding
import cn.ymade.module_home.databinding.ItemStaffLayoutBinding
import cn.ymade.module_home.db.beans.ClientBean
import com.dothantech.printer.IDzPrinter
import com.zcxie.zc.model_comm.base.BindBaseAdapter
import com.zcxie.zc.model_comm.base.BindBaseViewHolder
import com.zcxie.zc.model_comm.callbacks.CallBack

/**
 * @author zc.xie
 * @date 2021/6/15 0015.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
open class PrintBluetoothAdapter(val list: List<IDzPrinter.PrinterAddress>, val itemClick: CallBack<IDzPrinter.PrinterAddress>)
    :BindBaseAdapter<IDzPrinter.PrinterAddress>(list) {
    override fun getLayoutId(): Int {
        return R.layout.item_staff_layout
    }

    override fun onBindViewHolder(holder: BindBaseViewHolder, position: Int) {
        var data=list[position]
        ( holder.binding as ItemStaffLayoutBinding).bean=data
        ( holder.binding as ItemStaffLayoutBinding).executePendingBindings()


        holder.itemView.setOnClickListener {
            itemClick?.let {
                it.callBack(data)
            }
        }
    }
}