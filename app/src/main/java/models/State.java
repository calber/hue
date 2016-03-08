package models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by calber on 28/2/16.
 */
public class State {
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
    public String alert;
    @Expose
    public String effect;
    @Expose
    public String colormode;
    @Expose
    public Boolean reachable;

    @Expose
    public String scene;

    public State(boolean b) {
        on = b;
        if (on) bri = 254;
        else bri = 0;
    }

    public State(int i) {
        if (i == 0)
            on = false;
        else
            on = true;
        bri = i;
    }

    public State(Scene s, int i) {
        scene = s.id;
        if (i == 0)
            on = false;
        else
            on = true;
        bri = i;
    }
}
