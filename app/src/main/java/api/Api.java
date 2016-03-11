package api;

import java.util.HashMap;
import java.util.List;

import models.AllData;
import models.Configuration;
import models.Group;
import models.Light;
import models.RequestUser;
import models.Response;
import models.ResponseObjects;
import models.Scene;
import models.State;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by calber on 24/11/15.
 */
public interface Api {

    @POST("api")
    Observable<List<ResponseObjects>> createUser(@Body RequestUser device);

    @GET("api/{token}/config")
    Observable<Configuration> config(@Path("token") String token);

    @GET("api/{token}")
    Observable<AllData> all(@Path("token") String token);

    @PUT("api/{token}/lights/{id}/state")
    Observable<List<ResponseObjects>> lightSwitch(@Path("token") String token, @Path("id") String id, @Body State state);

    @DELETE("/api/{token}/config/whitelist/{device}")
    Observable<List<Response>> deleteUser(@Path("token") String token, @Path("device") String device);

    @POST("/api/{token}/lights")
    Observable<List<ResponseObjects>> searchLights(@Path("token") String token);

    @GET("api/{token}/scenes")
    Observable<HashMap<String,Scene>> scenes(@Path("token") String token);

    @GET("api/{token}/scenes/{id}")
    Observable<HashMap<String,Scene>> scene(@Path("token") String token, @Path("id") String id);

    @PUT("/api/{token}/groups/{id}/action")
    Observable<List<ResponseObjects>> setScene(@Path("token") String token, @Path("id") String id, @Body State state);

    @PUT("/api/{token}/groups/{id}")
    Observable<List<ResponseObjects>> setGroup(@Path("token") String token, @Path("id") String id, @Body Group group);

    @POST("api/{token}/groups")
    Observable<List<ResponseObjects>> group(@Path("token") String token, @Body Group groups);

    @DELETE("/api/{token}/groups/{id}")
    Observable<List<Response>> deleteGroup(@Path("token") String token, @Path("id") String group);

    @DELETE("/api/{token}/lights/{id}")
    Observable<List<Response>> deleteLight(@Path("token") String token, @Path("id") String light);

    @PUT("/api/{token}/lights/{id}")
    Observable<List<Response>> setLight(@Path("token") String token, @Path("id") String id, @Body Light light);
}
