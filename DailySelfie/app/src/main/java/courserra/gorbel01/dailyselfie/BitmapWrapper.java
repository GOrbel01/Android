package courserra.gorbel01.dailyselfie;

import android.graphics.Bitmap;

/**
 * Created by Cloud on 23/08/2015.
 */
public class BitmapWrapper {
    private String filePath;
    private String displayName;

    public BitmapWrapper(String path, String name) {
        this.filePath = path;
        this.displayName = name;
    }

    public BitmapWrapper() {

    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }
}
