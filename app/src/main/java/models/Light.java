package models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.CheckBox;

import com.google.gson.annotations.Expose;

/**
 * Created by calber on 28/2/16.
 */
public class Light implements Parcelable {
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
    public CheckBox checkBox;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.state, flags);
        dest.writeString(this.type);
        dest.writeString(this.name);
        dest.writeString(this.modelid);
        dest.writeString(this.swversion);
        dest.writeString(this.id);
    }

    public Light() {
    }

    protected Light(Parcel in) {
        this.state = in.readParcelable(State.class.getClassLoader());
        this.type = in.readString();
        this.name = in.readString();
        this.modelid = in.readString();
        this.swversion = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<Light> CREATOR = new Parcelable.Creator<Light>() {
        public Light createFromParcel(Parcel source) {
            return new Light(source);
        }

        public Light[] newArray(int size) {
            return new Light[size];
        }
    };
}
