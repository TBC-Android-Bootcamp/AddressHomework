package com.example.homeworkproject.activities

import android.app.Activity
import androidx.lifecycle.Observer
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homeworkproject.MainViewModel
import com.example.homeworkproject.R
import com.example.homeworkproject.adapters.Adapter
import com.example.homeworkproject.databinding.ActivityMainBinding
import com.example.homeworkproject.interfaces.CustomCallback
import com.example.homeworkproject.models.ListingAddressComponents
import com.example.homeworkproject.models.MainModel
import com.example.homeworkproject.network.ApiRequest
import com.example.homeworkproject.network.Request
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.json.JSONObject

class ChooseAddressActivity : AppCompatActivity() {
    lateinit var adapter: Adapter
    var itemslist= mutableListOf<MainModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val model= ViewModelProvider(this)[MainViewModel::class.java]
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        binding.viewmodel= model
        binding.lifecycleOwner=this
        addadapter()
        init(model)

    }
    private fun init(model: MainViewModel){
        var countryid= intent.extras?.getString("countryid")
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //toolbar.navigationIcon!!.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        btnDone.text="Add"
        tvTitle.text = "Area Of Interest"
        val map= mutableMapOf<String,String>()
        model.input.observe(this, Observer {
            if(it.isEmpty()) itemslist.clear()
            map["input"]=it
            map["key"]="AIzaSyBADWUmhO9XNVF_-qSZR6RQWcoHfSpAr6E"
            map["language"]="en"
            map["components"]="country:$countryid"
            ApiRequest.getRequest(
                "autocomplete",
                map,
                object : CustomCallback {
                    override fun onResponse(value: String?) {
                        itemslist.clear()
                        val jsonobj = JSONObject(value)
                        if (jsonobj.has("predictions")) {
                            val jsonarry = jsonobj.getJSONArray("predictions")
                            (0 until jsonarry.length()).forEach {
                                val jsonobject = jsonarry.getJSONObject(it)
                                itemslist.add(
                                    MainModel(
                                        jsonobject.getString("description"),
                                        jsonobject.getString("place_id")
                                    )
                                )
                                addadapter()

                            }
                        }
                    }

                    override fun onFailure(value: String?) {
                        Toast.makeText(this@ChooseAddressActivity, value, Toast.LENGTH_LONG).show()
                    }
                },
                this
            )
        })
        adapter.notifyDataSetChanged()
        btnDone.setOnClickListener(){
            getAddressDetail(itemslist[adapter.pos].place_id)

        }
    }
    private fun addadapter(){
        adapter= Adapter(itemslist)
        recyclerview.layoutManager= LinearLayoutManager(this)
        recyclerview.adapter=adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home){
            super.onBackPressed()
        }
        return true
    }
    fun getAddressDetail(
        placeID: String
        //callback: FutureCallback<MutableMap<String, String>>
    ) {
        val parameter: HashMap<String, String> = HashMap()
        parameter["placeid"] = placeID
        parameter["key"] = "AIzaSyBADWUmhO9XNVF_-qSZR6RQWcoHfSpAr6E"
        Request.getRequest("details", parameter, object : CustomCallback {
            override fun onFailure(value: String?) {

            }

            override fun  onResponse(result: String?) {
                val resultJson = JSONObject(result)
                Log.d("getAddressDetail ", result)
                if (resultJson.has("status")) {
                    val resultsMap = HashMap<String, String>()
                    if (resultJson.getString("status") == "OK")
                        if (resultJson.has("result"))
                            if (resultJson.getJSONObject("result").has("address_components")) {
                                val listingAddressComponents: List<ListingAddressComponents>? =
                                    Gson().fromJson(
                                        resultJson.getJSONObject("result")
                                            .getJSONArray("address_components").toString(),
                                        Array<ListingAddressComponents>::class.java
                                    ).toList()
                                var lat = 0.0
                                var lng = 0.0
                                if (resultJson.getJSONObject("result").has("geometry")) {
                                    val location =
                                        resultJson.getJSONObject("result").getJSONObject("geometry")
                                            .getJSONObject("location")
                                    lat = location.getDouble("lat")
                                    lng = location.getDouble("lng")
                                }

                                resultsMap["lat"] = lat.toString()
                                resultsMap["lng"] = lng.toString()
                                for (item in listingAddressComponents!!) {
                                    when {
                                        item.types.contains("street_number") -> resultsMap["streetNumber"] =
                                            item.long_name
                                        item.types.contains("country") -> resultsMap["county"] =
                                            item.long_name
                                        item.types.contains("postal_code") -> resultsMap["zipcode"] =
                                            item.long_name
                                        item.types.contains("locality") -> resultsMap["city"] =
                                            item.long_name
                                        item.types.contains("route") -> resultsMap["street"] =
                                            item.long_name
                                        item.types.contains("administrative_area_level_1") -> resultsMap["state"] =
                                            item.long_name
                                    }
                                }
                                // return  resultsMap
                            }
                    val adress=itemslist[adapter.pos].description
                    val intent=intent
                    intent.putExtra("adress",adress)
                    intent.putExtra("details",resultsMap)
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
            }


        },this)
    }
}