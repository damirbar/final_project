package com.ariel.wizeup.network;

import com.ariel.wizeup.model.Course;
import com.ariel.wizeup.model.CourseFile;
import com.ariel.wizeup.model.CourseMessage;
import com.ariel.wizeup.model.Event;
import com.ariel.wizeup.model.NotificationMsg;
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

    @FormUrlEncoded
    @POST("auth/reset-pass-init")
    Observable<Response> resetPasswordInit(@Field("email") String email);


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

    @GET("students/get-events")
    Observable<Event[]> getEvents(@Query("start") int start ,@Query("end") int end);

    @GET("students/get-notifications")
    Observable<NotificationMsg[]> getNotifications(@Query("start") int start , @Query("end") int end);


    //////////////////Session//////////////////

    @FormUrlEncoded
    @POST("sessions/connect-session")
    Observable<Session> connectSession(@Field("sid") String sid, @Field("name") String name);

    @POST("sessions/create-session")
    Observable<Response> createSession(@Body Session session);

    @GET("sessions/change-val")
    Observable<Response> changeVal(@Query("sid") String sid, @Query("val") int val);

    @GET("sessions/rate-message")
    Observable<Response> rateMessage(@Query("sid") String sid, @Query("msgid") String msgid, @Query("rating") int rating);

    @GET("sessions/rate-reply-message")
    Observable<Response> rateReplyMessage(@Query("sid") String sid, @Query("msgid") String msgid, @Query("rating") int rating);

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

    @GET("sessions/get-session")
    Observable<Session> getSessionById(@Query("sid") String sid);

    @GET("sessions/get-message-replies")
    Observable<SessionMessage[]> getSessionMessageReplies(@Query("mid") String mid);



    //////////////////Courses//////////////////

    @POST("courses/create-session")
    Observable<Response> createCourseSession(@Body Session session);


    @GET("courses/get-all-courses")
    Observable<Course[]> getAllCourses();

    @GET("courses/get-my-courses")
    Observable<Course[]> getMyCourses();

    @GET("courses/get-course")
    Observable<Course> getCourseById(@Query("cid") String cid);

    @Multipart
    @POST("courses/post-file")
    Observable<Response> uploadFile(@Part MultipartBody.Part file, @Query("cid") int cid);

    @POST("courses/create-course")
    Observable<Course> createCourse(@Body Course course);

    @GET("courses/add-student-to-course")
    Observable<Response> addStudentToCourse(@Query("cid") String cid, @Query("student") String student);

    @GET("courses/get-course-files")
    Observable<CourseFile[]> getCourseFiles(@Query("cid") String cid);

    @GET("courses/get-all-sessions")
    Observable<Session[]> getCourseSessions(@Query("cid") String cid);

    @GET("courses/get-all-messages")
    Observable<CourseMessage []> CourseGetAllMessages(@Query("cid") String cid);

    @GET("courses/get-message")
    Observable<CourseMessage> getCourseMessage(@Query("mid") String mid);

    @POST("courses/messages")
    Observable<Response> publishCourseMessage(@Body CourseMessage message);

    @POST("courses/reply")
    Observable<Response> publishCourseReply(@Body CourseMessage message);

    @GET("sessions/get-message-replies")
    Observable<CourseMessage[]> getCourseMessageReplies(@Query("mid") String mid);


}
