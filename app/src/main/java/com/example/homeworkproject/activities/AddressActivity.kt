package com.example.homeworkproject.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.example.homeworkproject.R
import com.example.homeworkproject.interfaces.CustomCallback
import com.example.homeworkproject.models.CountryModel
import com.example.homeworkproject.network.DataLoader
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_address.*
import kotlinx.android.synthetic.main.custom_chooser.view.*
import kotlinx.android.synthetic.main.dialog_window.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.json.JSONObject

class AddressActivity : AppCompatActivity() {
    var mylist= mutableListOf<CountryModel>()
    var randompos=0
    companion object{
        var REQUEST_COUNTRIES=1
        var REQUEST_ADRESSES=2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        init()
    }
    private fun init(){
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon!!.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        btnDone.text="Finalize"
        tvTitle.text = "Address"

        street.tiitlechoser.text="Street Address"


        DataLoader.getRequest(DataLoader.METHOD,object :CustomCallback{
            override fun onResponse(value: String?) {
                var json= JSONObject(value)
                var jsonarray=  json.getJSONArray("results")
                mylist=  Gson().fromJson(jsonarray.toString(),Array<CountryModel>::class.java).toMutableList()
                randompos=(0 until mylist.size).random()
                street.setOnClickListener(){
                    getadress(randompos)
                }

                var model=mylist[randompos]
                country.chooser.text=model.name
                country.setOnClickListener(){
                    openCountriesActivity(randompos)
                }
            }
            override fun onFailure(value: String?) {

            }
        },this)

        nextbtn.setOnClickListener(){
            dialog()
        }
    }
    private fun dialog(){
        val dialog= Dialog(this)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_window)

        val params: ViewGroup.LayoutParams=dialog.window!!.attributes
        params.width= ViewGroup.LayoutParams.MATCH_PARENT
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes=params as WindowManager.LayoutParams
        dialog.dialogbtn.setOnClickListener(){
            dialog.dismiss()
        }
        dialog.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home){
            super.onBackPressed()
        }
        return true
    }

    private fun openCountriesActivity( position:Int){
        var intent= Intent(this,ChooseCountryActivity::class.java)
        intent.putParcelableArrayListExtra("countries",mylist as java.util.ArrayList<out Parcelable>)
        intent.putExtra("selected",position)
        startActivityForResult(intent,REQUEST_COUNTRIES)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }

    private fun getadress(position:Int){
        val intet= Intent(this,
            ChooseAddressActivity::class.java)
        intet.putExtra("countryid",mylist[randompos].iso2)
        startActivityForResult(intet, REQUEST_ADRESSES)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==REQUEST_COUNTRIES){
                val selectedpos= data?.extras?.getInt("position",0)
                val model=mylist[selectedpos!!]
                country.chooser.text=model.name
                randompos=selectedpos
            }
            else if(requestCode== REQUEST_ADRESSES){
                var adress= data?.extras?.getString("adress","")
                val hashMap =
                    data?.getSerializableExtra("details") as HashMap<String, String>
                street.chooser.text=adress
                city.setText(hashMap["city"])
                state.setText(hashMap["county"])
                zipcode.setText(hashMap["zipcode"])

            }
        }
    }
}