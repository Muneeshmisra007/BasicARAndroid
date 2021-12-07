package com.mcdar.a3dassests

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


class ARTexture(var metallicity: Float,  var roughness: Float, var texture: Int,
                var interpolar: Float = 0.0f,
                var size: Float = 0.3f
                ) :  Serializable{

 }