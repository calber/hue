package models;

import com.google.gson.annotations.Expose;

/**
 * Created by calber on 28/2/16.
 */
public class Response {
    @Expose
    public User success;
    @Expose
    public Error error;

    public class User {
        @Expose
        public String username;
    }

    public class Error {
        @Expose
        public String type;
        @Expose
        public String description;
        @Expose
        public String address;
    }
}
