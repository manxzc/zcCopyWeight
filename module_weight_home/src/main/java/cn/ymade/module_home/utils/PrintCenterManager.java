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
 * ?????????????????????
 *
 * @author zhouhongmin
 * @date 2019-06-18
 */
public class PrintCenterManager {

    private static volatile PrintCenterManager mInstance;
    private static final double IOS_deviation=0.4; //????????????ios??????

    Context mApplicationContext;
    private String lastImageUrl="";
    private File lastImageFile;

    private final LPAPI.Callback mCallback = new LPAPI.Callback() {

        /****************************************************************************************************************************************/
        // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

        /****************************************************************************************************************************************/

        // ?????????????????????????????????????????????
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

        // ?????????????????????????????????????????????
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
        // ?????????????????????????????????????????????
        @Override
        public void onPrintProgress(IDzPrinter.PrinterAddress address, Object bitmapData, IDzPrinter.PrintProgress progress, Object addiInfo) {
            Log.i("TAG", "onPrintProgress: "+progress.name()+" addiInfo "+addiInfo.toString());
            switch (progress) {
                case Success://????????????
                    LiveDataBus.get().with("printing").postValue(true);
                    break;
                case Failed://????????????
                    LiveDataBus.get().with("printing").postValue(false);
                    String error="?????????????????????";
                    if (IsPrinting.equals(addiInfo)) {
                        error="????????????????????????";
                    }else if (IsRotating.equals(addiInfo)) {
                        error="?????????????????????";
                    }else if (VolTooLow.equals(addiInfo)) {
                        error="?????????????????????";
                    }else if (VolTooHigh.equals(addiInfo)) {
                        error="?????????????????????";
                    }else if (TphNotFound.equals(addiInfo)) {
                        error="???????????????Tph";
                    }else if (TphTooHot.equals(addiInfo)) {
                        error="?????????Tph??????";
                    }else if (TphTooCold.equals(addiInfo)) {
                        error="?????????Tph??????";
                    }else if (TphOpened.equals(addiInfo)) {
                        error="?????????Tph?????????";
                    }else if (LabelCanOpend.equals(addiInfo)) {
                        error="????????????????????????";
                    }else if (CoverOpened.equals(addiInfo)) {
                        error="?????????????????????";
                    }else if (No_Paper.equals(addiInfo)) {
                        error="?????????????????????";
                    }else if (No_Ribbon.equals(addiInfo)) {
                        error="??????????????????";
                    }else if (Unmatched_Ribbon.equals(addiInfo)) {
                        error="?????????????????????";
                    }else if (Usedup_Ribbon.equals(addiInfo)) {
                        error="?????????????????????";
                    }else if (Usedup_Ribbon2.equals(addiInfo)) {
                        error="?????????????????????";
                    }else if (Cancelled.equals(addiInfo)) {
                        error="?????????????????????";
                    }else if (Disconnected.equals(addiInfo)) {
                        error="?????????????????????";
                    }else if (Timeout.equals(addiInfo)) {
                        error="???????????????";
                    }else if (Other.equals(addiInfo)) {
                        error="??????????????????????????????";
                    }
                    CommUtil.ToastU.showToast(error);
                    break;
                case StartCopy://?????????????????????
                    break;
                case DataEnded://??????????????????
                    break;
                default:
                    break;
            }
        }
    };
    private LPAPI api;
    // ?????????????????????????????????
    private IDzPrinter.PrinterAddress mPrinterAddress = null;
    private IDzPrinter.PrinterAddress connectIngPrint = null;

    private PrintCenterManager() {
        mApplicationContext = BaseApplication.Companion.getApplication();
        this.api = LPAPI.Factory.createInstance(mCallback);
        mPrinterAddress = PrinterData.getCurrentPrint();
        // ??????????????????????????????????????????
        if (mPrinterAddress != null) {
            if (api.openPrinterByAddress(mPrinterAddress)) {
                // ?????????????????????????????????????????????????????????
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

    // ??????????????????????????????????????????
    private void onPrinterConnecting(IDzPrinter.PrinterAddress printer, boolean showDialog) {
        CommUtil.ToastU.showToast("?????????????????????");
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

    // ?????????????????????????????????
    public boolean isPrinterConnected() {
        // ??????LPAPI?????????getPrinterState??????????????????????????????????????????
        IDzPrinter.PrinterState state = api.getPrinterState();

        // ??????????????????
        if (state == null || state.equals(IDzPrinter.PrinterState.Disconnected)) {
//            CommUtil.ToastU.showToast("?????????????????????????????????????????????");
            return false;
        }

        // ?????????????????????
        if (state.equals(IDzPrinter.PrinterState.Connecting)) {
//            CommUtil.ToastU.showToast("????????????????????????????????????");
            return false;
        }
        // ??????????????????
        return true;
    }

    private int fontStyle = 0;  //??????
    private double fontHeight = 0;  //??????
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
                String title="????????????";
                String subt1="?????????"+lotData.getLotNo();
                String subt2="?????????"+lotData.getLotName();
                String subt3="???????????????"+CommUtil.getCurrentTimeHM();
                String subt4="????????????"+lotData.getItems()+" ?????????"+lotData.getWeight()+" (kg)";
                List<DevInfoBean> devInfoBeans=  DataBaseManager.INSTANCE.getDb().devinfoDao().getAll();
                if (devInfoBeans != null&&devInfoBeans.size()>0) {
                    DevInfoBean  devInfoBean=devInfoBeans.get(0);
                    title=devInfoBean.getCompany()+"????????????";

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
//                        api.setItemOrientation(Integer.parseInt(textBean.getOrientation()));  //??????????????????
//                    if (!TextUtils.isEmpty(textBean.getHorizontalAlignment()))
//                        api.setItemHorizontalAlignment(Integer.parseInt(textBean.getHorizontalAlignment())); //????????????   ??????
//                    if (!TextUtils.isEmpty(textBean.getVerticalAlignment()))
//                        api.setItemVerticalAlignment(Integer.parseInt(textBean.getVerticalAlignment())); //????????????  ??????
////				????????????
//                    if (!TextUtils.isEmpty(textBean.getFontName()))   //????????????  ????????????????????????????????????????????????
//                        api.setDrawParam(FONT_NAME, textBean.getFontName());
//                    if (!TextUtils.isEmpty(textBean.getFontStyle()))   //????????????
//                        fontStyle = Integer.parseInt(textBean.getFontStyle());
//                    if (!TextUtils.isEmpty(textBean.getFontHeight()))    //?????????
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
//        //?????????
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
//        //??????
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
//        //???
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
//                                    Double.valueOf(imageBean.getHeight()), 257); //257 ?????? ??????????????????????????????
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
//                                    Double.valueOf(imageBean.getHeight()), 257); //257 ?????? ??????????????????????????????
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
//                                            Double.valueOf(imageBean.getHeight()), 257); //257 ?????? ??????????????????????????????
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
//                                CommUtil.ToastU.showToast("??????????????????");
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
