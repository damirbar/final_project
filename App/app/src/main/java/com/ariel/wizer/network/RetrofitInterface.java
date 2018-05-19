package com.ariel.wizer.network;

import android.net.Uri;

import com.ariel.wizer.model.ChatChannel;
import com.ariel.wizer.model.ChatMessage;
import com.ariel.wizer.model.Course;
import com.ariel.wizer.model.CourseFile;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.Session;
import com.ariel.wizer.model.SessionMessage;
import com.ariel.wizer.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import rx.Observable;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public interface RetrofitInterface {

    @POST("auth/new-user")
    Observable<Response> register(@Body User user);

    @POST("auth/auth-login-user-pass")
    Observable<User> login();

    @GET("students/get-profile")
    Observable<User> getProfile(@Query("id") String id);

    @POST("students/edit-profile")
    Observable<Response> updateProfile(@Body User user);

    @PUT("auth/change-password")
    Observable<Response> changePassword(@Body User user);

    @POST("auth/reset-pass-init")
    Observable<Response> resetPasswordInit(@Body String mail);

    @POST("auth/reset-pass-finish")
    Observable<Response> resetPasswordFinish(@Body User user);

    /////Session/////
    @POST("sessions/connect-session")
    Observable<Response> connectSession(@Body Session session);

    @GET("sessions/create-session")
    Observable<Session> createSession();

    @GET("sessions/change-val")
    Observable<Response> changeVal(@Query("id") String id, @Query("val") String val);

    @GET("sessions/rate-message")
    Observable<Response> rateMessage(@Query("sid") String sid, @Query("msgid") String msgid, @Query("rating") int rating);

    @GET("sessions/get-students-count")
    Observable<Response> getStudentsCount(@Query("id") String id);

    @GET("sessions/get-students-rating")
    Observable<Response> getStudentsRating(@Query("id") String id);

    @GET("sessions/get-all-messages")
    Observable<SessionMessage []> getMessages(@Query("sid") String sid);

    @POST("sessions/messages")
    Observable<Response> publishSessionMessage(@Body SessionMessage message);

    @GET("sessions/disconnect")
    Observable<Response> disconnect(@Query("sid") String sid);

    /////Session-video/////

    @GET("sessions/get-video")
    @Streaming
    Observable<ResponseBody> getVideo(@Query("sid") String sid);

    /////Classes/////

    @GET("courses/get-all-courses")
    Observable<Course[]> getAllCourses();

    @GET("courses/get-all-Files")
    Observable<CourseFile[]> getAllFiles();


    /////chat/////
    @GET("chat/get-channels")
    Observable<ChatChannel[]> getChannels();

    @GET("chat/get-messages")
    Observable<ChatMessage[]> getChannelMessages(@Query("uid") String uid);

    @POST("chat/publish-message")
    Observable<Response> publishMessage(@Body ChatMessage message);

}
