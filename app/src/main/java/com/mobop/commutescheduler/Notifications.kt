package com.mobop.commutescheduler

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.util.*

class Notifications {

    fun setNotification(mRoute:Commute, checkPoint:Int, activity: Activity) {

        val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java) // AlarmReceiver1 = broadcast receiver
        var mNotificationTime = (mRoute.start_time_UTC!! - checkPoint*60) * 1000//Calendar.getInstance().timeInMillis + 5000 //Set after 5 seconds from the current time.

        alarmIntent.putExtra("checkPoint", checkPoint)
        alarmIntent.putExtra("timestamp", mNotificationTime)
        alarmIntent.putExtra("name", mRoute.name)
        alarmIntent.putExtra("origin", mRoute.start)
        alarmIntent.putExtra("destination", mRoute.arrival)
        alarmIntent.putExtra("departure_time", mRoute.start_time_long)
        alarmIntent.putExtra("arrival_time", mRoute.arrival_time_long)
        alarmIntent.putExtra("duration_in_traffic", mRoute.duration_traffic)



        val calendar = Calendar.getInstance()
        calendar.timeInMillis = mNotificationTime

        // the check point will have to be replaced by the unique ID of the route + checkpoint for each alarm
        val pendingIntent = PendingIntent.getBroadcast(activity, checkPoint, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

    }
}

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Toast.makeText(context, "Alarm, Alarm!!", Toast.LENGTH_SHORT).show()
        val service = Intent(context, NotificationService::class.java)
        service.putExtra("checkPoint", intent.getIntExtra("checkPoint",0))
        service.putExtra("name", intent.getStringExtra("name"))
        service.putExtra("origin", intent.getStringExtra("origin"))
        service.putExtra("destination", intent.getStringExtra("destination"))
        service.putExtra("departure_time", intent.getStringExtra("departure_time"))
        service.putExtra("arrival_time", intent.getStringExtra("arrival_time"))
        service.putExtra("duration_in_traffic", intent.getStringExtra("duration_in_traffic"))
        service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))

        context.startService(service)
    }

}


class NotificationService : IntentService("NotificationService") {
    companion object {

        const val CHANNEL_ID = "com.mobop.commutescheduler.CHANNEL_ID"
        const val CHANNEL_NAME = "Commute Scheduler Notifications"
    }

    private lateinit var mNotification: Notification
    private val mNotificationId: Int = 1000

    init{

    }

    var timestamp: Long = 0
    var name: String=""
    var origin: String=""
    var destination: String=""
    var departure_time: String=""
    var arrival_time: String=""
    var duration_in_traffic: String=""
    var checkPoint: Int=0

    override fun onHandleIntent(intent: Intent?) {




        if (intent != null && intent.extras != null) {
            timestamp = intent.extras!!.getLong("timestamp")
            name = intent.extras!!.getString("name")!!
            origin = intent.extras!!.getString("origin")!!
            destination = intent.extras!!.getString("destination")!!
            departure_time = intent.extras!!.getString("departure_time")!!
            arrival_time = intent.extras!!.getString("arrival_time")!!
            duration_in_traffic = intent.extras!!.getString("duration_in_traffic")!!
            checkPoint = intent.getIntExtra("checkPoint",0)
        }

        val mGoogleAPI=GoogleAPI()
        mGoogleAPI.setService(this )
        mGoogleAPI.requestRoute("Service","test",origin, destination, "Now",false)

    }
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val context = this.applicationContext
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.description = "Alarm messages"
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun routeRequestedReady(mRoute:Commute){

        Log.i("Mydebug",mRoute.duration_traffic)

        if (timestamp > 0) {

            //Create Channel
            createChannel()

            val context = this.applicationContext
            var notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notifyIntent = Intent(this, MainActivity::class.java)

            val title = "Commute Schedule Notification"
            val message = "Your departure is foreseen in " + checkPoint + " minutes" +
                    "\nRoute: " +name +
                    "\nFrom: " +origin +
                    " to: " + destination +
                    "\nEstimated Departure at: " + departure_time +
                    "\nScheduled arrival: " + arrival_time +
                    " Duration: " + duration_in_traffic +
                    "\nNew traject duration: " + mRoute.duration_traffic


            notifyIntent.putExtra("title", title)
            notifyIntent.putExtra("message", message)
            notifyIntent.putExtra("notification", true)

            notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            //notifyIntent.setAction(Intent.ACTION_MAIN);
            //notifyIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp


            val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val res = this.resources
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                mNotification = Notification.Builder(getApplicationContext(), CHANNEL_ID)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(
                        Notification.BigTextStyle()
                            .bigText(message))
                    .setContentText(message).build()
            } else {

                mNotification = Notification.Builder(this)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setStyle(
                        Notification.BigTextStyle()
                            .bigText(message))
                    .setSound(uri)
                    .setContentText(message).build()
            }
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // mNotificationId is a unique int for each notification that you must define
            notificationManager.notify(mNotificationId, mNotification)
        }

    }

}
