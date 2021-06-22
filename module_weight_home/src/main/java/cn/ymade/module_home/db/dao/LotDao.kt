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

    @Query("SELECT * FROM lotdatabean WHERE  lotId =(:id) limit 1")
    fun loadAllById(id:Long): List<LotDataBean>

//    fun getCount(startTime:Long ,stopTime:Long): List<LotDataBean>

    @Query("SELECT * FROM lotdatabean WHERE :no is null or lotName like '%'||  :no || '%'")
    fun loadAllByNos(no:String?): List<LotDataBean>

    @Query("SELECT * FROM lotdatabean WHERE (createTime BETWEEN :start and :end) ")
    fun loadAllInTime( start:Long,end:Long): List<LotDataBean>

    @Query("SELECT * FROM lotdatabean WHERE (:key isnull or ( lotName like '%'||  :key || '%' or lotNo like '%'||:key || '%') )and (createTime BETWEEN :start and :end) ")
    fun loadAllInTimeAndKey( key:String?,start:Long,end:Long): List<LotDataBean>

    @Query("SELECT * FROM lotdatabean WHERE (:no is null or lotName =(:no)) and status=:status ") //limit 1 , 3
    fun getAllByLotStatus(no:String?,status:String): List<LotDataBean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg beans: LotDataBean)

    @Delete
    fun delete(bean: LotDataBean)
}