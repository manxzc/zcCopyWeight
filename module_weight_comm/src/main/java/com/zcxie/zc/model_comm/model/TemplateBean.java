package com.zcxie.zc.model_comm.model;

import java.io.Serializable;

public class TemplateBean implements Serializable {

    /**
     * TemplateID : 488303dd-4396-4883-9735-e7dd1814f5a7
     * TemplateName : 固资5025
     * Width : 50
     * Height : 25
     * Orientation : 0
     * ViewHtml : <div id="488303dd-4396-4883-9735-e7dd1814f5a7"onclick="selectTempateClick(this)"class="      w-100 m-auto "><div class=" py-1 text-center">固资5025</div><table class="pString-table bg-white text-black"style="height: 1.75rem;width: 4rem;"><tbody><tr><td colspan="2"class="text-center">$CompanyName$</td></tr><tr><td colspan="2">资产名称<span id="AssetName">$AssetName$<span></span></span></td></tr><tr><td>资产编码<span id="AssetNo">$AssetNo$<span></span></span></td><td rowspan="2"style="width: 0.75rem;"><div style="width: 0.75rem; height: 0.875rem;"><img class="w-100 h-100"src="../img/saoprString.png"></div></td></tr><tr><td>使用人员<span id="Staff">$Staff$<span></span></span></td></tr></tbody></table></div>
     * Views : 5025.png
     * PrStringArray : {"Text":[{"orientation":"0","x":"3","y":"3","width":"46","height":"6","content":"$CompanyName$","horizontalAlignment":"1","verticalAlignment":"1","fontName":"黑体","fontHeight":"2.5","fontStyle":"0"},{"x":"3","y":"9","width":"28","height":"5","content":"名称：$AssetName$","fontHeight":"2"},{"x":"3","y":"14.5","width":"28","height":"5","content":"型号：$AssetModel$"},{"x":"3","y":"20","width":"28","height":"5","content":"购置：$PurchaseDate$"}],"QRCode":[{"x":"32.8","y":"9","width":"13","content":"$AssetNo$"}],"Line":[{"x1":"2","y1":"7.5","x2":"48","y2":"7.5","lineWidth":"0.5"},{"x1":"2","y1":"13","x2":"30","y2":"13","lineWidth":"0.5"},{"x1":"2","y1":"18.5","x2":"30","y2":"18.5","lineWidth":"0.5"},{"x1":"30","y1":"7.5","x2":"30","y2":"24","lineWidth":"0.5"}],"Rectangle":[{"x":"2","y":"1","width":"46","height":"23","lineWidth":"0.5"}]}
     * ShowIndex : 1
     * Stamp : null
     * Status : 0
     */

    private String TemplateID;
    private String TemplateName;
    private double Width;
    private double Height;
    private int Orientation;
    private String ViewHtml;
    private String Views;
    private String PrintArray;
    private String ShowIndex;
    private String Stamp;
    private String Status;

    public String getPrintArray() {
        return PrintArray;
    }

    public void setPrintArray(String printArray) {
        PrintArray = printArray;
    }

    public String getTemplateID() {
        return TemplateID;
    }

    public void setTemplateID(String TemplateID) {
        this.TemplateID = TemplateID;
    }

    public String getTemplateName() {
        return TemplateName;
    }

    public void setTemplateName(String TemplateName) {
        this.TemplateName = TemplateName;
    }

    public double getWidth() {
        return Width;
    }

    public void setWidth(double width) {
        Width = width;
    }

    public double getHeight() {
        return Height;
    }

    public void setHeight(double height) {
        Height = height;
    }

    public int getOrientation() {
        return Orientation;
    }

    public void setOrientation(int orientation) {
        Orientation = orientation;
    }

    public String getViewHtml() {
        return ViewHtml;
    }

    public void setViewHtml(String ViewHtml) {
        this.ViewHtml = ViewHtml;
    }

    public String getViews() {
        return Views;
    }

    public void setViews(String Views) {
        this.Views = Views;
    }



    public String getShowIndex() {
        return ShowIndex;
    }

    public void setShowIndex(String ShowIndex) {
        this.ShowIndex = ShowIndex;
    }

    public String getStamp() {
        return Stamp;
    }

    public void setStamp(String Stamp) {
        this.Stamp = Stamp;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }
}
