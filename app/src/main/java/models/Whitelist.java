package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Whitelist {


    @SerializedName("last use date")
    @Expose
    Date lastuse;
    @SerializedName("create date")
    @Expose
    Date created;
    @Expose
    String name;
}
