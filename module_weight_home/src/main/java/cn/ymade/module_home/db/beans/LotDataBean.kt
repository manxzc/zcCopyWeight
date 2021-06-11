package cn.ymade.module_home.db.beans

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LotDataBean(
        @PrimaryKey(autoGenerate = true)
        val lotId:Int,
        var status: String ="处理中", //状态
        var lotName: String , //客户或者单据名称
        var lotNo: String ,// 60196 牛肉
        var items: Int, //件数
        var user: String ,//经办人
        var weight: Float , //重量
        var createTime:Int
) {
}