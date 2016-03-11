package api;

import android.content.Context;
import android.support.annotation.NonNull;

import org.calber.hue.Hue;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.AllData;
import models.Change;
import models.Group;
import models.RequestUser;
import models.ResponseObjects;
import models.Scene;
import models.State;
import models.Whitelist;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
    public static Observable<?> apiDeleteGroup(String id) {
        return Observable.concat(Hue.api.deleteGroup(Hue.TOKEN, id), apiAll())
                .last()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public static Observable<AllData> apiAll() {
        return Hue.api.all(Hue.TOKEN)
                .doOnNext(allData -> {
                    Hue.hueConfiguration = allData;
                    EventBus.getDefault().post(new Change());
                })
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
    public static Observable<List<ResponseObjects>> apiGroup(Group g) {
        g.type = "LightGroup";
        return Observable.zip(Hue.api.group(Hue.TOKEN, g), apiAll(), (responseObjectses, allData) -> responseObjectses)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public static Observable<List<ResponseObjects>> apiSetGroup(Group g) {
        g.type = "LightGroup";
        return Observable.zip(Hue.api.setGroup(Hue.TOKEN, g.id, g), apiAll(), (responseObjectses, allData) -> responseObjectses)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public static Observable<?> apiLigthSwitch(String id, int bri) {
        return Observable.concat(ApiBuilder.getInstance().lightSwitch(Hue.TOKEN, id, new State(bri))
                , apiAll())
                .last()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public static Observable<List<ResponseObjects>> apiCreateUser(Context context) {
        return Hue.api.createUser(new RequestUser("fasthue@" + Hue.androidId))
                .doOnNext(o -> {
                    ResponseObjects response = o.get(0);
                    Hue.TOKEN = response.success.username;
                    context.getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("TOKEN", response.success.username).commit();
                })
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
        return Observable.concat(Hue.api.setScene(Hue.TOKEN, id, s), apiAll())
                .last()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static class RetryWithDelay implements
            Func1<Observable<? extends Throwable>, Observable<?>> {

        private final int maxRetries;
        private final int retryDelayMillis;
        private int retryCount;

        public RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
            this.maxRetries = maxRetries;
            this.retryDelayMillis = retryDelayMillis;
            this.retryCount = 0;
        }

        @Override
        public Observable<?> call(Observable<? extends Throwable> attempts) {
            return attempts
                    .flatMap(new Func1<Throwable, Observable<?>>() {
                        @Override
                        public Observable<?> call(Throwable throwable) {
                            if (++retryCount < maxRetries) {
                                // When this Observable calls onNext, the original
                                // Observable will be retried (i.e. re-subscribed).
                                return Observable.timer(retryDelayMillis,
                                        TimeUnit.MILLISECONDS);
                            }

                            // Max retries hit. Just pass the error along.
                            return Observable.error(throwable);
                        }
                    });
        }
    }

}


