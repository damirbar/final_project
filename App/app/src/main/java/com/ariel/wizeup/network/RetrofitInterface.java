package com.ariel.wizeup.network;

import com.ariel.wizeup.model.Course;
import com.ariel.wizeup.model.CourseFile;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.Searchable;
import com.ariel.wizeup.model.Session;
import com.ariel.wizeup.model.SessionMessage;
import com.ariel.wizeup.model.User;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

public interface RetrofitInterface {

    //////////////////search//////////////////

    @GET("search/free-text-search")
    Observable<Searchable> getSearch(@Query("keyword") String keyword);

    //////////////////Auth//////////////////

    @POST("auth/new-user")
    Observable<Response> register(@Body User user);

    @POST("auth/auth-login-user-pass")
    Observable<User> login();

    @GET("auth/get-user-by-token")
    Observable<User> getProfileByToken();

    @PUT("auth/change-password")
    Observable<Response> changePassword(@Body User user);

    @POST("auth/reset-pass-init")
    Observable<Response> resetPasswordInit(@Body String mail);

    @POST("auth/reset-pass-finish")
    Observable<Response> resetPasswordFinish(@Body User user);

    //////////////////User//////////////////

    @GET("students/get-profile")
    Observable<User> getProfile(@Query("id") String id);

    @POST("students/edit-profile")
    Observable<Response> updateProfile(@Body User user);

    @Multipart
    @POST("students/post-profile-image")
    Observable<Response> uploadProfileImage(@Part MultipartBody.Part file);

    //////////////////Session//////////////////

    @FormUrlEncoded
    @POST("sessions/connect-session")
    Observable<Session> connectSession(@Field("sid") String sid, @Field("name") String name);

    @POST("sessions/create-session")
    Observable<Response> createSession(@Body Session session);

    @GET("sessions/change-val")
    Observable<Response> changeVal(@Query("id") String id, @Query("val") int val);

    @GET("sessions/rate-message")
    Observable<Response> rateMessage(@Query("sid") String sid, @Query("msgid") String msgid, @Query("rating") int rating);

    @GET("sessions/get-students-count")
    Observable<Response> getStudentsCount(@Query("id") String id);

    @GET("sessions/get-students-rating")
    Observable<Response> getStudentsRating(@Query("id") String id);

    @GET("sessions/get-all-messages")
    Observable<SessionMessage []> getAllMessages(@Query("sid") String sid);

    @GET("sessions/get-message")
    Observable<SessionMessage> getMessage(@Query("mid") String mid);

    @POST("sessions/messages")
    Observable<Response> publishSessionMessage(@Body SessionMessage message);

    @POST("sessions/reply")
    Observable<Response> publishReply(@Body SessionMessage message);

    @GET("sessions/disconnect")
    Observable<Response> disconnect(@Query("sid") String sid);

    @Multipart
    @POST("sessions/post-video")
    Observable<Response> uploadVid(@Part MultipartBody.Part file, @Query("sid") String sid);

    //////////////////Courses//////////////////

    @GET("courses/get-all-courses")
    Observable<Course[]> getAllCourses();

    @GET("courses/get-all-courses-by-id")
    Observable<Course[]> getAllCoursesById(@Query("id") String id);

    @GET("courses/get-course")
    Observable<Course> getCourseById(@Query("cid") String cid);

    @Multipart
    @POST("courses/post-file")
    Observable<Response> uploadFile(@Part MultipartBody.Part file, @Query("cid") String cid);

    @POST("courses/create-course")
    Observable<Course> createCourse(@Body Course course);

    @GET("courses/add-students-to-course")
    Observable<Response> addStudentToCourse(@Query("cid") String cid, @Query("student") String student);

}
