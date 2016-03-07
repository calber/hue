package api;

import org.calber.hue.Hue;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by calber on 1/3/16.
 */
public class MockClient implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        String filename = chain.request().url().url().getFile().substring(1) + ".json";

        InputStream is = null;
        byte[] buffer = new byte[0];
        try {
            is = Hue.context.getAssets().open(filename);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResponseBody b = ResponseBody.create(MediaType.parse("application/json"), buffer);

        Response res = new Response.Builder()
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .request(chain.request())
                .body(b)
                .build();
        return res;
    }
}
