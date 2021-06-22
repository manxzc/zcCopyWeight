package cn.ymade.module_home.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.dothantech.lpapi.LPAPI;
import com.dothantech.printer.IDzPrinter;
import com.zcxie.zc.model_comm.base.BaseApplication;
import com.zcxie.zc.model_comm.callbacks.CallBack;
import com.zcxie.zc.model_comm.model.PrinterData;
import com.zcxie.zc.model_comm.model.TemplateBean;
import com.zcxie.zc.model_comm.util.CommUtil;
import com.zcxie.zc.model_comm.util.LiveDataBus;

import java.io.File;
import java.util.List;

import cn.ymade.module_home.db.beans.DevInfoBean;
import cn.ymade.module_home.db.beans.GoodsBean;
import cn.ymade.module_home.db.beans.LotDataBean;
import cn.ymade.module_home.db.database.DataBaseManager;

import static com.dothantech.printer.IDzPrinter.PrintFailReason.Cancelled;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.CoverOpened;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.Disconnected;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.IsPrinting;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.IsRotating;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.LabelCanOpend;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.No_Paper;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.No_Ribbon;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.Other;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.Timeout;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.TphNotFound;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.TphOpened;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.TphTooCold;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.TphTooHot;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.Unmatched_Ribbon;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.Usedup_Ribbon;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.Usedup_Ribbon2;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.VolTooHigh;
import static com.dothantech.printer.IDzPrinter.PrintFailReason.VolTooLow;

/**
 * 打印中心管理类
 *
 * @author zhouhongmin
 * @date 2019-06-18
 */
public class PrintCenterManager {

    private static volatile PrintCenterManager mInstance;
    private static final double IOS_deviation=0.4; //二维码与ios偏差

    Context mApplicationContext;
    private String lastImageUrl="";
    private File lastImageFile;

    private final LPAPI.Callback mCallback = new LPAPI.Callback() {

        /****************************************************************************************************************************************/
        // 所有回调函数都是在打印线程中被调用，因此如果需要刷新界面，需要发送消息给界面主线程，以避免互斥等繁琐操作。

        /****************************************************************************************************************************************/

        // 打印机连接状态发生变化时被调用
        @Override
        public void onStateChange(IDzPrinter.PrinterAddress arg0, IDzPrinter.PrinterState arg1) {
            final IDzPrinter.PrinterAddress printer = arg0;
            switch (arg1) {
                case Connected:
                case Connected2:
                    if (null != connectIngPrint) {
                        mPrinterAddress = connectIngPrint;
                    }
                    PrinterData.saveCurrentPrint(mPrinterAddress);
                    LiveDataBus.get().with("printConnect").postValue(true);
                    break;

                case Disconnected:
                    LiveDataBus.get().with("printConnect").postValue(false);
                    break;

                default:
                    break;
            }
        }

        // 蓝牙适配器状态发生变化时被调用
        @Override
        public void onProgressInfo(IDzPrinter.ProgressInfo arg0, Object arg1) {
        }

        @Override
        public void onPrinterDiscovery(IDzPrinter.PrinterAddress arg0, IDzPrinter.PrinterInfo arg1) {
        }
//        IsPrinting,
//        IsRotating,
//        VolTooLow,
//        VolTooHigh,
//        TphNotFound,
//        TphTooHot,
//        TphTooCold,
//        TphOpened,
//        LabelCanOpend,
//        CoverOpened,
//        No_Paper,
//        No_Ribbon,
//        Unmatched_Ribbon,
//        Usedup_Ribbon,
//        Usedup_Ribbon2,
//        Cancelled,
//        Disconnected,
//        Timeout,
//        Other;
        // 打印标签的进度发生变化是被调用
        @Override
        public void onPrintProgress(IDzPrinter.PrinterAddress address, Object bitmapData, IDzPrinter.PrintProgress progress, Object addiInfo) {
            Log.i("TAG", "onPrintProgress: "+progress.name()+" addiInfo "+addiInfo.toString());
            switch (progress) {
                case Success://成功结束
                    LiveDataBus.get().with("printing").postValue(true);
                    break;
                case Failed://打印失败
                    LiveDataBus.get().with("printing").postValue(false);
                    String error="打印机打印失败";
                    if (IsPrinting.equals(addiInfo)) {
                        error="打印机正在打印中";
                    }else if (IsRotating.equals(addiInfo)) {
                        error="打印机正在旋转";
                    }else if (VolTooLow.equals(addiInfo)) {
                        error="打印机电压过低";
                    }else if (VolTooHigh.equals(addiInfo)) {
                        error="打印机电压过高";
                    }else if (TphNotFound.equals(addiInfo)) {
                        error="打印机没有Tph";
                    }else if (TphTooHot.equals(addiInfo)) {
                        error="打印机Tph过热";
                    }else if (TphTooCold.equals(addiInfo)) {
                        error="打印机Tph过冷";
                    }else if (TphOpened.equals(addiInfo)) {
                        error="打印机Tph被打开";
                    }else if (LabelCanOpend.equals(addiInfo)) {
                        error="打印机标签未关闭";
                    }else if (CoverOpened.equals(addiInfo)) {
                        error="打印机盖被打开";
                    }else if (No_Paper.equals(addiInfo)) {
                        error="打印机没有纸！";
                    }else if (No_Ribbon.equals(addiInfo)) {
                        error="打印没有色带";
                    }else if (Unmatched_Ribbon.equals(addiInfo)) {
                        error="打印机正在工作";
                    }else if (Usedup_Ribbon.equals(addiInfo)) {
                        error="打印色带已废旧";
                    }else if (Usedup_Ribbon2.equals(addiInfo)) {
                        error="打印色带已废旧";
                    }else if (Cancelled.equals(addiInfo)) {
                        error="打印机取消打印";
                    }else if (Disconnected.equals(addiInfo)) {
                        error="打印机断开连接";
                    }else if (Timeout.equals(addiInfo)) {
                        error="打印机超时";
                    }else if (Other.equals(addiInfo)) {
                        error="其他错误导致打印失败";
                    }
                    CommUtil.ToastU.showToast(error);
                    break;
                case StartCopy://打印机开始打印
                    break;
                case DataEnded://数据传输结束
                    break;
                default:
                    break;
            }
        }
    };
    private LPAPI api;
    // 上次连接成功的设备对象
    private IDzPrinter.PrinterAddress mPrinterAddress = null;
    private IDzPrinter.PrinterAddress connectIngPrint = null;

