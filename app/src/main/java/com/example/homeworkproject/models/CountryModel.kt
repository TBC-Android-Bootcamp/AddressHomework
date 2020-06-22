package com.example.homeworkproject.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CountryModel(var name:String,var iso2:String): Parcelable {
}