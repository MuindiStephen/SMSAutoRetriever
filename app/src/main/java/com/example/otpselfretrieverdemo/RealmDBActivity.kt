package com.example.otpselfretrieverdemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.otpselfretrieverdemo.realmDb.PetRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers

class RealmDBActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_speech_recognition)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Realm.init(this)

        val config = RealmConfiguration.Builder()
            .name("test.db")
            .schemaVersion(1)
            .build()
        Realm.setDefaultConfiguration(config)

//        Realm.init(this)
//// Creating our db with custom properties
//        val config = RealmConfiguration.Builder()
//            .name("test.db")
//            .schemaVersion(1)
//            .build()
//        Realm.setDefaultConfiguration(config)
//    }
    }

    suspend fun insertToRealm() {

        val allPets = mutableListOf<PetRealm>()

        val config = RealmConfiguration.Builder()
            .name("test.db")
            .schemaVersion(1)
            .build()
        Realm.setDefaultConfiguration(config)

        val realm = Realm.getInstance(config)

        //insert
        realm.executeTransactionAwait(Dispatchers.IO) { realmTrans->
            val petRealm = PetRealm(name = "Bingo", age = 2, petType = "Dog", image = R.drawable.ic_launcher_background)
            realmTrans.insert(petRealm)
        }

        // fetch all
        realm.executeTransactionAwait (Dispatchers.IO) { realmTransaction->
            allPets.addAll( realmTransaction
                .where(PetRealm::class.java)
                .findAll()
            )
        }
    }

}