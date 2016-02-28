package models;

import com.google.gson.annotations.Expose;

/**
 * Created by calber on 28/2/16.
 */
public class Light {
    @Expose
    public State state;
    @Expose
    public String type;
    @Expose
    public String name;
    @Expose
    public String modelid;
    @Expose
    public String swversion;

    public Pointsymbol pointsymbol;
    public String id;

}
