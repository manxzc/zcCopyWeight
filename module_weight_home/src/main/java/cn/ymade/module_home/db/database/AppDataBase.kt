package cn.ymade.module_home.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import cn.ymade.module_home.db.beans.ClientBean
import cn.ymade.module_home.db.beans.DevInfoBean
import cn.ymade.module_home.db.beans.GoodsBean
import cn.ymade.module_home.db.beans.LotDataBean
import cn.ymade.module_home.db.dao.ClientDao
import cn.ymade.module_home.db.dao.DevInfoDao
import cn.ymade.module_home.db.dao.GoodsDao
import cn.ymade.module_home.db.dao.LotDao

/**
 * @author zc.xie
 * @date 2021/6/1 0001.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
@Database(entities = [DevInfoBean::class,GoodsBean::class,ClientBean::class,LotDataBean::class], version = 1 , exportSchema = false)
abstract class AppDataBase :RoomDatabase(){
    abstract fun devinfoDao(): DevInfoDao
    abstract fun goodsDao(): GoodsDao
    abstract fun lotDao(): LotDao
    abstract fun clientDao(): ClientDao
}