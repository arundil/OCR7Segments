package org.ocr.sevensegments.mainActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetActivity extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, OCR7SegmentActivity.class);
            intent.setComponent(new ComponentName(context.getPackageName(), OCR7SegmentActivity.class.getName()));
            
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
     
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
     
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
