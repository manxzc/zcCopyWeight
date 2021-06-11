package cn.ymade.module_home.db.dao

import androidx.room.*
import cn.ymade.module_home.db.beans.DevInfoBean
import cn.ymade.module_home.db.beans.GoodsBean
import cn.ymade.module_home.db.beans.LotDataBean

/**
 * @author zc.xie
 * @date 2021/6/10 0010.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
@Dao
interface LotDao {
    @Query("SELECT * FROM lotdatabean")
    fun getAll(): List<LotDataBean>

    @Query("SELECT * FROM lotdatabean WHERE :no is null or lotName =(:no) ")
    fun loadAllByNos(no:String): List<LotDataBean>

    @Query("SELECT * FROM lotdatabean WHERE :no is null or lotName =(:no) and status=:status limit 1 , 3")
    fun getAllByLotStatus(no:String,status:String): List<LotDataBean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg beans: LotDataBean)

    @Delete
    fun delete(bean: LotDataBean)
}