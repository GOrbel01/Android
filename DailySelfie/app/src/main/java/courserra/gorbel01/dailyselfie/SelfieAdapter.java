package courserra.gorbel01.dailyselfie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**

 */
public class SelfieAdapter extends BaseAdapter {

    private final List<BitmapWrapper> selfieList = new ArrayList<>();
    private Context context;

    public SelfieAdapter(Context context) {
        this.context = context;
    }

    public void add(BitmapWrapper bitmap) {
        selfieList.add(bitmap);
        notifyDataSetChanged();
    }

    public void clear() {
        selfieList.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        selfieList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return selfieList.size();
    }

    @Override
    public BitmapWrapper getItem(int position) {
        return selfieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<BitmapWrapper> getItemList() {
        return selfieList;
    }

    public int removeAll() {
        int size = selfieList.size();
        Log.d("SIZE", size + "");
        for (int i = 0; i < selfieList.size(); i++) {
            remove(i);
        }
        notifyDataSetChanged();
        return size;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final BitmapWrapper bitmap = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout itemLayout = (RelativeLayout) convertView;
        if (itemLayout == null) {
            itemLayout = (RelativeLayout) inflater.inflate(R.layout.photo_item, parent, false);
        }
        ViewHolder holder = new ViewHolder();

        // Fill in specific ToDoItem data
        // Remember that the data that goes in this View
        // corresponds to the user interface elements defined
        // in the layout file
        holder.name = (TextView) itemLayout.findViewById(R.id.selfieLabel);
        holder.name.setText(bitmap.getDisplayName());

        holder.selfieImage = (ImageView) itemLayout.findViewById(R.id.selfieImage);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        holder.selfieImage.setImageBitmap(BitmapFunctions.decodeBitmap(new File(bitmap.getFilePath())));

        itemLayout.setTag(holder);

        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showLargerImage = new Intent(context, SelfieLargerActivity.class);
                showLargerImage.putExtra("Photo", bitmap.getFilePath());
                context.startActivity(showLargerImage);
            }
        });
        // Return the View you just created
        return itemLayout;
    }

    //Wanted to use, but always gives me divide by 0 error
    private void setPic(ImageView mImageView, String imagePath) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    static class ViewHolder {
        TextView name;
        ImageView selfieImage;
        int position;
    }
}
