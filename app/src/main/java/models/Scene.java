package models;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
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

    public String getCleanName() {
        List<String> result = Splitter.on(" ").trimResults().omitEmptyStrings().splitToList(name);
        return Joiner.on(" ").join(result.get(0),result.get(1));
    }
}
