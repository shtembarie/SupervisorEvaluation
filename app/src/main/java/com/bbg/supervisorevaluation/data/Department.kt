package com.bbg.supervisorevaluation.data

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject


data class Department(
    val dId: Int,
    val dName: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(dId)
        parcel.writeString(dName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Department> {
        override fun createFromParcel(parcel: Parcel): Department {
            return Department(parcel)
        }

        override fun newArray(size: Int): Array<Department?> {
            return arrayOfNulls(size)
        }
    }

    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("dId", dId)
        jsonObject.put("dName", dName)
        return jsonObject
    }
}