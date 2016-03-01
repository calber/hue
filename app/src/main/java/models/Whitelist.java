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
        return (int) (another.lastuse.getTime() - lastuse.getTime());
    }
}
