package models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by calber on 28/2/16.
 */
public class Group implements Parcelable {

    @Expose
    public Action action;
    @Expose
    public List<String> lights = new ArrayList<String>();
    @Expose
    public String name;
    @Expose
    public String type;

    public boolean localstate;
    public String id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.action, flags);
        dest.writeStringList(this.lights);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeByte(localstate ? (byte) 1 : (byte) 0);
        dest.writeString(this.id);
    }

    public Group() {
    }

    protected Group(Parcel in) {
        this.action = in.readParcelable(Action.class.getClassLoader());
        this.lights = in.createStringArrayList();
        this.name = in.readString();
        this.type = in.readString();
        this.localstate = in.readByte() != 0;
        this.id = in.readString();
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}

