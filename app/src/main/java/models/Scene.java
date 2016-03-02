package models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by calber on 2/3/16.
 */
public class Scene {

    @Expose
    public String name;
    @Expose
    public List<String> lights = new ArrayList<>();
    @Expose
    public String owner;
    @Expose
    public Boolean recycle;
    @Expose
    public Boolean locked;
    @Expose
    public String picture;
    @Expose
    public Date lastupdated;
    @Expose
    public Integer version;
    @Expose
    public Integer transitiontime;

    public String id;

}
