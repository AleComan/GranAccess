package com.example.granaccess.tareas

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

data class asignadosParcelable(val userID: String, val completed: Boolean) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readBoolean() ?: false
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userID)
        parcel.writeBoolean(completed)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<asignadosParcelable> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): asignadosParcelable {
            return asignadosParcelable(parcel)
        }

        override fun newArray(size: Int): Array<asignadosParcelable?> {
            return arrayOfNulls(size)
        }
    }
}
