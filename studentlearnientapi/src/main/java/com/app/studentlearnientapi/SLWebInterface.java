package com.app.studentlearnientapi;

import com.app.studentlearnientapi.DataModels.GridDesignDataModels.GridDesignDataModel;
import com.app.studentlearnientapi.DataModels.LoginDataModels.UserLoginDataModel;
import com.app.studentlearnientapi.DataModels.SessionInfoDataModels.SessionInfoDataModel;
import com.app.studentlearnientapi.DataModels.TodaySessionModels.UserTodaySessionDataModel;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by macbookpro on 19/12/2015.
 */
public interface SLWebInterface {

    //@FormUrlEncoded
    @GET("/Jupiter/sun.php")
    public void signInUser(@Query("api") String query, Callback<UserLoginDataModel> callBack);

    @GET("/Jupiter/sun.php")
    public void getUserTodaysSession(@Query("api") String query, Callback<UserTodaySessionDataModel> callBack);

    @GET("/Jupiter/sun.php")
    public void getMyCurrentSession(@Query("api") String query, Callback<UserTodaySessionDataModel> callBack);

    @GET("/Jupiter/sun.php")
    public void retreiveSeatsAssignment(@Query("api") String query, Callback<SessionInfoDataModel> callBack);

    @GET("/Jupiter/sun.php")
    public void retreiveGridDesign(@Query("api") String query, Callback<GridDesignDataModel> callBack);

    @GET("/Jupiter/sun.php")
    public void logOutUser(@Query("api") String query, Callback<UserLoginDataModel> callBack);

    @GET("/Jupiter/sun.php")
    public void updateUserState(@Query("api") String query, Callback<Response> callBack);
}
