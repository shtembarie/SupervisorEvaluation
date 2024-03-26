package com.bbg.supervisorevaluation.data

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject
import java.io.Serializable


data class SupervisorModel(
    val id: Int,
    val name: String,
    val department: Department,
    val status: String
) : Parcelable {

    // Constructor for parceling
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readParcelable<Department>(Department::class.java.classLoader)!!,
        parcel.readString() ?: ""
    )

    // Convert SupervisorModel to JSON
    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("name", name)
        jsonObject.put("department", department.toJSON())
        jsonObject.put("status", status)
        return jsonObject
    }

    // Parcelable implementation
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeParcelable(department, flags)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SupervisorModel> {
        override fun createFromParcel(parcel: Parcel): SupervisorModel {
            return SupervisorModel(parcel)
        }

        override fun newArray(size: Int): Array<SupervisorModel?> {
            return arrayOfNulls(size)
        }
    }
}