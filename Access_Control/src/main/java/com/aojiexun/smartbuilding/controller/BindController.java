package com.aojiexun.smartbuilding.controller;

import android.os.Environment;
import android.util.Log;

import com.aojiexun.smartbuilding.network.BaseEntity;
import com.aojiexun.smartbuilding.network.BaseObserver;
import com.aojiexun.smartbuilding.network.BaseService;
import com.aojiexun.smartbuilding.network.HttpConfig;
import com.aojiexun.smartbuilding.network.IOMainThread;
import com.aojiexun.smartbuilding.network.RetrofitFactory;
import com.aojiexun.smartbuilding.request.CheckOutBean;
import com.aojiexun.smartbuilding.response.PersonBean;
import com.aojiexun.smartbuilding.response.ProjectID;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by 49488 on 2018/12/14.
 */

public class BindController {
    BindControllerListener listener;

    public interface BindControllerListener {

        void onMainErrorCode(String msg,int errorCode);

        void onMainFail(Throwable e, boolean isNetWork);

        void checkInSuccess(PersonBean personBean);

        void bindSuccess(PersonBean personBean);

    }

    private BaseService api;

    public BindController(BindControllerListener listener) {
        api = RetrofitFactory.getInstence().API();
        this.listener = listener;
    }

    public void check(String idCard) {
        CheckOutBean checkOutBean = new CheckOutBean();
        checkOutBean.setId_card(idCard);
        checkOutBean.setProject_id(HttpConfig.id+"");
        api.checkIs(checkOutBean)
                .compose(IOMainThread.<BaseEntity<PersonBean>>composeIO2main())
                .subscribe(new BaseObserver<PersonBean>() {
                    @Override
                    protected void onSuccees(BaseEntity<PersonBean> t)  {
                        listener.checkInSuccess(t.getData());
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

    public void BindFace(String idCard,String facePath,String data){

        RequestBody requestIdNo = RequestBody.create(MediaType.parse("multipart/form-data"), idCard);
        RequestBody requestPro = RequestBody.create(MediaType.parse("multipart/form-data"), HttpConfig.id+"");
        File file =new File(facePath);
        RequestBody requestImgFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part requestImgPart = MultipartBody.Part.createFormData("imgFile", file.getName(), requestImgFile);
        File dataFile =new File(data);
        RequestBody requestdataFile = RequestBody.create(MediaType.parse("multipart/form-data"), dataFile);
        MultipartBody.Part requestdataPart = MultipartBody.Part.createFormData("file", dataFile.getName(), requestdataFile);
        api.bindFace(requestIdNo,requestPro,requestImgPart,requestdataPart).compose(IOMainThread.composeIO2main()).subscribe( new BaseObserver<PersonBean>() {

            @Override
            protected void onSuccees(BaseEntity<PersonBean> t) {
                listener.bindSuccess(t.getData());
            }

            @Override
            protected void onCodeError(String msg, int codeErrorr) {
                listener.onMainErrorCode(msg,codeErrorr);
            }

            @Override
            protected void onFailure(Throwable e, boolean isNetWorkError) {
                listener.onMainFail(e, isNetWorkError);
            }
        });
    };
}
