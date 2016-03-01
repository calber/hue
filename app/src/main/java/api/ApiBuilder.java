package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.calber.hue.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by calber on 24/11/15.
 */
public class ApiBuilder {

    public static final String NONETWORK = "nonetwork";
    private static String ROOTURL;
    private static Api api;
    private static final String ANDROID = "ANDROID";


    public static Api newInstance(String url) {
        ROOTURL = url;
        return ApiBuilder();
    }

    public static Api getInstance() {
        return api;
    }

    private static Api ApiBuilder() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client;
        if (BuildConfig.BUILD_TYPE.equals(NONETWORK))
            client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(new MockClient())
                    .build();
        else
            client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(ROOTURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        api = retrofit.create(Api.class);
        return api;
    }


}
