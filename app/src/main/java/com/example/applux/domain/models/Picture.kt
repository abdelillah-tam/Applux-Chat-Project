package com.example.applux.domain.models

import com.example.applux.Privacy


data class Picture(var pic: String, var privacy: Privacy){
    constructor() : this("", Privacy.PUBLIC)
}