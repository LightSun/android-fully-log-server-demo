package com.heaven7.android.log_server.demo;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by heaven7 on 2016/7/25.
 */
public class PermissionChecker {

    private final String[] mRequestPermissions;
    private final int[] mRequestCodes;
    private int mCheckingIndex;

    private final ICallback mCallback;
    private final Activity mActivity;

    public PermissionChecker (Activity activity,String[] requestPermissions, int[] requestCodes, ICallback callback){
        if(requestPermissions==null || requestCodes==null){
            throw new NullPointerException();
        }
        if(requestPermissions.length != requestCodes.length){
            throw new IllegalArgumentException("requestPermissions.length != requestCodes.length");
        }
        this.mActivity = activity;
        this.mRequestPermissions = requestPermissions;
        this.mRequestCodes = requestCodes;
        this.mCallback = callback;
    }

    public void requestPermission(){
        Activity activity = this.mActivity;
        String permission = mRequestPermissions[mCheckingIndex];
        int requestCode = mRequestCodes[mCheckingIndex];
        int count = mRequestPermissions.length;
        if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{ permission }, requestCode);
        }else {
            if(Build.VERSION.SDK_INT < 23){
                   //check request end
                if(count-1 > mCheckingIndex){
                    //request next
                    mCheckingIndex += 1;
                    requestPermission();
                }else{
                    //request end
                    onRequestPermissionResult(permission, requestCode, true);
                }
            }else {
                //third app intercept
                //TODO
            }
        }
    }

    private void onRequestPermissionResult(String requestPermission, int requestCode, boolean success) {

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
         if(mRequestCodes[mCheckingIndex] == requestCode){
              if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                  // request failed and request list end
                  if(mRequestPermissions.length -1 > mCheckingIndex){
                      onRequestPermissionResult(mRequestPermissions[mCheckingIndex], requestCode, false);
                  }
              }else{
                  if(mRequestPermissions.length -1 > mCheckingIndex){
                      mCheckingIndex += 1;
                      requestPermission();
                  }else{
                      onRequestPermissionResult(mRequestPermissions[mCheckingIndex], requestCode, true);
                  }
              }
         }
    }

    public interface ICallback{

    }
    public static class RunnableResultChecker{

        public void check(){
            boolean success = false;
            try{
                run();
                success = true;
            }catch (Exception e){
                onException(e);
            }finally {
                if(success){
                    onSuccess();
                }
            }
        }

        protected void onSuccess() {

        }

        private void run() {

        }

        private void onException(Exception e) {

        }
    }
/**
 *  boolean success = false;
 try {
 // Logger.i(TAG, "requestAudioPermission", "begin openAudio() ...");
 doDemoAudio();
 success = true;
 } catch (Exception e) {
 Logger.i(TAG, "requestAudioPermission", "open audio record failed...exception = "+ e.toString());
 mCallback.onRequestAudioFailed(mRequestedCamera);
 mRequestedCamera = false;
 }finally {
 if(success) {
 mAudioChecked = true;
 doWithRequestAudioSuccess();
 }
 }
 */
}
