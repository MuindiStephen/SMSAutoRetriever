package com.example.otpselfretrieverdemo.realmDb

import androidx.annotation.DrawableRes
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId

open class PetRealm(
    @PrimaryKey // 2.
    var id: String = ObjectId().toHexString(), // 3.
    @Required // 4.
    var name: String = "",
    @Required
    var petType: String = "",
    var age: Int = 0,
    var isAdopted: Boolean = false,
    @DrawableRes
    var image: Int? = null // 5.
): RealmObject() // 6.