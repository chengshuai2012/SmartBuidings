package com.aojiexun.smartbuilding.network;
import com.aojiexun.smartbuilding.request.CheckOutBean;
import com.aojiexun.smartbuilding.request.RequestReport;
import com.aojiexun.smartbuilding.request.RequestRfid;
import com.aojiexun.smartbuilding.request.RequsetProjectId;
import com.aojiexun.smartbuilding.response.AllFace;
import com.aojiexun.smartbuilding.response.CardIDBean;
import com.aojiexun.smartbuilding.response.PersonBean;
import com.aojiexun.smartbuilding.response.ProjectID;

import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;


/**
 * Created by OFX002 on 2018/10/28.
 */

public interface BaseService {
    @POST(ApiConstants.PROJCETID)
    Observable<BaseEntity<ProjectID>> getProjectId(@Body RequsetProjectId requsetProjectId);

    @POST(ApiConstants.CHECKOUT)
    Observable<BaseEntity<PersonBean>> checkIs(@Body CheckOutBean checkOutBean);

    @Multipart
    @POST(ApiConstants.BINDFACE)
    Observable<BaseEntity<PersonBean>> bindFace(@Part("id_card") RequestBody id_card,
                                            @Part("project_id")RequestBody project_id,
                                            @Part() MultipartBody.Part file,
                                            @Part() MultipartBody.Part imgFile
    );


    @Streaming
    @Headers("Content-Type:application/force-download")
    @GET(ApiConstants.DOWNLOAD)
    Flowable<ResponseBody> getFaceFile(@Path("dowload") String url);


    @POST(ApiConstants.CARDLIST)
    Observable<BaseEntity<ArrayList<CardIDBean>>> getRfid(@Body RequestRfid requestRfid);

    @POST(ApiConstants.ALLFACE)
    Observable<BaseEntity<ArrayList<AllFace>>> getAllFace(@Body RequsetProjectId requsetProjectId);

    @POST(ApiConstants.REPORT)
    Observable<BaseEntity<CardIDBean>> report(@Body RequestReport requestReport);
//
//
//    /**
//     * 获取用户
//     */
//    @POST(ApiConstants.GETUSERS)
//    Observable<BaseEntity<BindUser>> getUser(@Body RequestBindFinger requestBindFinger);
//
//
//
//    /**
//     * 获取单个用户指静脉
//     */
//    @GET(ApiConstants.GETSINGLEUSER)
//    @Headers("Content-Type:application/json;charset=utf-8")
//    Observable<BaseEntity<SingleUser>> findOneUserFinger(@Path("uuid") String uuid);
//
//
//    /**
//     * 开门
//     */
//    @POST(ApiConstants.CHECKIN)
//    Observable<BaseEntity<CheckInBean>> checkIn(@Path("type") int type, @Body CheckInRequest checkInRequest);/**
//     * 开门日志
//     */
//    @POST(ApiConstants.QROPENDOORLOG)
//    Observable<BaseEntity<CheckInBean>> checkInLog(@Body CheckInLogRequest checkInRequest);
//  /**
//     * 密码
//     */
//    @POST(ApiConstants.VALIDATEPASS)
//    Observable<BaseEntity<PasswordBean>> validatePass(@Path("password") String password);
//
//    /**
//     * 二维码开门
//     */
//    @POST(ApiConstants.QROPENDOOR)
//    Observable<BaseEntity<CodeInBean>> openDoorByQr(@Body CheckInByOther qr);
//
//    /**
//     * 获取单独人脸
//     */
//    @POST(ApiConstants.GETSINGLEPERSONFACE)
//    Observable<BaseEntity<CodeInBean>> getSingleFace(@Body RequestSingleFace requestSingleFace);
//    /**
//     * 验证人脸
//     */
//    @Multipart
//    @POST(ApiConstants.IDENTIFYFACE)
//    @Headers("ReQuest:YuanGu")
//    Observable<YuanGuMessage> CheckInYuangu(@Part("deviceNo") RequestBody deviceNo,
//                                            @Part("mac") RequestBody mac,
//                                            @Part() MultipartBody.Part files);
//    /**
//     * APP版本
//     */
//    @GET(ApiConstants.APPVERSION)
//    Observable<BaseEntity<APPVersionBean>> getAppVersion(@Path("appType") int type);
//    /**
//     * 获取最新App
//     */
//    @Streaming
//    @Headers("Content-Type:application/force-download")
//    @GET(ApiConstants.DOWNLOAD)
//    Flowable<ResponseBody> getApp(@Path("appType") int type);



}
