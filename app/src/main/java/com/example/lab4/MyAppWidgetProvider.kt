package com.example.lab4

import android.app.*
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

public class MyAppWidgetProvider : AppWidgetProvider() {

    companion object {
        val days : MutableMap<Int, Long> = mutableMapOf(-1 to 0L)
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (id in appWidgetIds!!) {
            if (!days.containsKey(id)) {
                days[id] = 0
            }
            updateWidget(context, appWidgetManager, id)
        }
    }

    fun updateWidget(context : Context?, appWidgetManager : AppWidgetManager?, id : Int) {
        val widgetViews = RemoteViews(context?.packageName, R.layout.example_appwidget)

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("widgetID", id)
        val pIntent = PendingIntent.getActivity(context, id, intent, 0)
        widgetViews.setOnClickPendingIntent(R.id.textView, pIntent)

        widgetViews.setTextViewText(R.id.textView, "${days[id]} days")

        if (days[id] != 0L) {
            val am = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent2 = Intent(context, MyAppWidgetProvider::class.java)
            intent2.action = "CHANGE_DAYS"
            intent2.putExtra("widgetID", id)
            val pIntent2 = PendingIntent.getBroadcast(context, id, intent2, 0)

            val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000L +
                    Calendar.getInstance().get(Calendar.MINUTE) * 60 * 1000 +
                    Calendar.getInstance().get(Calendar.SECOND) * 1000
            val trigger = 24 * 60 * 60 * 1000 - time
            am.set(AlarmManager.RTC, trigger, pIntent2)
        }

        appWidgetManager?.updateAppWidget(id, widgetViews)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == "DATE_SET") {
            val id = intent.getIntExtra("widgetID", 0)
            days[id] = intent.getLongExtra("days", 0)
            val widgetViews = RemoteViews(context?.packageName, R.layout.example_appwidget)
            widgetViews.setTextViewText(R.id.textView, "${days[id]} days")
            AppWidgetManager.getInstance(context).updateAppWidget(id, widgetViews)
        }
        else if (intent?.action == "CHANGE_DAYS") {
            val id = intent.getIntExtra("widgetID", 0)
            val widgetViews = RemoteViews(context?.packageName, R.layout.example_appwidget)
            days[id] = days[id]!! - 1
            widgetViews.setTextViewText(R.id.textView, "${days[id]} days")
            if (days[id] == 0L) {
                val am = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent2 = Intent(context, MyAppWidgetProvider::class.java)
                intent2.action = "SEND_NOTIFICATION"
                intent2.putExtra("widgetID", id)
                val pIntent = PendingIntent.getBroadcast(context, id, intent2, 0)
                am.set(AlarmManager.RTC, 9 * 60 * 60 * 1000, pIntent)
            }
            AppWidgetManager.getInstance(context).updateAppWidget(id, widgetViews)
        }
        else if (intent?.action == "SEND_NOTIFICATION") {
            val id = intent.getIntExtra("widgetID", 0)
            val builder = NotificationCompat.Builder(context!!, "1")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Notification")
                .setContentText("Event has occurred")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Main channel"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("1", name, importance)
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            val nm = NotificationManagerCompat.from(context)
            nm.notify(id, builder.build())
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

}