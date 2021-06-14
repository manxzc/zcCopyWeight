package cn.ymade.module_home.db.beans

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
 class LotDataBean :Serializable{
        @PrimaryKey(autoGenerate = true)
        var lotId:Long?=0
        var status: String ="处理中"//状态
        var lotName: String?=null //客户
        var exp: String ?=null //摘要
        var user: String?=null //经办人
        var lotNo: String?=null // 60196 牛肉  货品
        var items: Int=0 //件数
        var weight: Float?=0f //重量
        var createTime:Long=System.currentTimeMillis()

}