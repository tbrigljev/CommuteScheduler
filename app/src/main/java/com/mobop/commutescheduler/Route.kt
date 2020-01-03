/*

package com.mobop.commutescheduler

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import java.util.*
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.SET_NULL
import org.jetbrains.anko.doAsync
import org.joda.time.DateTime


class Route private constructor  (context: Context) {

    var itemsList = ArrayList<Path>()
    var database = AppDatabase.getDatabase(context).routeDao()


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: Route? = null

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

    */
/*************************************************************************
     * Initializes the initial data into the ArrayList<Items>
     *************************************************************************//*

    init {
        doAsync {
            itemsList.addAll(database.getAll())
            itemsList.add(Path(1,"PROFESSIONAL", "2020-01-15 08:00:00", "2020-01-15 08:00:00", "test1", "test2","101",  false, "2020-01-15 08:00:00",true))
            itemsList.add(Path(2,"PRIVATE", "2020-01-15 08:00:00", "2020-01-15 08:00:00", "test3","test4","20", false, "2020-01-15 08:00:00",true))

            // Arrangement of the Array to separate by categories. The typeItem is also used
            // to order the header, the noItems item and the rest of the items per category
            itemsList.sortWith(compareBy({ it.dep_time }, { it.arr_time }))
        }
    }

}




    @Entity(tableName = "path" )

    // ajoutre option schedule -> du lundi au dimanche
    data class Path(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "pid") var pid: Long = 0,
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "dep_time") var dep_time: String="",
        @ColumnInfo(name = "arr_time") var arr_time: String = "",
        @ColumnInfo(name = "dep_add") var dep_add: String="",
        @ColumnInfo(name = "arr_add") var arr_add: String = "",
        @ColumnInfo(name = "distance") var distance: String? = "",
        @ColumnInfo(name = "alarm") var alarm: Boolean = false,
        @ColumnInfo(name = "next_update") var next_update: String = "",
        @ColumnInfo(name = "active") var active: Boolean = true,
        // @Relation(parentColumn = "aid", entityColumn = "adrId")
        // var address: List<Address>? = null,

        var typeItem: Int=1
    )



    @Dao
    interface RouteDao {

        // Route

        @Query("SELECT * FROM path")
        fun getAll(): List<Path>

        @Insert
        fun insertAll(path: Path): Long

        @Update
        fun updateAll(vararg path: Path)

        //@Query("DELETE FROM todoitemcontent WHERE uid = :uid")
        //fun deleteItem(vararg uid: Long): Int

        @Delete
        fun deleteAll(vararg path: Path)

        @Query("SELECT * FROM path WHERE pid=:pathId")
        fun getRoute(pathId: Long): Path


    }


    @Database(
        entities = arrayOf(Path::class),
        version = 1
    )
    abstract class AppDatabase : RoomDatabase() {
        abstract fun routeDao(): RouteDao

        companion object {
            // Singleton prevents multiple instances of database opening at the
            // same time.
            @Volatile
            private var INSTANCE: AppDatabase? = null

            fun getDatabase(context: Context): AppDatabase {
                val tempInstance = INSTANCE
                if (tempInstance != null) {
                    return tempInstance
                }
                synchronized(this) {

                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "route_database"
                    ).build()
                    INSTANCE = instance
                    return instance
                }
            }
        }
    }

*/
