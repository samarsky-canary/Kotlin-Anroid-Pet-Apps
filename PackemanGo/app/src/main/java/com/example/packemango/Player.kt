package com.example.packemango

import android.graphics.Bitmap

class Player {
    var power : Int = 0;
    var icon : Bitmap;

    constructor(playerIcon: Bitmap) {
        this.icon = playerIcon;
    }
}