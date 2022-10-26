package com.multimedia.writeyourthink.di

import android.content.Context
import com.facebook.CallbackManager
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.multimedia.writeyourthink.repositories.DiaryRepository
import com.multimedia.writeyourthink.repositories.DiaryRepositoryImpl
import com.multimedia.writeyourthink.services.GpsTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getFirebaseInstance() = FirebaseDatabase.getInstance()

    @Singleton
    @Provides
    fun getFirebaseAuthInstance() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun getUserInfomationFromFirebaseAuth(auth: FirebaseAuth) = auth.currentUser!!

    @Singleton
    @Provides
    fun getFirebaseItemRef(db: FirebaseDatabase, user: FirebaseUser) = db.getReference(user.uid)

    @Singleton
    @Provides
    fun getRepository(firebaseRef: DatabaseReference): DiaryRepository = DiaryRepositoryImpl(firebaseRef)

    @Singleton
    @Provides
    fun getAppCheckInstance() = FirebaseAppCheck.getInstance()

    @Singleton
    @Provides
    fun getSafetyNetAppCheckProviderFactoryInstance() =
        SafetyNetAppCheckProviderFactory.getInstance()

    @Singleton
    @Provides
    fun getFirebaseStorageInstance() = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun getGPSTrackerInstance(@ApplicationContext context: Context) = GpsTracker(context)

    @Singleton
    @Provides
    fun getCalendarInstance() = Calendar.getInstance()

    @Singleton
    @Provides
    fun getCallbackManagerInstance() = CallbackManager.Factory.create()
}