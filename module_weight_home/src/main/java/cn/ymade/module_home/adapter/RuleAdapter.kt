package cn.ymade.module_home.adapter

import android.util.Log
import cn.ymade.module_home.R
import cn.ymade.module_home.databinding.LayoutItemRuleBinding
import cn.ymade.module_home.model.RuleData
import com.google.gson.Gson
import com.zcxie.zc.model_comm.base.BindBaseAdapter
import com.zcxie.zc.model_comm.base.BindBaseViewHolder
import com.zcxie.zc.model_comm.util.AppConfig

class RuleAdapter(var ruleData: RuleData) :BindBaseAdapter<RuleData.RuleBean>(ruleData.rule) {
    override fun getLayoutId(): Int {
        return R.layout.layout_item_rule
    }

    override fun onBindViewHolder(holder: BindBaseViewHolder, position: Int) {
       var data=ruleData.rule[position]
        Log.i("TAG", "onBindViewHolder: data=ruleData "+data.category)
        (holder.binding as LayoutItemRuleBinding).bean=data
        (holder.binding as LayoutItemRuleBinding).executePendingBindings()
        (holder.binding as LayoutItemRuleBinding).swItem.isChecked=data.status==1
        (holder.binding as LayoutItemRuleBinding).swItem.setOnClickListener {
            data.status=if (data.status==0)1 else 0
            notifyDataSetChanged()
            var gson=Gson().toJson(ruleData)
            AppConfig.ruleStr.put(gson)
            Log.i("check", "onBindViewHolder: gson $gson")
        }
    }
}