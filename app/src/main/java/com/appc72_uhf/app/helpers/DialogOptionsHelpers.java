package com.appc72_uhf.app.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.appc72_uhf.app.R;
import com.appc72_uhf.app.activities.LoginActivity;

public class DialogOptionsHelpers extends AlertDialog {
    Context mContext;
    public DialogOptionsHelpers(Context context) {
        super(context);
        mContext=context;
    }
    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.ap_dialog_configuration_logout);
        builder.setMessage("Esta seguro que desea cerrar sesión?");
        //builder.setIcon(R.drawable.button_bg_up);
        builder.setNegativeButton(R.string.ap_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(R.string.ap_dialog_acept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferencesAccess_token=getContext().getSharedPreferences("access_token", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit=preferencesAccess_token.edit();
                edit.clear();
                edit.apply();
                SharedPreferences preferencesExpireDate= getContext().getSharedPreferences("expireDate", Context.MODE_PRIVATE);
                SharedPreferences.Editor obj_expireDate=preferencesExpireDate.edit();
                obj_expireDate.remove("expireDate");
                obj_expireDate.apply();
                Intent goToLogin=new Intent(mContext, LoginActivity.class);
                mContext.startActivity(goToLogin);
            }
        });
        builder.create().show();
    }

    public void DialogCommentary(){

    }
    public void DialogSincronizateParameters(){

    }
}