    private PrintCenterManager() {
        mApplicationContext = BaseApplication.Companion.getApplication();
        this.api = LPAPI.Factory.createInstance(mCallback);
        mPrinterAddress = PrinterData.getCurrentPrint();
        // 尝试连接上次成功连接的打印机
        if (mPrinterAddress != null) {
            if (api.openPrinterByAddress(mPrinterAddress)) {
                // 连接打印机的请求提交成功，刷新界面提示
                onPrinterConnecting(mPrinterAddress, false);
                return;
            }
        }
    }

    public static PrintCenterManager getInstance() {
        if (mInstance == null) {
            synchronized (PrintCenterManager.class) {
                if (mInstance == null) {
                    mInstance = new PrintCenterManager();
                }
            }
        }
        return mInstance;
    }

    public void connectingPrint(IDzPrinter.PrinterAddress printer, CallBack callBack) {
        if (api.openPrinterByAddress(printer)) {
            this.connectIngPrint = printer;
            callBack.callBack(null);
            return;
        }
    }

    // 连接打印机请求成功提交时操作
    private void onPrinterConnecting(IDzPrinter.PrinterAddress printer, boolean showDialog) {
        CommUtil.ToastU.showToast("打印机连接成功");
    }

    public IDzPrinter.PrinterAddress getCurrentPrint() {
        return mPrinterAddress;
    }

    public void setmCurrentPrint(IDzPrinter.PrinterAddress mPrinterAddress) {
        this.mPrinterAddress = mPrinterAddress;
    }

    public List<IDzPrinter.PrinterAddress> getAllPrint() {
        return api.getAllPrinterAddresses(null);
    }

    // 判断当前打印机是否连接
    public boolean isPrinterConnected() {
        // 调用LPAPI对象的getPrinterState方法获取当前打印机的连接状态
        IDzPrinter.PrinterState state = api.getPrinterState();

        // 打印机未连接
        if (state == null || state.equals(IDzPrinter.PrinterState.Disconnected)) {
//            CommUtil.ToastU.showToast("打印机未连接，请先连接打印机！");
            return false;
        }

        // 打印机正在连接
        if (state.equals(IDzPrinter.PrinterState.Connecting)) {
//            CommUtil.ToastU.showToast("正在连接打印机，请稍候！");
            return false;
        }
        // 打印机已连接
        return true;
    }

