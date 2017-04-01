package com.duran.johan.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


/**
 * Created by johan on 22/2/2017.
 */

public class ActivityLauncher {

    //método para iniciar una nueva actividad en caso de ser necesario borrar la actual flag = 1 caso contrario 0
     public static void startActivityB(Context context,final Class<? extends Activity> activityToStart, boolean flag) {
            Intent intent = new Intent(context, activityToStart);
         if (flag){
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         }
            //intent.putExtra("sub1","chemistry");//vienen en un vector por parámetro.
            context.startActivity(intent);
        }


}
