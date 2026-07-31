package edu.ucne.credifast.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.credifast.R
import edu.ucne.credifast.data.auth.GoogleAuthRepositoryImpl
import edu.ucne.credifast.data.local.CrediFastDatabase
import edu.ucne.credifast.data.local.dao.UsuarioDao
import edu.ucne.credifast.domain.auth.repository.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CrediFastDatabase {
        return Room.databaseBuilder(
            context,
            CrediFastDatabase::class.java,
            "credifast.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUsuarioDao(db: CrediFastDatabase): UsuarioDao = db.usuarioDao()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideWebClientId(@ApplicationContext context: Context): String =
        context.getString(R.string.default_web_client_id)

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        auth: FirebaseAuth,
        webClientId: String,
        usuarioDao: UsuarioDao
    ): AuthRepository = GoogleAuthRepositoryImpl(context, auth, webClientId, usuarioDao)
}