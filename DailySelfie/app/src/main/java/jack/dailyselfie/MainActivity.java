package jack.dailyselfie;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    static final String TAG = "TAG-dailyselfie";

    static final int REQUEST_TAKE_PHOTO = 1;
    static final String INTENT_FILE_NAMES = "intent-file-names";

    private String mCurrentPhotoPath;
    private Long mostRecent;

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.thumb_image);

        mostRecent = 0L;
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] tempFiles = path.listFiles(/*new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                Log.i(TAG, "File.lastModified = " + file.lastModified());
                if(file.lastModified() < mostRecent || mostRecent == 0L) {
                    Log.d(TAG, "Passed. MostRecent = " + mostRecent);
                    mostRecent = file.lastModified();
                    return true;
                }
                else
                    return false;
            }
        }*/);

        if(tempFiles != null && tempFiles.length > 0) {
            try {
                mCurrentPhotoPath = tempFiles[tempFiles.length - 1].getCanonicalPath();
                GalleryActivity.setPic(mImageView, mCurrentPhotoPath);
            }
            catch (IOException e) { Log.e(TAG, "IOException: " + e.getMessage()); }
        }

        new BootReceiver().setupRepeatingAlarm(getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d(TAG, "setPic Main: ImageView: " + mImageView.toString() + "\n Path: " + mCurrentPhotoPath);
            Log.d(TAG, "ImageView: " + mImageView.toString());
            GalleryActivity.setPic(mImageView, mCurrentPhotoPath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.photo_button:
                dispatchTakePictureIntent();
                break;
            case R.id.gallery_button:
                startGalleryView();
                break;
            default:
                return false;
        }
        return true;
    }

    private void dispatchTakePictureIntent() {
        Log.d(TAG, "dispatchTakePictureIntent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                Log.d(TAG, "createImageFile");
                photoFile = createImageFile();
                Log.d(TAG, "createImageFile success");
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = getImageContentUri(getApplicationContext(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void startGalleryView() {
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Intent galleryIntent = new Intent(getApplicationContext(), GalleryActivity.class);
        File[] tempFiles = path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                Log.d(TAG, "File: " + s);
                return true;
            }
        });
        String[] fileNames = new String[tempFiles.length];
        for(int i = 0; i < tempFiles.length; i++) {
            try {
                fileNames[i] = tempFiles[i].getCanonicalPath();
            }
            catch (IOException e) { Log.e(TAG, "IOException: " + e.getMessage()); }
        }
        galleryIntent.putExtra(INTENT_FILE_NAMES, fileNames);
        startActivity(galleryIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "mCurrentPhotoPath = " + mCurrentPhotoPath);
        return image;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
