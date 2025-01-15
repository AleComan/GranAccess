package com.example.granaccess.tareas

import android.os.Parcel
import android.os.Parcelable

data class PasoTarea(var numero: Int, val descripcion: String, var uri: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(numero)
        parcel.writeString(descripcion)
        parcel.writeString(uri)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PasoTarea> {
        override fun createFromParcel(parcel: Parcel): PasoTarea {
            return PasoTarea(parcel)
        }

        override fun newArray(size: Int): Array<PasoTarea?> {
            return arrayOfNulls(size)
        }
    }
}
