package cn.ymade.module_home.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import cn.ymade.module_home.db.beans.*
import cn.ymade.module_home.db.dao.*

/**
 * @author zc.xie
 * @date 2021/6/1 0001.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
@Database(entities = [DevInfoBean::class,GoodsBean::class,ClientBean::class,LotDataBean::class, GoodsCatrgoryBeanN::class], version = 1 , exportSchema = false)
abstract class AppDataBase :RoomDatabase(){
    abstract fun devinfoDao(): DevInfoDao
    abstract fun goodsDao(): GoodsDao
    abstract fun lotDao(): LotDao
    abstract fun clientDao(): ClientDao
    abstract fun goodsCategoryDao(): GoodsCategoryDao
}