package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import org.jetbrains.anko.doAsync
import com.google.android.gms.maps.model.LatLng
import java.util.*
import androidx.room.*
/* *************************************************************** */

/* CommutesItems ************************************************* */
/* Contents of the recycler view (list) of commutes items and **** */
/* *** favorites items ******************************************* */

class CommutesItemsList private constructor(context : Context){

    var commutesItemsList = ArrayList<Commute>()
    var favoritesItemsList = ArrayList<Favorite>()
    var database = AppDatabase.getDatabase(context).routeDao()

    companion object{
        @Volatile private var INSTANCE : CommutesItemsList? = null

        fun getSingleton(context : Context) : CommutesItemsList{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = CommutesItemsList(
                    context.applicationContext)
                INSTANCE = instance
                return instance
            }
        }
    }

    /* Initialising data into the ArrayList<Items> *************** */
    init{
        doAsync{
            commutesItemsList.addAll(database.getAll())
            favoritesItemsList.addAll(database.getAllFavorite())


            // Arrangement of the Array to separate by categories. The typeItem is also used
            // to order the header, the noItems item and the rest of the items per category
            commutesItemsList.sortWith(compareBy(
                    { it.start_time_long },
                    { it.arrival_time_long }))

            /*
            val homeToSchool = Commute(
                name = "Home2School",
                start = "",
                start_address = "Route des Arsenaux 29, 1700 Fribourg",
                arrival = "",
                arrival_address = "Avenue de Provence 6, 1007 Lausanne",
                arrival_time_short = "on 15.01.2020, at 08:00",
                arrival_time_long = "2020-01-15 08:00:00",
                days = mutableListOf(R.string.Tuesday, R.string.Thursday),
                duration = "01:12",
                duration_val = 999/*,
                reminder_on = true,
                reminder_tune = "Kimmunicator",
                alarm_on = false,
                alarm_time = "20 min",
                alarm_tune = "Ribbit"*/)
            val homeToWork = Commute(
                name = "Home2Work",
                start = "Home",
                start_address = "Route des Arsenaux 29, 1700 Fribourg",
                arrival = "Work",
                arrival_address = "Route de Morat 135, 1763 Granges-Paccot",
                arrival_time_short = "on 20.02.2020, at 07:00",
                arrival_time_long = "2020-02-20 07:00:00",
                days = mutableListOf(R.string.Monday, R.string.Wednesday, R.string.Friday),
                duration = "00:25",
                duration_val = 999/*,
                reminder_on = false,
                reminder_tune = "Kimmunicator",
                alarm_on = true,
                alarm_time = "10 min",
                alarm_tune = "Ribbit"*/)
            val longNameTest = Commute(
                name = "This should be long enough to " +
                        "fill the screen to make sure ellipsis works",
                start = "Wherever",
                start_address = "Street of Road, 10000 City",
                arrival = "Wherever",
                arrival_address = "Avenue of Path, 20000 City",
                arrival_time_short = "on 15.01.2020, at 08:24",
                arrival_time_long = "2020-01-15 08:24:00",
                days = mutableListOf(R.string.Saturday, R.string.Sunday),
                duration = "00:00",
                duration_val = 999/*,
                reminder_on = false,
                reminder_tune = "DING",
                alarm_on = false,
                alarm_time = "0 min",
                alarm_tune = "DONG"*/)
            val longParamTest = Commute(
                name = "A test for parameters",
                start = "That one place that has a very long name",
                start_address = "Place of Bridge, 30000 City",
                arrival = "That other place that has a very long name",
                arrival_address = "Alley of Way, 40000 City",
                days = mutableListOf(R.string.Sunday),
                arrival_time_short = "on 15.01.2020, at 08:24",
                arrival_time_long = "2020-01-15 08:24:00",
                duration = "00:00",
                duration_val = 999/*,
                reminder_on = true,
                reminder_tune = "DING",
                alarm_on = false,
                alarm_time = "0 min",
                alarm_tune = "DONG"*/)

            //commutesItemsList.add(homeToWork)
            //commutesItemsList.add(homeToSchool)
            //commutesItemsList.add(longNameTest)
            //commutesItemsList.add(longParamTest)
            */

            val home = Favorite(name = "Home", address = "")
            val school = Favorite(name = "School", address = "")
            val work = Favorite(name = "Work", address = "")
            val shopping = Favorite(name = "Shopping", address = "")
            val restaurant = Favorite(name = "Restaurant", address = "")
            val club = Favorite(name = "Club", address = "")
            val bar = Favorite(name = "Bar", address = "")
            val friends = Favorite(name = "Friends", address = "")
            val kids = Favorite(name = "Kids", address = "")
            val gym = Favorite(name = "Gym", address = "")
            val gasStation = Favorite(name = "Gas Station", address = "")
            val train = Favorite(name = "Train", address = "")
            val bank = Favorite(name = "Bank", address = "")
            val vacation = Favorite(name = "Vacation", address = "")
            val currentLocation = Favorite(name = "CurrentLocation", address = "")

            val fav = ArrayList<String>()
            commutesList!!.favoritesItemsList.forEach {fav.add(it.name)}

            if(fav!!.contains(club.name) == false) database.insertAllFavorite(club)
            if(fav!!.contains(bar.name) == false)database.insertAllFavorite(bar)
            if(fav!!.contains(restaurant.name) == false)database.insertAllFavorite(restaurant)
            if(fav!!.contains(friends.name) == false)database.insertAllFavorite(friends)
            if(fav!!.contains(kids.name) == false)database.insertAllFavorite(kids)
            if(fav!!.contains(gym.name) == false)database.insertAllFavorite(gym)
            if(fav!!.contains(gasStation.name) == false)database.insertAllFavorite(gasStation)
            if(fav!!.contains(shopping.name) == false)database.insertAllFavorite(shopping)
            if(fav!!.contains(train.name) == false)database.insertAllFavorite(train)
            if(fav!!.contains(bank.name) == false)database.insertAllFavorite(bank)
            if(fav!!.contains(work.name) == false)database.insertAllFavorite(work)
            if(fav!!.contains(home.name) == false)database.insertAllFavorite(home)
            if(fav!!.contains(vacation.name) == false)database.insertAllFavorite(vacation)
            if(fav!!.contains(school.name) == false)database.insertAllFavorite(school)
            if(fav!!.contains(currentLocation.name) == false)database.insertAllFavorite(currentLocation)
            favoritesItemsList.clear()
            favoritesItemsList.addAll(database.getAllFavorite())
        }
    }
}

