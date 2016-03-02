package api;

import android.content.Context;
import android.support.annotation.NonNull;

import org.calber.hue.Hue;

import java.util.HashMap;
import java.util.List;

import models.AllData;
import models.RequestUser;
import models.ResponseObjects;
import models.Scene;
import models.State;
import models.Whitelist;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by calber on 1/3/16.
 */
public class ApiController {


    @NonNull
    public static Observable<?> apiDeleteUser(Whitelist w) {
        return Observable.concat(Hue.api.deleteUser(Hue.TOKEN, w.id), apiAll())
                .last()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public static Observable<AllData> apiAll() {
//        return Observable.zip(Hue.api.all(Hue.TOKEN), Hue.api.scenes(Hue.TOKEN)
//                , (alld, sash) -> {
//                    Hue.hueConfiguration = alld;
//                    Hue.hueConfiguration.appscenes = sash;
//                    return Hue.hueConfiguration;
//                })
//                .retry(3)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
        return Hue.api.all(Hue.TOKEN)
                .doOnNext(allData -> Hue.hueConfiguration = allData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public static Observable<List<ResponseObjects>> apiSeachLigths() {
        return Hue.api.searchLights(Hue.TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @NonNull
    public static Observable<?> apiCreateUser(Context context) {
        return Observable.concat(Hue.api.createUser(new RequestUser("calberhue#" + Hue.androidId)),
                apiAll())
                .doOnNext(o -> {
                    ResponseObjects response = ((List<ResponseObjects>) o).get(0);
                    Hue.TOKEN = response.success.username;
                    context.getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("TOKEN", response.success.username).commit();
                })
                .last()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public static Observable<HashMap<String, Scene>> apiGetScenes() {
        return Hue.api.scenes(Hue.TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @NonNull
    public static Observable<?> apiSetScene(String id, State s) {
        return Hue.api.setScene(Hue.TOKEN, id, s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}


