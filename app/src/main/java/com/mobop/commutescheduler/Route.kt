package com.mobop.commutescheduler

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import java.util.*
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.SET_NULL
import org.jetbrains.anko.doAsync
import org.joda.time.DateTime


class Route private constructor  (context: Context){

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile private var INSTANCE: Route? = null

        fun getSingleton(context: Context): Route {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Route(context.applicationContext)
                INSTANCE = instance
                return instance
            }
        }
    }





    //var listRoutes = ArrayList<Route>()

    /*************************************************************************
     * Initializes the initial data into the ArrayList<Items>
     *************************************************************************/
    init {
        doAsync {
            //listRoutes.addAll(database.getAll())

        }
    }


    // !!!!!! Ajouter une colonne pour la page avec les icon (icon home avec addresse maison par exemple)
    @Entity(tableName = "address")
    data class Address(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "aid") var aid: Long=0,
        @ColumnInfo(name = "name") var name: String="",
        @ColumnInfo(name = "address") var address: String="",
        @ColumnInfo(name = "actif") var actif: Boolean=true,
        @ColumnInfo(name = "favoris") var favoris: Boolean=false,
        @ColumnInfo(name = "icon") var icon: String="",
        var typeItemA: Int=1
    )




    @Entity(tableName = "path",
        foreignKeys = [
            ForeignKey(
                entity = Address::class,
                parentColumns = ["aid"],
                childColumns = ["dep"],
                onDelete = SET_NULL),
            ForeignKey(
                entity = Address::class,
                parentColumns = ["aid"],
                childColumns = ["arr"],
                onDelete = SET_NULL)
        ])

    // ajoutre option schedule -> du lundi au dimanche
    data class Path(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "pid") var pid: Long=0,
        @ColumnInfo(name = "name") var name: String="",
        @ColumnInfo(name = "dat_time") var time: DateTime,
        @ColumnInfo(name = "distance") var distance: Int=0,
        @ColumnInfo(name = "ttime") var ttime: DateTime,
        @ColumnInfo(name = "alarm") var alarm: Boolean=false,
        @ColumnInfo(name = "actif") var actif: Boolean=true,
        @ColumnInfo(name = "next_update") var next_update: DateTime,
       // @Relation(parentColumn = "aid", entityColumn = "adrId")
       // var address: List<Address>? = null,
        var typeItemP: Int=1

    )


    @Entity(tableName = "alarm"/*,
        foreignKeys = [
            ForeignKey(
                entity = Address::class,
                parentColumns = ["aid"],
                childColumns = ["aid"],
                onDelete = CASCADE)
        ]*/)
    data class Alarm(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "alid") var alid: Long=0,
        @ColumnInfo(name = "name") var name: String="",
        @ColumnInfo(name = "time") var time: DateTime,
        @Relation(parentColumn = "pid", entityColumn = "pathId")
        var path: List<Path>? = null,
        var typeItemAl: Int=1
    )

/*
    class Converter {

        @TypeConverter
        fun fromTimestamp(value: Long?): Date? {
            return if (value == null) null else Date(value)
        }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? {
            return date?.time
        }
    }
*/
}
/*
data class Route (var origin:String,
                  var destination:String,
                  var departure_time:String="",
                  var departure_time_UTC:Long?=null,
                  var arrival_time:String="now",
                  var arrival_time_UTC:Long?=null,
                  var distance:String?=null,
                  var duration:String?=null,
                  var duration_value:Long?=null,
                  var duration_in_traffic:String?=null,
                  var duration_in_traffic_value:Long?=null,
                  var path:MutableList<List<LatLng>> = ArrayList(),
                  var start_address:String?=null,
                  var start_address_LatLng:LatLng?=null,
                  var end_address:String?=null,
                  var end_address_LatLng:LatLng?=null,
                  var raw_data:String?=null,
                  var errorTraffic:Long?=null)
*/

@Dao
public interface RouteDao{

    // Route

    @Query("SELECT * FROM path")
    fun getAll(): List<Route>

    @Insert
    fun insertAll(Path: Route): Long

    @Update
    fun updateAll(vararg Path: Route)

    //@Query("DELETE FROM todoitemcontent WHERE uid = :uid")
    //fun deleteItem(vararg uid: Long): Int

    @Delete
    fun deleteAll(vararg Path: Route)

    @Query("SELECT * FROM path WHERE pid=:routeid")
    fun getRoute(routeid: Long) : Route

    // Address

    @Query("SELECT * FROM address")
    fun getAllAddress(): List<Route.Address>

    @Insert
    fun insertAddress(address: Route.Address): Long

    @Update
    fun updateAddress(vararg address: Route.Address)

    @Delete
    fun deleteAll(vararg address: Route.Address)

    @Query("SELECT * FROM address WHERE aid=:addressId")
    fun getAddress(addressId: Long) : Route.Address

    // Alarm

    @Query("SELECT * FROM alarm")
    fun getAllAlarm(): List<Route.Alarm>

    @Insert
    fun insertAlarm(alarm: Route.Alarm): Long

    @Update
    fun updateAlarm(vararg alarm: Route.Alarm)

    @Delete
    fun deleteAllalarm(vararg alarm: Route.Alarm)

    @Query("SELECT * FROM alarm WHERE alid=:alarmId")
    fun getAlarm(alarmId: Long) : Route.Alarm
}


@Database(
    entities = arrayOf(Route::class),
    version = 1)
public abstract class AppDatabase : RoomDatabase() {
    abstract fun routeDao(): RouteDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {

                val instance = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java,
                    "route_database").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}