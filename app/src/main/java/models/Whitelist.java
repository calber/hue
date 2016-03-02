package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Whitelist implements Comparable<Whitelist> {


    @SerializedName("last use date")
    @Expose
    public Date lastuse;
    @SerializedName("create date")
    @Expose
    public Date created;
    @Expose
    public String name;

    public String id;

    @Override
    public int compareTo(Whitelist another) {
        if(another.lastuse.getTime() > lastuse.getTime()) return 1;
        if(another.lastuse.getTime() < lastuse.getTime()) return -1;
        return 0;
    }
}
