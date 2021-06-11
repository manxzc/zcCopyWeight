package cn.ymade.module_home.db.dao

import androidx.room.*
import cn.ymade.module_home.db.beans.DevInfoBean
import cn.ymade.module_home.db.beans.GoodsBean

/**
 * @author zc.xie
 * @date 2021/6/10 0010.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
@Dao
interface GoodsDao {
    @Query("SELECT * FROM goodsbean")
    fun getAll(): List<GoodsBean>

    @Query("SELECT * FROM goodsbean WHERE :no is null or GoodsNO =(:no) ")
    fun loadAllByNos(no:String?): List<GoodsBean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg beans: GoodsBean)

    @Delete
    fun delete(bean: GoodsBean)
}