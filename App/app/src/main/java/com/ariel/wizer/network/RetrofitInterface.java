package com.ariel.wizer.network;

import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface RetrofitInterface {

    @POST("users")
    Observable<Response> register(@Body User user);

    @POST("authenticate")
    Observable<Response> login();

    @GET("users/{mail}")
    Observable<User> getProfile(@Path("mail") String mail);

    @PUT("users/{mail}")
    Observable<Response> changePassword(@Path("mail") String mail, @Body User user);

    @POST("users/{mail}/password")
    Observable<Response> resetPasswordInit(@Path("mail") String mail);

    @POST("users/{mail}/password")
    Observable<Response> resetPasswordFinish(@Path("mail") String mail, @Body User user);
}
