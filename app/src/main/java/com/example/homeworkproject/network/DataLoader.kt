package com.example.homeworkproject.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.homeworkproject.interfaces.CustomCallback
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object DataLoader {
    private const val CODE_200=200
    private const val CODE_201=201
    private const val CODE_400=400
    private const val CODE_401=401
    private const val CODE_404=404
    private const val CODE_500=500
    private const val CODE_204=204

    var  METHOD="90aa5377-649b-4e39-ab7c-fa6a21a6882d"

    var retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl("https://run.mocky.io/v3/")
        .build()


    var service = retrofit.create(
        Apiservice::class.java)



    fun getRequest(path :String,callback: CustomCallback, context: Context){
        val call= service.Getrequest(path)
        call.enqueue(
            calll(
                callback,
                context
            )
        )

    }

    private fun calll(callback: CustomCallback, context: Context) =  object : Callback<String> {
        override fun onFailure(call: Call<String>, t: Throwable) {
            callback.onFailure(t.message.toString())
        }

        override fun onResponse(call: Call<String>, response: Response<String>) {


            val responseCode=response.code()
            if(responseCode== CODE_200 ||responseCode== CODE_201){
                callback.onResponse(response.body().toString())
            }
            else if(responseCode== CODE_400){
                try {
                    val jsonobject= JSONObject(response.errorBody()?.string())
                    if(jsonobject.has("error")){
                        Toast.makeText(context,"${jsonobject.getString("error")}", Toast.LENGTH_SHORT).show()

                    }
                }
                catch (ex: JSONException){
                    Toast.makeText(context,"${ex.message.toString()}", Toast.LENGTH_SHORT).show()
                    Log.d("response", "${ex.message.toString()}")
                }
            }
            else if(responseCode== CODE_401){
                Toast.makeText(context,"user not authorized", Toast.LENGTH_SHORT).show()

            }
            else if(responseCode== CODE_404){
                Toast.makeText(context,"user not found", Toast.LENGTH_SHORT).show()
            }
            else  if(responseCode== CODE_500){
                Toast.makeText(context,"server not responding", Toast.LENGTH_SHORT).show()

            }
            else if(responseCode== CODE_204){
                Toast.makeText(context,"no content", Toast.LENGTH_SHORT).show()

            }


        }

    }



    interface Apiservice {

        @GET("{path}")
        fun Getrequest(@Path("path") path: String): Call<String>
    }
}