    private int fontStyle = 0;  //字体
    private double fontHeight = 0;  //字体
    private boolean loadimage=false;
    public Bitmap prnit(TemplateBean prnitBean, boolean isPrint){
        Log.i("TAG", "prnit: ");
        return null;
    }

    public void printLot(LotDataBean lotData, List<GoodsBean> goodsBeans){

        Log.i("TAG", "printLot: lotData "+lotData.getLotNo());
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("TAG", "run: printLot goodsBeans.size() "+goodsBeans.size()+" goodsBeans.size()/4 "+goodsBeans.size()/4);
                int h=34+(goodsBeans.size()/4+1)*4;
                Log.i("TAG", "run: printLot total h "+h);
                api.startJob(70,h,0);
                String title="出库清单";
                String subt1="货品："+lotData.getLotNo();
                String subt2="客户："+lotData.getLotName();
                String subt3="打印时间："+CommUtil.getCurrentTimeHM();
                String subt4="总件数："+lotData.getItems()+" 总重："+lotData.getWeight()+" (kg)";
                List<DevInfoBean> devInfoBeans=  DataBaseManager.INSTANCE.getDb().devinfoDao().getAll();
                if (devInfoBeans != null&&devInfoBeans.size()>0) {
                    DevInfoBean  devInfoBean=devInfoBeans.get(0);
                    title=devInfoBean.getCompany()+"出库清单";

                }
                api.drawTextRegular(title, 3 , 3, 70, 6
                        , 5, LPAPI.FontStyle.BOLD, 0);

                api.drawTextRegular(subt1, 3 , 9, 70, 5
                        , 4, LPAPI.FontStyle.BOLD, 0);

                api.drawTextRegular(subt2, 3 , 14, 70, 5
                        , 4, LPAPI.FontStyle.BOLD, 0);
                api.drawTextRegular(subt3, 3 , 19, 70, 5
                        , 4, LPAPI.FontStyle.BOLD, 0);
                api.drawTextRegular(subt4, 3 , 24, 70,5
                        , 4, LPAPI.FontStyle.BOLD, 0);

                api.drawTextRegular("===================", 3 , 29, 70, 5
                        , 4, LPAPI.FontStyle.BOLD, 0);

                int tempH=30;
                int startW=3;
                int steup=70/4;
                int tempLine=0;
//                int HorizontalIndex=0;
                for ( int i=0;i<goodsBeans.size();i++){
                    if (i%4==0){
                        tempLine+=1;
                    }
//                    if (HorizontalIndex>3)
//                        HorizontalIndex=0;
                    Log.i("TAG", "run: printLot tempH+tempLine*4 "+(tempH+tempLine*4));
                    api.drawTextRegular(goodsBeans.get(i).getWeight()+"", 3+i%4*steup , tempH+tempLine*4, steup, 4
                            , 3, LPAPI.FontStyle.BOLD, 0);

                }

//                api.endJob();
                Bundle param = new Bundle();
                param.putInt(LPAPI.PrintParamName.BOTTOM_MARGIN_PX,10);
                api.commitJobWithParam(param);
                Log.i("TAG", "run: printLot  api.commitJob() ");  ;
            }
        }).start();
    }
