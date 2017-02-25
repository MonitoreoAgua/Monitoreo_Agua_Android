package com.duran.johan.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by johan on 22/2/2017.
 */

public class ActivityLauncher {
     public static void startActivityB(Context context,final Class<? extends Activity> activityToStart) {
            Intent intent = new Intent(context, activityToStart);
            intent.putExtra("sub1","chemistry");//vienen en un vector por parámetro.
            context.startActivity(intent);
        }
}
