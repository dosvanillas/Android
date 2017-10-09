package jack.dailyselfie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

public class GalleryActivity extends Activity {

    private final static String TAG = "TAG-dailyselfie-gallery";

    private ListView mList;
    private Resources mRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mList = (ListView) findViewById(R.id.gallery_list);
        mList.setAdapter(new GalleryAdapter(getApplicationContext(), getIntent().getStringArrayExtra(MainActivity.INTENT_FILE_NAMES)));

        mRes = getResources();
    }

    private class GalleryAdapter extends ArrayAdapter<String> {

        private final Context context;
        private final LayoutInflater inflater;
        private final String[] values;

        public GalleryAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.values = values;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            View rowView = inflater.inflate(R.layout.list_selfie, null);
            final ImageView imageView = (ImageView) rowView.findViewById(R.id.picture);
            final TextView textView = (TextView) rowView.findViewById(R.id.photo_text);
            final LinearLayout linearLayout = (LinearLayout) rowView.findViewById(R.id.selfie_item);

            Log.d(TAG, "setPic gallery " + position);
            Log.d(TAG, "RowView: " + rowView.toString());
            Log.d(TAG, "ImageView: " + imageView.toString());

            setPicDimen(imageView, values[position]);
            setImageText(textView, values[position]);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openImageBig(values[position]);
                }
            });

            return rowView;
        }
    }

    protected void setPicDimen(ImageView imageView, String currentPhotoPath) {
        // Get the dimensions of the View
        int targetW = (int)mRes.getDimension(R.dimen.image_width);
        int targetH = (int)mRes.getDimension(R.dimen.list_height);
        Log.d(TAG, "TW: " + targetW + ", TH: " + targetH + ", ImageView: " + imageView.toString());
        Log.d(TAG, "Path: " + currentPhotoPath);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            imageView.setImageBitmap(bitmap);
            imageView.setRotation(90);
        }
        catch(OutOfMemoryError e) { Log.e(TAG, "setPic OutOfMemoryError: " + e.getMessage()); }
    }

    protected void setImageText(TextView textView, String currentPhotoPath) {
        textView.setText(currentPhotoPath.substring(currentPhotoPath.lastIndexOf("/") + 1));
    }

    protected static void setPic(ImageView imageView, String currentPhotoPath) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth(); targetW = targetW > 0 ? targetW : 400;
        int targetH = imageView.getHeight(); targetH = targetH > 0 ? targetH : 400;
        Log.d(TAG, "TW: " + targetW + ", TH: " + targetH + ", ImageView: " + imageView.toString());
        Log.d(TAG, "Path: " + currentPhotoPath);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        try {
            Log.d(TAG, "Attempt create Bitmap");
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            Log.d(TAG, "Bitmap created: Bitmap: " + bitmap.toString() + "\nW: " + bitmap.getWidth() + ", H: " + bitmap.getHeight());
            imageView.setImageBitmap(bitmap);
            Log.d(TAG, "Bitmap set: ImageView: " + imageView.toString());
            imageView.setRotation(90);
        }
        catch(OutOfMemoryError e) { Log.e(TAG, "setPic OutOfMemoryError: " + e.getMessage()); }
    }

    private void openImageBig(String currentPhotoPath) {
        Log.i(TAG, "Open Image Big: " + currentPhotoPath);
        Intent openPic = new Intent(Intent.ACTION_VIEW);
        openPic.setDataAndType(Uri.fromFile(new File(currentPhotoPath)), "image/*");
        startActivity(openPic);
    }
}
