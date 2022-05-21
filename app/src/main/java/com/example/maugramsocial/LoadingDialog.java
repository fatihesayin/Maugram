package com.example.maugramsocial;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingDialog {
     Activity activity;
     AlertDialog dialog;

     LoadingDialog(Activity myActivity){

          activity = myActivity;
     }
     public void startLoadingDialog(){
          AlertDialog.Builder builder = new AlertDialog.Builder(activity);
          LayoutInflater inflater = activity.getLayoutInflater();
          builder.setView(inflater.inflate(R.layout.custom_dialog,null));
          builder.setCancelable(false);
          dialog = builder.create();
          dialog.show();
     }
     public void stopLoadingDialog(){
          dialog.dismiss();
     }
}
