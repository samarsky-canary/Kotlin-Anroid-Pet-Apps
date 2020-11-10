package com.example.packemango

import android.location.Location
import com.google.android.gms.maps.model.LatLng

class Pockemon {
    var name: String = "Default name";
    var description: String = "Default description";
    var power: Int = 100;
    var image: Int = 0
    var position : Location = Location("Pos")
    var isCatched: Boolean = false

    constructor(name:String,description:String,power:Int,image:Int,position:LatLng) {
        this.name = name;
        this.description = description;
        this.power = power;
        this.position.longitude = position.longitude;
        this.position.latitude = position.latitude;
        this.image = image;

    }
}