package com.app.learniatstudent.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.learniatstudent.R;
import com.app.learniatstudent.XMPPClient.XMPPController;
import com.app.studentlearnientapi.DataModels.LoginDataModels.UserLoginDataModel;
import com.app.studentlearnientapi.DataModels.TodaySessionModels.Session;
import com.app.studentlearnientapi.SLAPI;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by macbookpro on 23/12/2015.
 */
public class CommonUtils {

    private static Context mContext;
    public static CommonUtils _instance = null;
    Toast toast;
    private CountDownTimer timer;
    Dialog dialog;
    ProgressDialog progressDialog;
    private Session currentSession;
    private List<Session> sessionsList;
    private String classTimeExtendMessage = "";
    private int teacherId = 0;
    public static Activity currentActivity;
    private boolean isXMPPReconnectDialogShown = false;

    private CommonUtils(){

    }

    public static CommonUtils getInstance(Context ctx){
        if(_instance == null){
            _instance = new CommonUtils();
            mContext = ctx;
        }
        return _instance;
    }

    /**
     * Internet connection check
     * @param context
     * @return
     */
    public boolean isOnline(Context context) {
        boolean connected = false;
        final ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connected = true;
        } else if (netInfo != null && netInfo.isConnected()
                && cm.getActiveNetworkInfo().isAvailable()) {
            connected = true;
        } else if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://www.google.com");
                HttpURLConnection urlc = (HttpURLConnection) url
                        .openConnection();
                urlc.setConnectTimeout(3000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    connected = true;
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (cm != null) {
            final NetworkInfo[] netInfoAll = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfoAll) {
                System.out.println("get network type :::" + ni.getTypeName());
                if ((ni.getTypeName().equalsIgnoreCase("WIFI") || ni
                        .getTypeName().equalsIgnoreCase("MOBILE"))
                        && ni.isConnected() && ni.isAvailable()) {
                    connected = true;
                    if (connected) {
                        break;
                    }
                }
            }
        }
        return connected;
    }

    public void startTimer(int time, final TextView v){

        timer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                String time = secondsToMinutesAndSeconds((int) (millisUntilFinished / 1000));
                v.setText(time);
//                jobCompleteTime = ""+ view.getText();
            }

            public void onFinish() {
                v.setText("0:0");

                timeEnds();
            }
        };timer.start();
    }

    private void timeEnds(){
        timer.cancel();
    }

    public String secondsToMinutesAndSeconds(int time) {
        int hours = time / 3600;
        int remainder = (int) time - hours * 3600;
        int minutes = remainder / 60;
        remainder = remainder - minutes * 60;
        int seconds = remainder;
        String result = "";
        if(hours > 0){
            result = String.format("%02d:%02d:%02d",hours, minutes, seconds);
        }
        else{
            result = String.format("%02d:%02d", minutes, seconds);
        }

        return result;
    }

    public void saveStringPreferences(String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("preferenceString", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String loadStringPreferences(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("preferenceString", 0);
        String stringValue = sharedPreferences.getString(key, "Empty");
        return stringValue;
    }

    private final Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
                    + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
                    + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");

    public boolean isEmailValid(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);
            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host,
                    port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    public void callPhone(Context context, String call_number) {
        if (call_number != null && call_number.length() > 0) {
            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
            phoneIntent.setData(Uri.parse("tel:" + call_number));
            context.startActivity(phoneIntent);
        } else {
            System.out.println("call_number is null:" + call_number);
        }
    }

    public String getDeviceId(Context context) {

        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();

    }

    public boolean isExternalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize);
    }

    public String getAvailableExternalMemorySize() {
        if (isExternalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return "ERROR";
        }
    }

    public String getTotalExternalMemorySize() {
        if (isExternalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
        } else {
            return "ERROR";
        }
    }

    public String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public String removeFileExtension(String file) {

        int index = file.lastIndexOf(".");

        return file.substring(0, index);
    }

    public boolean isGpsEnable(Context context) {
        final LocationManager manager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }

        return false;
    }

    public void launchGpsSetting(Context context) {
        Intent setting = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        // setting.setClassName("com.android.settings",
        // "com.android.settings.SecuritySettings");
        showToast("Please! Enable Gps.", context);

        context.startActivity(setting);
    }

    public void enableGps(Context context) {
        // Enable GPS
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        context.sendBroadcast(intent);
    }

    public void disableGps(Context context) {
        // Enable GPS
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", false);
        context.sendBroadcast(intent);
    }

    public boolean isAvailableCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void showToast(String message, Context context){
        if(toast != null)
        {
            try{toast.cancel(); toast = null;}
            catch(Exception e){}
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + mContext.getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        // String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="LC_DP"+".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public File loadUserDp(String pictureName) {
        File pictureFile = getDefaultDpPath(pictureName);
        return pictureFile;
    }

    public  File getDefaultDpPath(String pictureName){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + mContext.getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        // String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName=pictureName+".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public void storeUserDp(Bitmap image, String pictureName) {
        File pictureFile = getDefaultDpPath(pictureName);
        if (pictureFile == null) {
            Log.d("Store", "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("Store", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Store", "Error accessing file: " + e.getMessage());
        }
    }

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public String BitMapToString(Bitmap bitmap){
        if(bitmap ==null){
            return "";
        }
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void saveImageToPrefs(ImageView profileImg){
        profileImg.buildDrawingCache();
        Bitmap bitmap = profileImg.getDrawingCache();
        String bitmapToString = BitMapToString(bitmap);
        saveStringPreferences("profile_image", bitmapToString);
    }

    public Bitmap getImageFromPrefs(){
        String currentDpPath = loadStringPreferences("profile_image");
        Bitmap bMap = null;
        if(!currentDpPath.equalsIgnoreCase("Empty") && currentDpPath !=null){
            bMap = StringToBitMap(currentDpPath);
        }
        return bMap;
    }

    // Show spinning hud
    public void showHud(Boolean isCancelable, Context context)
    {
        hideHud();
        if(context !=null){
            Animation animRotate;
            animRotate = AnimationUtils.loadAnimation(context, R.anim.animation_hud);
            try{
                LayoutInflater li = LayoutInflater.from(context);
                View customAlertView = li.inflate(R.layout.custom_hud, null);


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setView(customAlertView);
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(customAlertView);

                ImageView ivHud = (ImageView) customAlertView.findViewById(R.id.iv_logo);
                ivHud.startAnimation(animRotate);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(isCancelable);
                if(dialog !=null){
                    dialog.show();
                }
            }
            catch(Exception e)
            {
            }
        }
    }
    // Hide spinning hud
    public void hideHud()
    {
        try
        {
            if(this.dialog != null && this.dialog.isShowing()){
                this.dialog.dismiss();
            }

        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            this.dialog = null;
        }
    }

    public void showProgressDialog(Context ctx,String title, String msg){
        if(ctx == null){
            return;
        }
        progressDialog = ProgressDialog.show(ctx, title,
                msg, true);
    }

    public void hideProgressDialog(){
        try {
            if(progressDialog !=null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getDayName(String d){
        String dayNum = getDayNumberFromDate(d);
        int dayNumber = Integer.parseInt(dayNum);
        String result ="";
        switch(dayNumber){
            case 1:
                return result ="Monday";
            case 2:
                return result = "Tuesday";
            case 3:
                return result = "Wednesday";
            case 4:
                return result = "Thursday";
            case 5:
                return result = "Friday";
            case 6:
                return result = "Saturday";
            case 0:
                return result = "Sunday";
            default:
                return result = "";

        }

    }

    public String getDayNumberFromDate(String d){
        Date date = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            date = date_format.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date.getDay()+"";
    }

    public String getTimeDifference(Date date1, Date date2){
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long diff = time2-time1;

        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        int diffInDays = (int) ((time2 - time1) / (1000 * 60 * 60 * 24));
        return String.valueOf(diff);
    }

    public String calculateDatesAndRerunTime(String currentDate, String otherDate){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String result ="";
        try {
            Date date1 = sdf.parse(currentDate);
            Date date2 = sdf.parse(otherDate);

            if(date2.after(date1)){
                result = getTimeDifference(date1,date2);
            }
            if(date2.before(date1)){
                System.out.println("Date is : "+date1.getHours());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public long getDifferenceInMinutes1(Date date1, Date date2){
        long diff = date1.getTime() - date2.getTime();
        long diffInSeconds = diff / 1000L;
        long diffInMinutes = diffInSeconds / 60L;

        return diffInMinutes;
    }

    public long getDifferenceInSeconds(Date date1, Date date2){
        long diff = date1.getTime() - date2.getTime();
        long diffInSeconds = diff / 1000L;
        return diffInSeconds;
    }
    public String getFormattedDateTimeString(String dateTime){
        String dayOfWeek = null;
        String result = "";
        try {
            String[] splittedDateArray = dateTime.split("/");
            String dateOfMonth = splittedDateArray[0];
            Date date1 = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateTime);
            dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date1);
            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date1);
            result = dateOfMonth+" "+monthName+", "+dayOfWeek;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getTimeFromDateTimeString(String dateTime){
        String time = "";
        try {
            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date1 = formatDate.parse(dateTime);
            time = new SimpleDateFormat("hh:mm aaa").format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
    //Time format HH:MM:SS
    public String getCurrentTime() {

        final Calendar c = Calendar.getInstance();

        return(new StringBuilder()
                .append(c.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(c.get(Calendar.MINUTE)).append(":")
                .append(c.get(Calendar.SECOND)).append(" ")).toString();
    }

    public String convertHHMMSSToMilliSeconds(String time){
        String[] token = time.split(":");
        String hrs="";
        String mins = "";
        String secs = "";
        double result = 0;
        if(token.length==3){
            hrs = token[0];
            mins = token[1];
            secs = token[2];
            result = (Integer.parseInt(hrs)*60)+(Integer.parseInt(mins))+(Math.ceil(Double.parseDouble(secs))/60);
        }
        if(token.length == 2){
            hrs = token[0];
            mins = token[1];
            result = (Integer.parseInt(hrs)*60)+(Integer.parseInt(mins));
        }
        if(token.length == 1){
            secs = token[0];
            result = (Math.ceil(Double.parseDouble(secs))/60);
        }

        return String.valueOf(Math.ceil(result));
    }

    //Date format MM/DD/YYYY
    public String getTodaysDate() {

        final Date c = Calendar.getInstance().getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(c);
    }

    //Date format MM/DD/YYYY
    public String getTodaysDateAndTime() {

        final Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(c);
    }
    public String getTimeDifference(String starttime, String endtime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");// 2016-02-12 00:30:00
        long diff = 0L;
        try {
            Date date1 = sdf.parse(endtime);
            Date date2 = sdf.parse(starttime);
            long time1 = date1.getTime();
            long time2 = date2.getTime();

            diff = time1-time2;
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(diff);
    }

    public boolean isDateValidForFutureJob(String currentDate, String otherDate){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        boolean result =false;
        try {
            Date date1 = sdf.parse(currentDate);
            Date date2 = sdf.parse(otherDate);

            if(date2.after(date1)){
                result = true;
            }
            if(date2.before(date1)){
                result = false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getTimeDifferenceFromASpecifiedDate(String sessionStartDate){

        String dateStart = getTodaysDateAndTime();//"01/14/2012 09:29:58";
        //			String dateStop = "01/15/2012 10:31:48";
        long diff = 0;
        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(sessionStartDate);

            //in milliseconds
            diff = d2.getTime() - d1.getTime();

            //				long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(diff);
    }

    public boolean isDateValid(String startDate){

        String endDate = getTodaysDateAndTime();//"2014/09/13 00:00";
        boolean res = false;

        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd")
                    .parse(startDate);
            Date end = new SimpleDateFormat("yyyy-MM-dd")
                    .parse(endDate);

            System.out.println(start);
            System.out.println(end);

            if (start.compareTo(end) > 0) {
                res = true;
                System.out.println("start is after end");
            } else if (start.compareTo(end) == 0) {
                res = true;
                System.out.println("start is equal to end");
            }
             else if (start.compareTo(end) < 0) {
                res = false;
                System.out.println("start is before end");
            }
            else {
                System.out.println("Something weird happened...");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;
    }
/*
    public String getTimeDifference(Date date1, Date date2){
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long diff = time2-time1;

        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        int diffInDays = (int) ((time2 - time1) / (1000 * 60 * 60 * 24));
        return String.valueOf(diff);
    }*/

    public List<Session> getSessionsList() {
        return this.sessionsList;
    }

    public void setSessionsList(List<Session> sList) {
        if(this.sessionsList !=null && this.sessionsList.size() > 0){
            this.sessionsList.clear();
        }
        this.sessionsList = sList;
    }

    public boolean compareDates(String compareStringOne){
        boolean res = false;
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);

        Date date = parseDate(hour + ":" + minute);
        Date dateCompareOne = parseDate(compareStringOne);

        if ( dateCompareOne.before( date ) || dateCompareOne.after( date )) {
            //yada yada
            res = true;
        }
        return res;
    }

    private Date parseDate(String date) {
        SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm");
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    public void showXMPPReConnectPopUp(String title, String message, final Context ctx)
    {
        if(isXMPPReconnectDialogShown){
            return;
        }
        try {
            LayoutInflater li = LayoutInflater.from(currentActivity);
            View customAlertView = li.inflate(R.layout.custom_alert_layout, null);


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(currentActivity);

            alertDialogBuilder.setView(customAlertView);

            //final EditText userInput = (EditText) customAlertView.findViewById(R.id.editTextPromptName);
            final TextView userTextViewTitle = (TextView) customAlertView.findViewById(R.id.tv_alertBox_Title);
            final TextView userTextViewMessage = (TextView) customAlertView.findViewById(R.id.tv_alertBox_message);
            userTextViewTitle.setText(title);
            userTextViewTitle.setGravity(Gravity.CENTER);
            userTextViewMessage.setText(message);
            userTextViewMessage.setGravity(Gravity.CENTER);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setNegativeButton("Connect", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isOnline(mContext)) {
                                String userId = CommonUtils.getInstance(mContext).loadStringPreferences("userid");
                                String userPwd = CommonUtils.getInstance(mContext).loadStringPreferences("userpwd");
//                                CommonUtils.getInstance(mContext).showProgressDialog(mContext, "Learniat Student", "Reconnecting to xmpp server....");
                                XMPPController.getInstance(mContext).loginToXMPPServer(userId, userPwd, "");
                                isXMPPReconnectDialogShown = false;
                                dialog.cancel();
                            } else {
                                Toast.makeText(mContext, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            isXMPPReconnectDialogShown = false;
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            isXMPPReconnectDialogShown = true;


        } catch (Exception e) {
            isXMPPReconnectDialogShown = false;
        }
    }

    public void updateUserState(String status){
        String userId = CommonUtils.getInstance(mContext).loadStringPreferences("userid");
        String url = "<Sunstone><Action><Service>UpdateUserState</Service><UserId>"+userId+"</UserId><StatusId>"+status+"</StatusId><SessionId>2047</SessionId></Action></Sunstone";
        SLAPI.getInstance().StudentWebInterface().updateUserState(url, new Callback<Response>() {
            @Override
            public void success(Response response, Response response1) {
                if(response.getStatus()== 200){
//                    Toast.makeText(mContext, "Status updated successfully.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mContext, "Status update failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(mContext, "Error in status update.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }

    public void setClassTimeExtendMessage(String msg){
        classTimeExtendMessage = msg;
    }

    public String getClassTimeExtendMessage(){
        return classTimeExtendMessage;
    }

    public void setTeacherID(int id){
        teacherId = id;
    }

    public int getTeacherID(){
        return teacherId;
    }

}
