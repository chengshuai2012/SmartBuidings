package com.aojiexun.smartbuilding.controller;

import android.os.Environment;

import com.aojiexun.smartbuilding.network.BaseEntity;
import com.aojiexun.smartbuilding.network.BaseObserver;
import com.aojiexun.smartbuilding.network.BaseService;
import com.aojiexun.smartbuilding.network.HttpConfig;
import com.aojiexun.smartbuilding.network.IOMainThread;
import com.aojiexun.smartbuilding.network.RetrofitFactory;
import com.aojiexun.smartbuilding.request.RequestReport;
import com.aojiexun.smartbuilding.request.RequestRfid;
import com.aojiexun.smartbuilding.request.RequsetProjectId;
import com.aojiexun.smartbuilding.response.AllFace;
import com.aojiexun.smartbuilding.response.CardIDBean;

import java.io.File;
import java.util.ArrayList;

import static com.aojiexun.smartbuilding.network.IOMainThread.ioMainDownload;

/**
 * Created by 49488 on 2018/12/14.
 */

public class FaceInController {
    FaceInControllerListener listener;

    public interface FaceInControllerListener {

        void onMainErrorCode(String msg, int errorCode);

        void onMainFail(Throwable e, boolean isNetWork);

        void FridSuccess(ArrayList<CardIDBean> fridBean);

        void getAllFaceSuccess(ArrayList<AllFace> allFaces);

        void reportSuccess(CardIDBean cardIDBean);

        void reportNotSuccessSuccess(CardIDBean cardIDBean);

        void onReportError();

        void onReportNotSuccessError();

        void onComplete();

    }

    private BaseService api;

    public FaceInController(FaceInControllerListener listener) {
        api = RetrofitFactory.getInstence().API();
        this.listener = listener;
    }

    public void downloadFile(String idCard,String url) {
        File file = new File(Environment.getExternalStorageDirectory()+"/faceFile");
        if(!file.exists()){
            file.mkdir();
        }
        File faceFile = new File(file.getAbsolutePath()+"/"+idCard+".data");
        if(faceFile.exists()){
            faceFile.delete();
        }
        api.getFaceFile(url).
                compose(ioMainDownload()).
                subscribeWith(new FileDownLoadSubscriber(faceFile){
                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        listener.onComplete();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        listener.onComplete();
                        System.gc();
                    }

                });

    }
    public void getRfid(String idCard) {
        RequestRfid requestRfid = new RequestRfid();
        requestRfid.setId_card(idCard);
        requestRfid.setProject_id(HttpConfig.id+"");
        api.getRfid(requestRfid)
                .compose(IOMainThread.composeIO2main())
                .subscribe(new BaseObserver<ArrayList<CardIDBean>>() {
                    @Override
                    protected void onSuccees(BaseEntity<ArrayList<CardIDBean>> t)  {
                        listener.FridSuccess(t.getData());
                    }

                    @Override
                    protected void onCodeError(String msg,int codeErrorr) {
                        listener.onMainErrorCode(msg,codeErrorr);
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError)  {
                        listener.onMainFail(e, isNetWorkError);
                    }
                });
    }
    public void getAllFace() {
        RequsetProjectId requsetProjectId = new RequsetProjectId();
        requsetProjectId.setMac_address(HttpConfig.MAC);

        api.getAllFace(requsetProjectId)
                .compose(IOMainThread.composeIO2main())
                .subscribe(new BaseObserver<ArrayList<AllFace>>() {
                    @Override
                    protected void onSuccees(BaseEntity<ArrayList<AllFace>> t)  {
                        listener.getAllFaceSuccess(t.getData());
                    }

                    @Override
                    protected void onCodeError(String msg,int codeErrorr) {
                        listener.onMainErrorCode(msg,codeErrorr);
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError)  {
                        listener.onMainFail(e, isNetWorkError);
                    }
                });
    }
    public void report(String idCard,String image,String date) {
        RequestReport requestReport = new RequestReport();
        requestReport.setMac_address(HttpConfig.MAC);
        requestReport.setId_card(idCard);
        requestReport.setTraffic_img(image);
        requestReport.setTraffic_direction(HttpConfig.derication);
        requestReport.setProject_id(HttpConfig.id+"");
        requestReport.setTraffic_time(date);
        api.report(requestReport)
                .compose(IOMainThread.composeIO2main())
                .subscribe(new BaseObserver<CardIDBean>() {
                    @Override
                    protected void onSuccees(BaseEntity<CardIDBean> t)  {
                        listener.reportSuccess(t.getData());
                    }

                    @Override
                    protected void onCodeError(String msg,int codeErrorr) {
                        listener.onReportError();
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError)  {
                        listener.onReportError();
                    }
                });
    }
    public void reportNotSuccess(String idCard,String image,String date) {
        RequestReport requestReport = new RequestReport();
        requestReport.setMac_address(HttpConfig.MAC);
        requestReport.setId_card(idCard);
        requestReport.setTraffic_img(image);
        requestReport.setTraffic_direction(HttpConfig.derication);
        requestReport.setProject_id(HttpConfig.id+"");
        requestReport.setTraffic_time(date);
        api.report(requestReport)
                .compose(IOMainThread.composeIO2main())
                .subscribe(new BaseObserver<CardIDBean>() {
                    @Override
                    protected void onSuccees(BaseEntity<CardIDBean> t)  {
                        listener.reportNotSuccessSuccess(t.getData());
                    }

                    @Override
                    protected void onCodeError(String msg,int codeErrorr) {
                        listener.onReportNotSuccessError();
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError)  {
                        listener.onReportNotSuccessError();
                    }
                });
    }


}
