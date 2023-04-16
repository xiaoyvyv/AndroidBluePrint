package com.xiaoyv.floater.widget.adapt;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

/**
 * ================================================
 * {@link DisplayMetrics} 封装类
 * <p>
 * Created by JessYan on 2018/8/11 16:42
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class DisplayMetricsInfo implements Parcelable {
    private float density;
    private int densityDpi;
    private float scaledDensity;
    private float xdpi;
    private int screenWidthDp;
    private int screenHeightDp;

    public DisplayMetricsInfo(float density, int densityDpi, float scaledDensity, float xdpi) {
        this.density = density;
        this.densityDpi = densityDpi;
        this.scaledDensity = scaledDensity;
        this.xdpi = xdpi;
    }

    public DisplayMetricsInfo(float density, int densityDpi, float scaledDensity, float xdpi, int screenWidthDp, int screenHeightDp) {
        this.density = density;
        this.densityDpi = densityDpi;
        this.scaledDensity = scaledDensity;
        this.xdpi = xdpi;
        this.screenWidthDp = screenWidthDp;
        this.screenHeightDp = screenHeightDp;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public int getDensityDpi() {
        return densityDpi;
    }

    public void setDensityDpi(int densityDpi) {
        this.densityDpi = densityDpi;
    }

    public float getScaledDensity() {
        return scaledDensity;
    }

    public void setScaledDensity(float scaledDensity) {
        this.scaledDensity = scaledDensity;
    }

    public float getXdpi() {
        return xdpi;
    }

    public void setXdpi(float xdpi) {
        this.xdpi = xdpi;
    }

    public int getScreenWidthDp() {
        return screenWidthDp;
    }

    public void setScreenWidthDp(int screenWidthDp) {
        this.screenWidthDp = screenWidthDp;
    }

    public int getScreenHeightDp() {
        return screenHeightDp;
    }

    public void setScreenHeightDp(int screenHeightDp) {
        this.screenHeightDp = screenHeightDp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.density);
        dest.writeInt(this.densityDpi);
        dest.writeFloat(this.scaledDensity);
        dest.writeFloat(this.xdpi);
        dest.writeInt(this.screenWidthDp);
        dest.writeInt(this.screenHeightDp);
    }

    protected DisplayMetricsInfo(Parcel in) {
        this.density = in.readFloat();
        this.densityDpi = in.readInt();
        this.scaledDensity = in.readFloat();
        this.xdpi = in.readFloat();
        this.screenWidthDp = in.readInt();
        this.screenHeightDp = in.readInt();
    }

    public static final Creator<DisplayMetricsInfo> CREATOR = new Creator<DisplayMetricsInfo>() {
        @Override
        public DisplayMetricsInfo createFromParcel(Parcel source) {
            return new DisplayMetricsInfo(source);
        }

        @Override
        public DisplayMetricsInfo[] newArray(int size) {
            return new DisplayMetricsInfo[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "DisplayMetricsInfo{" +
                "density=" + density +
                ", densityDpi=" + densityDpi +
                ", scaledDensity=" + scaledDensity +
                ", xdpi=" + xdpi +
                ", screenWidthDp=" + screenWidthDp +
                ", screenHeightDp=" + screenHeightDp +
                '}';
    }
}