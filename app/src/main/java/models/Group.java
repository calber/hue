package models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by calber on 28/2/16.
 */
public class Group {

    @Expose
    public Action action;
    @Expose
    public List<String> lights = new ArrayList<String>();
    @Expose
    public String name;

}

