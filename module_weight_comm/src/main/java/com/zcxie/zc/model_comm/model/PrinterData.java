package com.zcxie.zc.model_comm.model;

import android.text.TextUtils;

import com.dothantech.printer.IDzPrinter;
import com.zcxie.zc.model_comm.util.AppConfig;

public class PrinterData {
    public static IDzPrinter.PrinterAddress getCurrentPrint() {
        IDzPrinter.PrinterAddress printerAddress = null;
        if (TextUtils.isEmpty(AppConfig.printMac.get()) || TextUtils.isEmpty(AppConfig.printName.get()) || TextUtils.isEmpty(AppConfig.printType.get()))
            return null;
        IDzPrinter.AddressType lastAddressType = TextUtils.isEmpty(AppConfig.printType.get()) ? null : Enum.valueOf(IDzPrinter.AddressType.class, AppConfig.printType.get());
        printerAddress = new IDzPrinter.PrinterAddress(AppConfig.printName.get(), AppConfig.printMac.get(), lastAddressType);
        return printerAddress;
    }

    public static void saveCurrentPrint(IDzPrinter.PrinterAddress printerAddress) {
        AppConfig.printMac.put(printerAddress.macAddress);
        AppConfig.printName.put(printerAddress.shownName);
        AppConfig.printType.put(printerAddress.addressType.toString());
    }
}
