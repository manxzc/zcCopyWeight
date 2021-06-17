package cn.ymade.module_home.db.dao

import androidx.room.*
import cn.ymade.module_home.db.beans.ClientBean
import cn.ymade.module_home.db.beans.LotDataBean

@Dao
interface ClientDao {
    @Query("SELECT * FROM clientbean order by clientName desc")
    fun getAll(): List<ClientBean>

    @Query("SELECT * FROM clientbean WHERE :clientName isnull or  clientName like '%'||:clientName||'%' order by clientName desc")
    fun loadAllByname(clientName:String?): List<ClientBean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg beans: ClientBean)

    @Delete
    fun delete(bean: ClientBean)
}