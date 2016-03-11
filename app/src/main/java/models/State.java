package models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by calber on 28/2/16.
 */
public class State implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.on);
        dest.writeValue(this.bri);
        dest.writeValue(this.hue);
        dest.writeValue(this.sat);
        dest.writeList(this.xy);
        dest.writeValue(this.ct);
        dest.writeString(this.alert);
        dest.writeString(this.effect);
        dest.writeString(this.colormode);
        dest.writeValue(this.reachable);
        dest.writeString(this.scene);
    }

    protected State(Parcel in) {
        this.on = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.bri = (Integer) in.readValue(Integer.class.getClassLoader());
        this.hue = (Integer) in.readValue(Integer.class.getClassLoader());
        this.sat = (Integer) in.readValue(Integer.class.getClassLoader());
        this.xy = new ArrayList<Double>();
        in.readList(this.xy, List.class.getClassLoader());
        this.ct = (Integer) in.readValue(Integer.class.getClassLoader());
        this.alert = in.readString();
        this.effect = in.readString();
        this.colormode = in.readString();
        this.reachable = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.scene = in.readString();
    }

    public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() {
        public State createFromParcel(Parcel source) {
            return new State(source);
        }

        public State[] newArray(int size) {
            return new State[size];
        }
    };
}
