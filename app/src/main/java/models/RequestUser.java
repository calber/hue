package models;

import com.google.gson.annotations.Expose;

/**
 * Created by calber on 28/2/16.
 */
public class RequestUser {

    @Expose
    public String devicetype;

    public RequestUser(String devicetype) {
        this.devicetype = devicetype;
    }
}
