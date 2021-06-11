package cn.ymade.module_home.db.beans

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ClientBean(
        @PrimaryKey
        var clientName: String , //客户名称
        var clientPhone: String// 电话
)
