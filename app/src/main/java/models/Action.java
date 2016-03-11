package models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by calber on 28/2/16.
 */
public class Action implements Parcelable {
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
        dest.writeString(this.effect);
        dest.writeString(this.colormode);
    }

    public Action() {
    }

    protected Action(Parcel in) {
        this.on = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.bri = (Integer) in.readValue(Integer.class.getClassLoader());
        this.hue = (Integer) in.readValue(Integer.class.getClassLoader());
        this.sat = (Integer) in.readValue(Integer.class.getClassLoader());
        this.xy = new ArrayList<Double>();
        in.readList(this.xy, List.class.getClassLoader());
        this.ct = (Integer) in.readValue(Integer.class.getClassLoader());
        this.effect = in.readString();
        this.colormode = in.readString();
    }

    public static final Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>() {
        public Action createFromParcel(Parcel source) {
            return new Action(source);
        }

        public Action[] newArray(int size) {
            return new Action[size];
        }
    };
}
