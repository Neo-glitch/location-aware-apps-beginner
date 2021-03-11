package com.neo.locationawareapps.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.GsonBuilder
import java.io.IOException

@Database(
    entities = [Activity::class, Location::class, ActivityLocationCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class OutdoorRoomDatabase : RoomDatabase() {
    abstract fun outdoorDao(): OutdoorDao

    companion object {
        @Volatile
        private var INSTANCE: OutdoorRoomDatabase? = null

        fun getInstance(context: Context): OutdoorRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OutdoorRoomDatabase::class.java,
                    "outdoor_database.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Thread(Runnable {
                                prepopulateDb(
                                    context,
                                    getInstance(context)
                                )
                            }).start()
                        }
                    })
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private fun prepopulateDb(context: Context, db: OutdoorRoomDatabase) {
            val gson = GsonBuilder().create()
            var data = getJsonDataFromAsset(context, "activities.json")

            db.outdoorDao()
                .insertActivities(gson.fromJson(data, Array<Activity>::class.java).toList())

            data = getJsonDataFromAsset(context, "locations.json")
            db.outdoorDao()
                .insertLocations(gson.fromJson(data, Array<Location>::class.java).toList())

            data = getJsonDataFromAsset(context, "crossref.json")
            db.outdoorDao().insertActivityLocationCrossRefs(
                gson.fromJson(
                    data,
                    Array<ActivityLocationCrossRef>::class.java
                ).toList()
            )
        }

        private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
            val jsonString: String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }
    }
}