package cn.ymade.module_home.db.beans

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * @author zc.xie
 * @date 2021/6/10 0010.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
@Entity()
 class GoodsBean  : Serializable {
    @PrimaryKey
    var GoodsName: String=""
    var lotId:Long=0
    var weight:Float=0f
   var createDate:String=""
   var shelflife:String=""
    var lotNumber:String="" //批号
    @ColumnInfo(name = "GoodsNO") var GoodsNO: String  =""  //货号
}