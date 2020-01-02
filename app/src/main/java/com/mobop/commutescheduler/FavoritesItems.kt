package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import org.jetbrains.anko.doAsync
/* *************************************************************** */

/* FavoritesItems ************************************************* */
/* Contents of the recycler view (list) of favorites items ******** */
class FavoritesItemsList private constructor(context : Context){
    companion object{
        @Volatile private var INSTANCE : FavoritesItemsList? = null

        fun getSingleton(context : Context) : FavoritesItemsList{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = FavoritesItemsList(context.applicationContext)
                INSTANCE = instance
                return instance
            }
        }
    }

    var favoritesItemsList = ArrayList<Favorite>()

    /* Initialising data into the ArrayList<Items> *************** */
    init{
        doAsync{


            val home = Favorite(
                name = "Home",
                address = "Route des Arsenaux 29, 1700 Fribourg")
            val school = Favorite(
                name = "School",
                address = "Avenue de Provence 6, 1007 Lausanne")
            val work = Favorite(
                name = "Work",
                address = "Route de Morat 135, 1763 Granges-Paccot"
            )

            favoritesItemsList.add(home)
            favoritesItemsList.add(school)
            favoritesItemsList.add(work)
        }
    }
}

class Favorites private constructor(context : Context){
    companion object{
        @Volatile private var INSTANCE : Favorites? = null

        fun getSingleton(context : Context) : Favorites{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Favorites(context.applicationContext)
                INSTANCE = instance
                return instance
            }
        }
    }

    var listFavorites = ArrayList<Commute>()

    /* Initialising data into the ArrayList<Items> *************** */
    init{
        doAsync{

        }
    }
}

data class Favorite(

    var name : String = "",
    var address : String? = ""
)