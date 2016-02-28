package models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by calber on 28/2/16.
 */
public class Action {
    @Expose
    public Boolean on;
    @Expose
    public Integer bri;
    @Expose
    public Integer hue;
    @Expose
    public Integer sat;
    @Expose
    public List<Double> xy = new ArrayList<Double>();
    @Expose
    public Integer ct;
    @Expose
    public String effect;
    @Expose
    public String colormode;

}
