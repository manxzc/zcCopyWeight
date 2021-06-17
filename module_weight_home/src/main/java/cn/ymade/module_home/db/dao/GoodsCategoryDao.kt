package cn.ymade.module_home.db.dao

import androidx.room.*
import cn.ymade.module_home.db.beans.GoodsBean
import cn.ymade.module_home.db.beans.GoodsCatrgoryBeanN

/**
 * @author zc.xie
 * @date 2021/6/14 0014.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
@Dao
interface GoodsCategoryDao {
    @Query("SELECT * FROM goodscatrgorybeann order by goodsNo desc")
    fun getAll(): List<GoodsCatrgoryBeanN>

    @Query("SELECT * FROM goodscatrgorybeann WHERE :no is null or GoodsNO like '%'||:no||'%' order by goodsNo desc")
    fun loadAllByNos(no:String?): List<GoodsCatrgoryBeanN>

    @Query("SELECT * FROM goodscatrgorybeann WHERE :no is null or GoodsNO =(:no)  limit 1")
    fun loadSingleByNos(no:String): GoodsCatrgoryBeanN

//    @Query("SELECT * FROM goodscatrgorybeann WHERE gdctgId =(:Id) ")
//    fun loadAllById(Id:Long?): List<GoodsCatrgoryBeanN>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg beans: GoodsCatrgoryBeanN)

    @Delete
    fun delete(bean: GoodsCatrgoryBeanN)
}