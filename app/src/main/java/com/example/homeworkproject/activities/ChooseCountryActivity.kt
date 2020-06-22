package com.example.homeworkproject.activities

import android.app.Activity
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homeworkproject.R
import com.example.homeworkproject.adapters.CountryAdapter
import com.example.homeworkproject.models.CountryModel
import kotlinx.android.synthetic.main.activity_choose_country.*
import kotlinx.android.synthetic.main.main_toolbar.*

class ChooseCountryActivity : AppCompatActivity() {
    lateinit var adapter: CountryAdapter
    var countries= mutableListOf<CountryModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_country)
        init()
    }
    private fun init(){
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon!!.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        btnDone.text="Done"
        tvTitle.text = "Choose Country"

        countries.addAll(intent.extras?.getParcelableArrayList("countries")!!)
        var selected=intent.extras?.getInt("selected",0)
        adapter= CountryAdapter(countries)
        if (selected != null) {
            adapter.selected=selected
        }

        countriesrecuclerview.isNestedScrollingEnabled = false
        countriesrecuclerview.layoutManager= LinearLayoutManager(this)
        countriesrecuclerview.adapter=adapter


        cancelbtn.setOnClickListener(){
            onBackPressed()
        }

        btnDone.setOnClickListener(){
            done()
            onBackPressed()
        }

    }

    private fun done(){
        val intent=intent
        intent.putExtra("position",adapter.selected)
        setResult(Activity.RESULT_OK,intent)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home){
            onBackPressed()
        }
        return true
    }
}