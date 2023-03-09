package com.atocash.utils.fileUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.atocash.BuildConfig;
import com.atocash.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ImageHelpers {

    private static volatile ImageHelpers INSTANCE;
    private Context context;

    //max width and height values of the compressed image is taken as 612x816
    private float maxWidth = 612.0f;
    private float maxHeight = 816.0f;
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
    private int quality = 100;
    private String destinationDirectoryPath;
    private String fileNamePrefix;
    private String fileName;

    private static String FOLDER_TITLE = "Brainwek";
    private static final String IMAGE_FOLDER = "Images";

    private static String folderPath = "";
    static final String FILES_PATH = "App";

    private ImageHelpers(Context context) {
        this.context = context;
        destinationDirectoryPath = context.getCacheDir().getPath() + File.pathSeparator + FILES_PATH;

        FOLDER_TITLE = context.getString(R.string.app_name);
    }

    public static void initFolders() {
        try {
            File f = new File(Environment.getExternalStorageDirectory(), FOLDER_TITLE + "_Images");
            folderPath = String.valueOf(f);
            if (!f.exists()) {
                f.mkdirs();
            }
        } catch (Exception error) {
            error.printStackTrace();
            printLog("Error while creating folder");
        }
    }

    public static ImageHelpers getDefault(Context context) {
        if (INSTANCE == null) {
            synchronized (ImageHelpers.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImageHelpers(context);
                }
            }
        }
        return INSTANCE;
    }


    @SuppressLint("CheckResult")
    public static void getCompressedBitmap(Context context, File file, onCompressDone compressDone) {
        getDefault(context).compressToFileAsObservable(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file1 -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(file1 + "");
                    addJpgSignatureToImage(context, bitmap);
                    compressDone.onDone(bitmap);
                }, throwable -> printLog(throwable.getMessage()));
    }

    private static void printLog(String message) {
        if (BuildConfig.DEBUG) Log.e("Debug imageHelper: ", message);
    }

    public static Bitmap getBitmapFromPath(String imagePath) {
        return BitmapFactory.decodeFile(imagePath);
    }

    public static Bitmap getBitmapFromFile(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    public static String getPathFromBitmap(Context context, Bitmap bitmap) {
        String imagePath = getFolderPath() + "/" + "Preview_" + System.currentTimeMillis() + ".png";

        File imgFile = new File(imagePath);
        if (!imgFile.exists()) {
            if (!imgFile.getParentFile().exists()) {
                imgFile.getParentFile().mkdirs();
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        sendRefreshBroadCast(context, imgFile);

        return imagePath;
    }


    public static String saveImageFromImageView(ImageView imageView) {
        String imagePath = getFolderPath() + "/" + "Preview_" + System.currentTimeMillis() + ".jpg";

        imageView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File imgFile = new File(imagePath);
        if (!imgFile.exists()) {
            if (!imgFile.getParentFile().exists()) {
                imgFile.getParentFile().mkdirs();
            }
        }
        try {
            imgFile.delete();
            imgFile.createNewFile();
            int quality = 100;
            FileOutputStream fileOutputStream = new FileOutputStream(imgFile);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);

            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendRefreshBroadCast(imageView.getContext(), imgFile);

        return imagePath;
    }

    public static void sendRefreshBroadCast(Context context, File imgFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(imgFile));
        context.sendBroadcast(intent);
    }

    public static Uri getUriFromPath(String imagePath) {
        return Uri.fromFile(new File(imagePath));
    }

    public interface onCompressDone {
        void onDone(Bitmap bitmap);
    }

    public File compressToFile(File file) {
        return ImageCompressor.compressImage(context, Uri.fromFile(file), maxWidth, maxHeight,
                compressFormat, bitmapConfig, quality, destinationDirectoryPath,
                fileNamePrefix, fileName);
    }

    public Bitmap compressToBitmap(File file) {
        return ImageCompressor.getScaledBitmap(context, Uri.fromFile(file), maxWidth, maxHeight, bitmapConfig);
    }

    public Observable<File> compressToFileAsObservable(final File file) {
        return Observable.defer(() -> Observable.just(compressToFile(file)));
    }

    public Observable<Bitmap> compressToBitmapAsObservable(final File file) {
        return Observable.defer(() -> Observable.just(compressToBitmap(file)));
    }

    public static class Builder {
        private ImageHelpers compressor;

        public Builder(Context context) {
            compressor = new ImageHelpers(context);
        }

        public Builder setMaxWidth(float maxWidth) {
            compressor.maxWidth = maxWidth;
            return this;
        }

        public Builder setMaxHeight(float maxHeight) {
            compressor.maxHeight = maxHeight;
            return this;
        }

        public Builder setCompressFormat(Bitmap.CompressFormat compressFormat) {
            compressor.compressFormat = compressFormat;
            return this;
        }

        public Builder setBitmapConfig(Bitmap.Config bitmapConfig) {
            compressor.bitmapConfig = bitmapConfig;
            return this;
        }

        public Builder setQuality(int quality) {
            compressor.quality = quality;
            return this;
        }

        public Builder setDestinationDirectoryPath(String destinationDirectoryPath) {
            compressor.destinationDirectoryPath = destinationDirectoryPath;
            return this;
        }

        public Builder setFileNamePrefix(String prefix) {
            compressor.fileNamePrefix = prefix;
            return this;
        }

        public Builder setFileName(String fileName) {
            compressor.fileName = fileName;
            return this;
        }

        public ImageHelpers build() {
            return compressor;
        }
    }

    public static final String insertImage(ContentResolver cr,
                                           Bitmap source,
                                           String title,
                                           String description) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());


        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    private static final Bitmap storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind) {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND, kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);

            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }


    //to get bitmap from uri
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //to resize bitmap
    public static Bitmap getResizedBitmap(Bitmap image) {
        Bitmap bitmap = null;
        if (image != null) {
            bitmap = Bitmap.createScaledBitmap(image, 90, 90, true);
        }
        return bitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        if (image != null) {
            bitmap = Bitmap.createScaledBitmap(image, maxWidth, maxHeight, true);
        }
        return bitmap;
    }

    public static Bitmap getResizedBitmapFromUri(Context context, Uri uri) {
        Bitmap resizedBitmap = getBitmapFromUri(context, uri);
        if (resizedBitmap != null) {
            printLog("Bitmap size: " + resizedBitmap.getByteCount());
        }
        printLog("Bitmap size: " + getResizedBitmap(resizedBitmap).getByteCount());
        return getResizedBitmap(resizedBitmap);
    }

    public static Drawable getResizedDrawable(Context context, int imageResource) {
        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        window.getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;

        BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(imageResource);
        double imageHeight = bd.getBitmap().getHeight();
        double imageWidth = bd.getBitmap().getWidth();

        double ratio = screenWidth / imageWidth;
        int newImageHeight = (int) (imageHeight * ratio);

        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), imageResource);

        return new BitmapDrawable(context.getResources(), getBitmapResized(bMap, newImageHeight, screenWidth));
    }

    private static Bitmap getBitmapResized(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate and returns the new Bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = ((Activity) context).managedQuery(uri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public static void scanMediaFile(Context context, File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    private static File getAlbumStorageDir(String albumName) {
        File file = new File(folderPath, albumName);
        if (!file.mkdirs()) {
        }
        return file;
    }

    public static String getFolderPath() {
        return folderPath;
    }

    public static void addJpgSignatureToImage(Context context, Bitmap bitmap) {
        try {
            String fileName = String.format(Locale.getDefault(), "Image__%d.jpg", System.currentTimeMillis());
            File photo = new File(getAlbumStorageDir(IMAGE_FOLDER), fileName);

            saveBitmapToJPG(bitmap, photo);
            scanMediaFile(context, photo);

            String imagePath = photo + "";
            printLog("ImagePath: " + imagePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static StorageSize convertStorageSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        StorageSize sto = new StorageSize();
        if (size >= gb) {

            sto.suffix = "GB";
            sto.value = (float) size / gb;
            return sto;
        } else if (size >= mb) {

            sto.suffix = "MB";
            sto.value = (float) size / mb;

            return sto;
        } else if (size >= kb) {


            sto.suffix = "KB";
            sto.value = (float) size / kb;

            return sto;
        } else {
            sto.suffix = "B";
            sto.value = (float) size;

            return sto;
        }
    }
}
