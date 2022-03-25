package com.multimedia.writeyourthink.di

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.multimedia.writeyourthink.repositories.DiaryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun getRepository(firebaseRef: DatabaseReference) = DiaryRepository(firebaseRef)

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
}