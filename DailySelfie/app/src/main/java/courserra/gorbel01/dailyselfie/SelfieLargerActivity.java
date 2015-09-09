package courserra.gorbel01.dailyselfie;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Cloud on 22/08/2015.
 */
public class SelfieLargerActivity extends Activity {

    private LinearLayout largeSelfieLayout;

    private ImageView largeSelfieView;

    //TODO Improve after impl persistent storage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_selfie);
        largeSelfieLayout = (LinearLayout) findViewById(R.id.largeSelfieLayout);
        largeSelfieView = (ImageView) findViewById(R.id.largeSelfie);
        Bundle extras = getIntent().getExtras();
        String bitmapPath = extras.getString("Photo");
        Log.d("BM_PATH", bitmapPath);
        Bitmap largeSelfie;
        if (extras != null) {
            largeSelfie = BitmapFactory.decodeFile(bitmapPath);
        }
        else {
            largeSelfie = null;
        }

        final int maxSize = 960;
        int outWidth;
        int outHeight;
        int inWidth = largeSelfie.getWidth();
        int inHeight = largeSelfie.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(largeSelfie, outWidth, outHeight, false);
        largeSelfieView.setImageBitmap(scaledBitmap);
    }
}