@Entity(tableName = "Commute" )
// Option schedule to add -> weeks of the day
data class Commute(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "pid") var pid : Long = 0,

    @ColumnInfo(name = "name") var name : String = "",

    @ColumnInfo(name = "start") var start : String = "",
    @ColumnInfo(name = "start_address")  var start_address : String = "",

    @ColumnInfo(name = "arrival") var arrival : String = "",
    @ColumnInfo(name = "arrival_address") var arrival_address : String = "",

    @ColumnInfo(name = "start_time_short") var start_time_short : String = "",
    @ColumnInfo(name = "start_time_long") var start_time_long : String = "",
    @ColumnInfo(name = "start_time_UTC") var start_time_UTC : Long? = null,

    @ColumnInfo(name = "arrival_time_short") var arrival_time_short : String = "",
    @ColumnInfo(name = "arrival_time_long") var arrival_time_long : String = "",
    @ColumnInfo(name = "arrival_time_UTC") var arrival_time_UTC : Long? = null,

    @ColumnInfo(name = "distance") var distance : String? = null,
    @ColumnInfo(name = "duration") var duration : String? = null,
    @ColumnInfo(name = "duration_val") var duration_val : Long? = null,
    @ColumnInfo(name = "duration_traffic") var duration_traffic : String? = null,
    @ColumnInfo(name = "duration_traffic_val") var duration_traffic_val : Long? = null,

    @ColumnInfo(name = "raw_data") var raw_data : String? = null,
    @ColumnInfo(name = "errorTraffic") var errorTraffic : Long? = null,

    @ColumnInfo(name = "alarm") var alarm : Boolean = false,
    @ColumnInfo(name = "next_update") var next_update : String = "",
    @ColumnInfo(name = "active") var active : Boolean = true,


    var typeItem : Int = 1,
    @Ignore var start_address_LatLng : LatLng? = null,
    @Ignore var arrival_address_LatLng : LatLng? = null,

    @Ignore var days : MutableList<Int?> = ArrayList(),
    @Ignore var path : MutableList<List<LatLng>> = ArrayList()
)

@Entity(tableName = "Favorite" )
data class Favorite(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "pid") var pid : Long = 0,
    @ColumnInfo(name = "name") var name : String = "",
    @ColumnInfo(name = "address") var address : String = "",
    var typeItem : Int = 1
)

@Dao
interface RouteDao {
    // Commute
    @Query("SELECT * FROM Commute")
    fun getAll() : List<Commute>

    @Insert
    fun insertAll(path : Commute) : Long

    @Update
    fun updateAll(vararg path : Commute)

    @Delete
    fun deleteAll(vararg path : Commute)

    @Query("SELECT * FROM Commute WHERE pid = :pathId")
    fun getRoute(pathId : Long) : Commute

    // Favorite
    @Query("SELECT * FROM Favorite")
    fun getAllFavorite() : List<Favorite>

    @Insert
    fun insertAllFavorite(path : Favorite) : Long

    @Update
    fun updateAllFavorite(vararg path : Favorite)

    @Query("DELETE FROM Favorite WHERE pid = :pid")
    fun deleteItem(vararg pid : Long) : Int

    @Delete
    fun deleteAllFavorite(vararg path : Favorite)

    @Query("SELECT * FROM Favorite WHERE pid = :pathId")
    fun getFavorite(pathId : Long) : Favorite
}

@Database(
    //entities = arrayOf(Commute::class),
    entities = [Commute::class, Favorite::class],
    version = 1
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun routeDao() : RouteDao

    companion object {
        // Singleton prevents multiple instances of database
        // opening at the same time.
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context : Context) : AppDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "CommuteScheduler_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
