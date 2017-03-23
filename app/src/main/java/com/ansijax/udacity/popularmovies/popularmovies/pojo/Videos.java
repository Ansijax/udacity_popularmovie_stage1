package com.ansijax.udacity.popularmovies.popularmovies.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Videos implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<VideosResult> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<VideosResult> getResults() {
        return results;
    }

    public void setResults(List<VideosResult> results) {
        this.results = results;
    }


    protected Videos(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        if (in.readByte() == 0x01) {
            results = new ArrayList<VideosResult>();
            in.readList(results, VideosResult.class.getClassLoader());
        } else {
            results = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        if (results == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(results);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Videos> CREATOR = new Parcelable.Creator<Videos>() {
        @Override
        public Videos createFromParcel(Parcel in) {
            return new Videos(in);
        }

        @Override
        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };
}