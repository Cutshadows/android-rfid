package com.appc72_uhf.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.appc72_uhf.app.repositories.ReceptionRepository;
import com.appc72_uhf.app.tools.UIHelper;

import org.json.JSONArray;

public class GetParametersHelper {
    private Context context;
    private String token_access;
    public GetParametersHelper(Context context) {
        this.context = context;
    }

   public void SaveLocationByUser(String URL_COMPLETE, final String EMAIL_USER, int CompanyId){
       final ReceptionRepository receptionRepository=new ReceptionRepository(context);
       getToken();

       HttpHelpers http = new HttpHelpers(context, URL_COMPLETE, "");
       http.addHeader("Authorization", "Bearer "+token_access);
       http.client(Request.Method.GET, "/api/location/getLocationByUserName?userName="+EMAIL_USER+"&companyId="+CompanyId, "application/json; charset=utf-8", null, new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {
               Log.e("response", response);
               try{
                   JSONArray repositoryRe=new JSONArray(response);
                   for (int i=0; i < repositoryRe.length();i++){
                       String nameLocation=repositoryRe.getJSONObject(i).getString("name");
                       int idLocation=repositoryRe.getJSONObject(i).getInt("id");
                       boolean b=receptionRepository.InserParametersLocation(idLocation, nameLocation, EMAIL_USER);
                       if(i==repositoryRe.length()-1 && b){
                           UIHelper.ToastMessage(context, "Sincronización completada", 0);
                       }else if(i==repositoryRe.length()-1 && !b){
                           UIHelper.ToastMessage(context, "Ya se realizo la sincronización", 0);
                       }
                   }
               }catch (Exception ex){
                   UIHelper.ToastMessage(context, "Error : "+ex.getLocalizedMessage(), 5);
               }
            }
           }, new Response.ErrorListener(){
               @Override
               public void onErrorResponse(VolleyError error) {
                   if (error instanceof NetworkError) {
                       UIHelper.ToastMessage(context, "Error de conexion, no hay conexion a internet", 3);
                   } else if (error instanceof ServerError) {
                       UIHelper.ToastMessage(context, "Error de conexion, credenciales invalidas", 3);
                   } else if (error instanceof AuthFailureError) {
                       UIHelper.ToastMessage(context, "Fallo en la autenticacion, vuelva a iniciar sesion.", 3);
                      // authSession();
                   } else if (error instanceof ParseError) {
                       UIHelper.ToastMessage(context, "Error desconocido, intente mas tarde", 3);
                   } else if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                       UIHelper.ToastMessage(context, "Error con el servidor, intente mas tarde!!!", 3);
                   }
               }
           });
}
   public void getToken(){
       SharedPreferences preferencesAccess_token=context.getSharedPreferences("access_token", Context.MODE_PRIVATE);
       String access_token=preferencesAccess_token.getString("access_token", "");
       if(access_token.length()==0){
           Log.e("No data preferences", " Error data empty "+access_token);
       }else{
           token_access=access_token;
       }
   }

}