//    public Bitmap prnit(TemplateBean prnitBean, boolean isPrint) {
//        if (null == prnitBean) return null;
//        api.startJob(prnitBean.getWidth(), prnitBean.getHeight(), prnitBean.getOrientation());
//        int marginLeft=Sputils.getSpInt(CrossApp.get(),"marginLeft",0);
//        Log.i("sss", "prnit: marginLeft= "+marginLeft);
////        api.setDrawParam(LPAPI.PrintParamName.HORIZONTAL_OFFSET_01MM,i);
////        Bundle param = new Bundle();
////        param.putInt(LPAPI.PrintParamName.HORIZONTAL_OFFSET_01MM,i);
//
//        if (TextUtils.isEmpty(prnitBean.getPrintArray())) return null;
//        PrintContentBean printContent = JSON.parseObject(prnitBean.getPrintArray(), PrintContentBean.class);
//
//        List<PrintContentBean.TextBean> texts = printContent.getText();
//        if (printContent.getText() != null) {
//            for (PrintContentBean.TextBean textBean : texts) {
//                if (textBean != null) {
//                    if (!TextUtils.isEmpty(textBean.getOrientation()))
//                        api.setItemOrientation(Integer.parseInt(textBean.getOrientation()));  //设置旋转角度
//                    if (!TextUtils.isEmpty(textBean.getHorizontalAlignment()))
//                        api.setItemHorizontalAlignment(Integer.parseInt(textBean.getHorizontalAlignment())); //对齐方式   水平
//                    if (!TextUtils.isEmpty(textBean.getVerticalAlignment()))
//                        api.setItemVerticalAlignment(Integer.parseInt(textBean.getVerticalAlignment())); //对齐方式  垂直
////				绘制文字
//                    if (!TextUtils.isEmpty(textBean.getFontName()))   //设置字体  如果没有打印机会使用上一次设置的
//                        api.setDrawParam(FONT_NAME, textBean.getFontName());
//                    if (!TextUtils.isEmpty(textBean.getFontStyle()))   //字体风格
//                        fontStyle = Integer.parseInt(textBean.getFontStyle());
//                    if (!TextUtils.isEmpty(textBean.getFontHeight()))    //字体高
//                        fontHeight = Double.parseDouble(textBean.getFontHeight());
//
//                    double y = Double.parseDouble(textBean.getY());
//                    double hi = Double.parseDouble(textBean.getHeight());
//
//                    Log.i("TAG", "prnit: " + textBean.getContent());
//                    api.drawTextRegular(textBean.getContent(), Double.parseDouble(textBean.getX()) + marginLeft, y
//                            , Double.parseDouble(textBean.getWidth()), hi
//                            , fontHeight,
//                            fontStyle, 0);
//                }
//            }
//        }
//        //二维码
//        List<PrintContentBean.QRCodeBean> qrcodes = printContent.getQRCode();
//        Log.i("TAG", "prnit: qrcodes "+qrcodes.size());
//        if (qrcodes != null) {
//            for (PrintContentBean.QRCodeBean qrcodeBean : qrcodes) {
//                if (qrcodeBean != null) {
//                    Log.i("TAG", "prnit: qrcodes getContent" + qrcodeBean.getContent());
//                    api.draw2DQRCode(qrcodeBean.getContent(), Double.valueOf(qrcodeBean.getX())-IOS_deviation + marginLeft,
//                            Double.valueOf(qrcodeBean.getY()), Double.valueOf(qrcodeBean.getWidth()));
//                }
//            }
//        }
//        //直线
//        List<PrintContentBean.LineBean> lines = printContent.getLine();
//        if (lines != null) {
//            for (PrintContentBean.LineBean lineBean : lines) {
//                if (lineBean != null) {
//                    Log.i("TAG", "prnit: qrcodes lineBean" + lineBean.getX1());
//                    double y1 = Double.valueOf(lineBean.getY1());
//                    double y2 = Double.valueOf(lineBean.getY2());
//                    double x1 = Double.valueOf(lineBean.getX1()) + marginLeft;
//                    double x2 = Double.valueOf(lineBean.getX2()) + marginLeft;
//                    double lineW = Double.valueOf(lineBean.getLineWidth());
////                if (y1==y2)
////                api.drawLine(x1, y1, x2-lineW/2, y2, lineW);
////                else if (x1==x2){
////                    api.drawLine(x1, y1-lineW/2, x2,y2-lineW/3, lineW);
////                }else {
////                    api.drawLine(x1, y1, x2,y2, lineW);
////                }
//                    double startx = x1;
//                    double starty = y1;
//                    double x = x2;
//                    double y = y2;
//                    double wd = x - startx;
//                    double hd = y - starty;
//                    if (y1 == y2)
//                        api.drawRectangle(startx, starty, wd - lineW / 2, hd, lineW / 2);
//                    else if (x1 == x2) {
//                        api.drawRectangle(startx - lineW / 2, starty, wd, hd, lineW / 2);
//                    } else {
//                        api.drawRectangle(startx, starty, wd, hd, lineW / 2);
//                    }
//                }
//            }
//        }
//        //框
//        List<PrintContentBean.RectangleBean> rectangles = printContent.getRectangle();
//        if (rectangles != null) {
//            for (PrintContentBean.RectangleBean rectangleBean : rectangles) {
//                if (rectangleBean!=null) {
//                    Log.i("TAG", "prnit: qrcodes rectangleBean" + rectangleBean.getX());
//                    api.drawRectangle(Double.valueOf(rectangleBean.getX()) + marginLeft, Double.valueOf(rectangleBean.getY()),
//                            Double.valueOf(rectangleBean.getWidth()), Double.valueOf(rectangleBean.getHeight())
//                            , Double.valueOf(rectangleBean.getLineWidth()));
//                }
//            }
//        }
//
//        //image
//        List<PrintContentBean.ImageBean>imageBeans=printContent.getImage();
//        loadimage=false;
//        if (imageBeans!=null){
//          String cachePath=  Sputils.getSpString(CrossApp.get(),"logopath","");
//
//            for (PrintContentBean.ImageBean imageBean:imageBeans){
//                if (imageBean!=null) {
//                    loadimage=true;
//                    Log.i("TAG", "prnit: imageBeans " + imageBean.getBitmap() + " lastImageUrl " + lastImageUrl + " lastImageFile " +
//                            (lastImageFile == null ? "null" : lastImageFile.exists() ? lastImageFile.getAbsolutePath() : " noExists "));
//                    File cacheFile=new File(cachePath);
//                    if (!TextUtils.isEmpty(cachePath) && (cacheFile.exists())) {
//                        loadimage=false;
//                        Log.i("TAG", "prnit: requestLogo cache file " + cachePath);
//                        try {
//                            api.drawBitmapWithThreshold(BitmapFactory.decodeStream(new FileInputStream(cacheFile)), Double.valueOf(imageBean.getX()),
//                                    Double.valueOf(imageBean.getY()), Double.valueOf(imageBean.getWidth()),
//                                    Double.valueOf(imageBean.getHeight()), 257); //257 原图 字段解析打印会出问题
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                            Log.i("TAG", "prnit: cacheFile FileNotFoundException");
//                        }
//
//                    } else if (lastImageFile != null && lastImageUrl.equals(imageBean.getBitmap()) && !TextUtils.isEmpty(lastImageUrl)
//                            && lastImageFile.exists()) {
//                        loadimage = false;
//                        try {
//                            api.drawBitmapWithThreshold(BitmapFactory.decodeStream(new FileInputStream(lastImageFile)), Double.valueOf(imageBean.getX()),
//                                    Double.valueOf(imageBean.getY()), Double.valueOf(imageBean.getWidth()),
//                                    Double.valueOf(imageBean.getHeight()), 257); //257 原图 字段解析打印会出问题
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                            Log.i("TAG", "prnit: lastImageFile FileNotFoundException");
//                        }
//                    } else {
//                        OkGo.<File>get(imageBean.getBitmap()).tag(mInstance).execute(new FileCallback() {
//                            @Override
//                            public void onSuccess(Response<File> response) {
//                                lastImageFile = response.body();
//                                Log.i("TAG", "onSuccess: imageBeans body " + response.body().exists());
//                                FileInputStream fileInputStream = null;
//                                try {
//                                    fileInputStream = new FileInputStream(response.body());
//                                } catch (FileNotFoundException e) {
//                                    e.printStackTrace();
//                                }
//                                if (fileInputStream != null) {
//                                    api.drawBitmapWithThreshold(BitmapFactory.decodeStream(fileInputStream), Double.valueOf(imageBean.getX()),
//                                            Double.valueOf(imageBean.getY()), Double.valueOf(imageBean.getWidth()),
//                                            Double.valueOf(imageBean.getHeight()), 257); //257 原图 字段解析打印会出问题
////                            api.drawBitmapStream(fileInputStream, Double.valueOf(imageBean.getX()),
////                                    Double.valueOf(imageBean.getY()), Double.valueOf(imageBean.getWidth()),
////                                    Double.valueOf(imageBean.getHeight()));
//                                }
//                                loadimage = false;
//                            }
//
//                            @Override
//                            public void onError(Response<File> response) {
//                                super.onError(response);
//                                CommUtil.ToastU.showToast("图片下载失败");
//                                loadimage = false;
//                            }
//                        });
//                    }
//                    lastImageUrl = imageBean.getBitmap();
//                }
//            }
//        }
//        while (loadimage){
//
//        }
//        api.endJob();
//
//        if (isPrint) {
//         api.commitJob();
//        }
//
//         return api.getJobPages().get(0);
//
//    }

    public List<Bitmap> getPages() {
        return api.getJobPages();
    }
}
