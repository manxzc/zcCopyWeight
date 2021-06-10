package cn.ymade.module_home.db.beans

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author zc.xie
 * @date 2021/6/10 0010.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
@Entity()
data class GoodsBean(@PrimaryKey(autoGenerate = true)
                     val id:Int,
                     @ColumnInfo(name = "GoodsNO") var GoodsNO: String ?,
                     @ColumnInfo(name = "GoodsName") val GoodsName: String?) {

}