package com.zcxie.zc.model_comm.net

class HttpConstant {
    companion object{
        const val BASE_URL = "http://pda.ymade.cn"
        //设备激活
        const val URL_PDA_RGS= "$BASE_URL/PDA_Device/Register"
        //设备资料
        const val URL_PDA_IDX= "$BASE_URL/PDA_Device/Index"
        //同步规则
        const val URL_PDA_Rule= "$BASE_URL/WET_Rule/List"
        //出库单上传
        const val URL_SCAN_LOT_UP= "$BASE_URL/PACK_Lot/Update"
    }
}