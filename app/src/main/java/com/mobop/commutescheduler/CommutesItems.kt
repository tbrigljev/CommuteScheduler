package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import org.jetbrains.anko.doAsync
import com.google.android.gms.maps.model.LatLng
/* *************************************************************** */

/* CommutesItems ************************************************* */
/* Contents of the recycler view (list) of commutes items ******** */
class CommutesItemsList{
    var commutesItemsList = ArrayList<Commute>()

    init{
        val homeToSchool = Commute(
            name = "Home2School",
            start = "Home",
            start_address = "Route des Arsenaux 29, 1700 Fribourg",
            start_time = "07:12",
            arrival = "School",
            arrival_address = "Avenue de Provence 6, 1007 Lausanne",
            arrival_time = "08:24",
            duration = "01:12",
            reminder_on = true,
            reminder_tune = "Kimmunicator",
            alarm_on = false,
            alarm_time = "20 min",
            alarm_tune = "Ribbit")
        val homeToWork = Commute(
            name = "Home2Work",
            start = "Home",
            start_address = "Route des Arsenaux 29, 1700 Fribourg",
            start_time = "06:35",
            arrival = "Work",
            arrival_address = "Route de Morat 135, 1763 Granges-Paccot",
            arrival_time = "07:00",
            duration = "00:25",
            reminder_on = false,
            reminder_tune = "Kimmunicator",
            alarm_on = true,
            alarm_time = "10 min",
            alarm_tune = "Ribbit")
        val longNameTest = Commute(
            name = "This should be long enough to " +
                    "fill the screen to make sure ellipsis works",
            start = "Wherever",
            start_address = "Street of Road, 10000 City",
            start_time = "00:00",
            arrival = "Wherever",
            arrival_address = "Avenue of Path, 20000 City",
            arrival_time = "00:00",
            duration = "00:00",
            reminder_on = false,
            reminder_tune = "DING",
            alarm_on = false,
            alarm_time = "0 min",
            alarm_tune = "DONG")
        val longParamTest = Commute(
            name = "A test for parameters",
            start = "That one place that has a very long name",
            start_address = "Place of Bridge, 30000 City",
            start_time = "00:00",
            arrival = "That other place that has a very long name",
            arrival_address = "Alley of Way, 40000 City",
            arrival_time = "00:00",
            duration = "00:00",
            reminder_on = true,
            reminder_tune = "DING",
            alarm_on = false,
            alarm_time = "0 min",
            alarm_tune = "DONG")

        commutesItemsList.add(homeToWork)
        commutesItemsList.add(homeToSchool)
        commutesItemsList.add(longNameTest)
        commutesItemsList.add(longParamTest)
    }
}

/*data class CommutesItemsElement(
    var commutesName : String,
    var commutesStart : String,
    var commutesEnd : String,
    var commutesTime : String)*/

class Commutes private constructor(context : Context){
    companion object{
        @Volatile private var INSTANCE : Commutes? = null

        fun getSingleton(context : Context) : Commutes{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Commutes(context.applicationContext)
                INSTANCE = instance
                return instance
            }
        }
    }

    var listCommutes = ArrayList<Commute>()

    /* Initialising data into the ArrayList<Items> *************** */
    init{
        doAsync{

        }
    }
}

data class Commute(

    var name : String? = "",

    var start : String = "",
    var start_address : String? = null,
    var start_address_LatLng : LatLng? = null,

    var arrival : String = "",
    var arrival_address : String? = null,
    var arrival_address_LatLng : LatLng? = null,

    var start_time : String = "",
    var start_time_UTC : Long? = null,

    var arrival_time : String = "",
    var arrival_time_UTC : Long? = null,

    var distance : String? = null,
    var duration : String? = null,
    var duration_val : Long? = null,
    var duration_traffic : String? = null,
    var duration_traffic_val : Long? = null,

    var path : MutableList<List<LatLng>> = ArrayList(),
    var raw_data : String? = null,
    var errorTraffic : Long? = null,

    var reminder_on : Boolean = false,
    var reminder_tune : String? = null,

    var alarm_on : Boolean = false,
    var alarm_time : String? = null,
    var alarm_tune : String? = null
)