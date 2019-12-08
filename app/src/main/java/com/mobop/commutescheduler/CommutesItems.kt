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
            arrival = "School",
            duration = "01:12")
        val homeToWork = Commute(
            name = "Home2Work",
            start = "Home",
            arrival = "Work",
            duration = "00:25")
        val longNameTest = Commute(
            name = "This should be long enough to" +
                    "fill the screen to make sure ellipsis works",
            start = "Wherever",
            arrival = "Wherever",
            duration = "00:00")
        val longParamTest = Commute(
            name = "A test for parameters",
            start = "That one place that has a very long name",
            arrival = "That other place that has a very long name",
            duration = "00:00")

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
    var errorTraffic : Long? = null
)