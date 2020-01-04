package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.os.AsyncTask
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
/* *************************************************************** */

/* HTTPRequest *************************************************** */
/* This is an ASYNC task for sending an HTTP request and receive * */
/* *** the result, which is then passed to GoogleAPI using the *** */
/* *** function getResults(result) ******************************* */
/* To have access to getResults(), the calling activity has to set */
/* *** its activity to the object created in this class with the * */
/* *** function setActivityContext() ***************************** */
class HTTPRequest : AsyncTask<String, Int, String>(){
    private var mActivity : GoogleAPI? = null

    fun setActivityContext(activity : GoogleAPI){
        mActivity = activity
    }

    override fun doInBackground(vararg params : String) : String?{
        return try{
            params.first().let{
                val url = URL(it)
                val urlConnect = url.openConnection()
                        as HttpURLConnection
                urlConnect.connectTimeout = 700
                publishProgress(100)
                urlConnect.inputStream.bufferedReader().readText()
            }
        } catch (e : Exception){
            null
        }
    }

    override fun onProgressUpdate(vararg values : Int?){
        for(it in values){
            Log.d("onProgressUpdate", it.toString())
        }
    }

    override fun onPostExecute(result : String?){
        Log.d("HTTPRquest", "onPostExecute")
        when{
            result !=
                    null -> {mActivity?.getResults(result)}
            else -> { /* Some error */ }
        }
    }
}