package org.calber.hue;

import java.util.List;

import models.AllData;
import models.Configuration;
import models.RequestUser;
import models.Response;
import models.State;
import retrofit2.http.Body;
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
    Observable<List<Response>> createUser(@Body RequestUser device);

    @GET("api/{token}/config")
    Observable<Configuration> config(@Path("token") String token);

    @GET("api/{token}")
    Observable<AllData> all(@Path("token") String token);

    @PUT("api/{token}/lights/{id}/state")
    Observable<List<Response>> lightSwitch(@Path("token") String token,@Path("id") String id, @Body State state);
}
