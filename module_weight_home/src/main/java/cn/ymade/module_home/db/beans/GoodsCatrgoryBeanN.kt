package cn.ymade.module_home.db.beans

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

/**
 * @author zc.xie
 * @date 2021/6/14 0014.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
@Entity
class GoodsCatrgoryBeanN : Serializable{
    @PrimaryKey
    var goodsNo: String= ""
    var goodsName: String? = ""
    var lastTime: Long = System.currentTimeMillis()
}