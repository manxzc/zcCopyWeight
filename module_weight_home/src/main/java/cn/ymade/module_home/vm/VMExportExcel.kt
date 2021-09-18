package cn.ymade.module_home.vm

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import cn.ymade.module_home.db.beans.LotDataBean
import cn.ymade.module_home.db.database.DataBaseManager
import cn.ymade.module_home.model.SummaryData
import cn.ymade.module_home.ui.ExportActivity
import com.zcxie.zc.model_comm.base.BaseViewModel
import com.zcxie.zc.model_comm.callbacks.CallBack
import com.zcxie.zc.model_comm.util.CommUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class VMExportExcel :BaseViewModel() {

    var search:String=""
    var exportList= mutableListOf<LotDataBean>()

    var act:ExportActivity?=null
    fun init(act: ExportActivity){
        this.act=act
    }

    fun search(key: String){
        if (search!=key) {
            search = key
            loadData()
        }
    }
    fun loadData(){

    }
    fun getNum(key: String, starTime: String, stopTime: String, callback: CallBack<SummaryData>){
        val df2: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        var d1: Date? = null
        var d2: Date? = null
        try {
            d1 = df2.parse(starTime)
            d2 = df2.parse(stopTime)

            d2!!.hours=23
            d2!!.minutes=59
            d2!!.seconds=59
            Log.i(TAG, "getNum: parse d1 " + d1 + " d2 " + d2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val d3 = (d2!!.time)/1000 - (d1!!.time)/1000
        val d4 = 60*60*24*31 //间隔日期

        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")

        var today= Date(CommUtil.getEndTime())
        val nowdayTime = dateFormat.format(today)
        val nowDate = dateFormat.parse(nowdayTime)
        nowDate!!.hours=23
        nowDate!!.minutes=59
        nowDate!!.seconds=59
        if ((d2!!.time)/1000 > (nowDate!!.time)/1000) {
            callback.callBack(null)
            CommUtil.ToastU.showToast("查询时间不能超过今天")
            return
        }
        if (d3 > d4) {
            callback.callBack(null)
            CommUtil.ToastU.showToast("查询时间不能超过30天")
            return
        }
        if (d3 < 0) {
            callback.callBack(null)
            CommUtil.ToastU.showToast("结束时间不能小于开始时间")
            return
        }

        Observable.create<SummaryData> {
            Log.i(TAG, "getNum: d1.time " + d1.time + " d2.time " + d2.time + " key " + key)
            exportList.clear()
            val lots = DataBaseManager.db.lotDao().loadAllInTimeAndKey(
                if (TextUtils.isEmpty(key)) null else key,
                d1.time,
                d2.time
            ) //所有数量
            exportList.addAll(lots)
            Log.i(TAG, "getNum: lots " + lots)
            var lotCount=lots.size
            var goodsCountByLot=0
            for (lot in lots){
                goodsCountByLot+=lot.items
            }

            var homeTitleData= SummaryData(lotCount, goodsCountByLot)
            it.onNext(homeTitleData)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback.callBack(it)
            }
    }

    fun export(){
        if (exportList.size<1){
            CommUtil.ToastU.showToast("没有单据可以导出~！")
        }else{
            act!!.showProgress("表格制作中..")
            Observable.create<String> {
                // 创建excel xlsx格式
                val wb: Workbook = SXSSFWorkbook()


                // 设置单元格样式:居中显示
                var cellStyle=wb.createCellStyle();
                cellStyle.alignment = CellStyle.ALIGN_CENTER;
                cellStyle.verticalAlignment = CellStyle.VERTICAL_CENTER;

                var creationHelper = wb.creationHelper

                for (i in 0 until  exportList.size){
                    var lot=exportList[i]
                    // 创建工作表
                    val sheet = wb.createSheet()
                    sheet.setColumnWidth(0, 256*70 );
                    sheet.setColumnWidth(1, 256*20 );
                    sheet.setColumnWidth(2, 256*20 );
                    sheet.setColumnWidth(3, 256*20 );
                    sheet.setColumnWidth(4, 256*20 );
                    sheet.setColumnWidth(5, 256*20 );
                    for ( title in 0 until 3) {
                        var row = sheet.createRow(title);
                        var cell = row.createCell(0);
                        cell.cellStyle = cellStyle;
                        when (title) {
                            0 -> cell.setCellValue(lot.lotName)
                            1 -> cell.setCellValue(lot.lotNo)
                            2 -> cell.setCellValue(lot.weight.toString())
                        };
                    }
                    var row = sheet.createRow(3);
                    for ( subt in 0 until 6){
                        var cell = row.createCell(subt);
                        cell.cellStyle = cellStyle;
                        when (subt) {
                            0 -> cell.setCellValue("GoodsName")
                            1 -> cell.setCellValue("weight")
                            2 -> cell.setCellValue("createDate")
                            3 -> cell.setCellValue("shelflife")
                            4 -> cell.setCellValue("批号")
                            5 -> cell.setCellValue("货号")
                        }
                    }
                   var gds= DataBaseManager.db.goodsDao().loadAllByLotId(lot.lotId)
                    for (gdrow in gds.indices) {
                        var gd=gds[gdrow]
                        // 创建单元格
                        var row = sheet.createRow(gdrow+4);
                        for ( subt in 0 until 6){
                            var cell = row.createCell(subt);
                            cell.cellStyle = cellStyle;
                            when (subt) {
                                0 -> cell.setCellValue(gd.GoodsName+"")
                                1 -> cell.setCellValue(gd.weight.toString())
                                2 -> cell.setCellValue(gd.createDate+"")
                                3 -> cell.setCellValue(gd.shelflife+"")
                                4 -> cell.setCellValue(gd.lotNumber+"")
                                5 -> cell.setCellValue(gd.GoodsNO+"")
                            }
                        }
                    }

                }

// 生成excel表格
                var path=Environment.getExternalStorageDirectory().absolutePath+"/copyWeight"+CommUtil.getCurrentTime2File()+".xlsx"
                var fos =  FileOutputStream(path)
                wb.write(fos);
                fos.flush();

                it.onNext(path)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                   act!!.hideProgress()
                    CommUtil.ToastU.showToast("已完成导出路径 "+it)
                }
        }

    }
}