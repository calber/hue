package models;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

/**
 * Created by calber on 28/2/16.
 */
public class AllData {

    @Expose
    public HashMap<String,Light> lights;
    @Expose
    public HashMap<String,Group> groups;
    @Expose
    public HashMap<String,Schedule> schedules;
    @Expose
    public HashMap<String,Scene> scenes;

    @Expose
    public Configuration config;
}
