package com.ariel.wizer.network;

import com.ariel.wizer.model.ChatChannel;
import com.ariel.wizer.model.ChatMessage;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.User;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

public interface RetrofitInterface {

    @POST("auth/new-user")
    Observable<Response> register(@Body User user);

    @POST("auth/auth-login-user-pass")
    Observable<Response> login();

    @GET("students/get-profile")
    Observable<User> getProfile();

    @POST("auth/updateProfile")
    Observable<Response> updateProfile(@Body User user);

    @PUT("auth/change-password")
    Observable<Response> changePassword(@Body User user);

    @POST("auth/reset-pass-init")
    Observable<Response> resetPasswordInit(@Body String mail);

    @POST("auth/reset-pass-finish")
    Observable<Response> resetPasswordFinish(@Body User user);

    @POST("sessions/connect-session")
    Observable<Response> connectSession(@Body String internal_id);

    @GET("sessions/change-val")
    Observable<Response> changeVal(@Query("id") String id, @Query("val") String val);

    @GET("sessions/get-students-count")
    Observable<Response> getStudentsCount(@Query("id") String id);

    /////chat/////
    @GET("chat/get-channels")
    Observable<ChatChannel[]> getChannels();

    @GET("chat/get-messages")
    Observable<ChatMessage[]> getChannelMessages(@Query("uid") String uid);

    @POST("chat/publish-message")
    Observable<Response> publishMessage(@Body ChatMessage message);

}
