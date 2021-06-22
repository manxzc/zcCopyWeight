package cn.ymade.module_home.db.beans

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ClientBean(
        @PrimaryKey
        var clientName: String , //客户名称
        var lastTime: Long , //shijian
        var clientPhone: String// 电话
)
