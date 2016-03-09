package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.calber.hue.BuildConfig;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.ResponseObjects;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
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
                    .addInterceptor(new PhilipsDontKnowWhatIsRest())
                    .build();
        else
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .addInterceptor(new PhilipsDontKnowWhatIsRest())
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

    static class PhilipsDontKnowWhatIsRest implements Interceptor {

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());

            Gson gson = new Gson();
            Type type = new TypeToken<List<ResponseObjects>>() {}.getType();

            StringWriter writer = new StringWriter();
            IOUtils.copy(originalResponse.body().charStream(), writer);
            String s = writer.toString();

            try {
                List<ResponseObjects> res = gson.fromJson(s, type);
                if (res.size() > 0 && res.get(0).error != null)
                    switch (res.get(0).error.type) {
                        case "1":
                            return originalResponse.newBuilder().code(403).message(res.get(0).error.description).build();
                        case "3":
                            return originalResponse.newBuilder().code(404).message(res.get(0).error.description).build();
                        default:
                            return originalResponse.newBuilder().code(400).message(res.get(0).error.description).build();
                    }
                else
                    return originalResponse.newBuilder().body(ResponseBody.create(MediaType.parse("application/json"),s)).build();
            } catch (Exception e) {
                return originalResponse.newBuilder().body(ResponseBody.create(MediaType.parse("application/json"),s)).build();
            }
        }

    }

}
