package edu.ucne.credifast.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.credifast.data.local.dao.UsuarioDao
import edu.ucne.credifast.data.local.entities.UsuarioEntity
import edu.ucne.credifast.domain.auth.model.UsuarioAuth
import edu.ucne.credifast.domain.auth.repository.AuthRepository
import edu.ucne.credifast.domain.common.Resource
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val webClientId: String,
    private val usuarioDao: UsuarioDao
) : AuthRepository {

    override suspend fun signInWithGoogle(): Resource<UsuarioAuth> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId).build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential).await()

                val user = getCurrentUser()
                    ?: return Resource.Error("No se pudo obtener el usuario tras iniciar sesión")

                usuarioDao.upsert(
                    UsuarioEntity(
                        uid = user.uid,
                        nombre = user.nombre,
                        correo = user.correo,
                        fotoUrl = user.fotoUrl
                    )
                )

                Resource.Success(user)
            } else {
                Resource.Error("Credencial inesperada")
            }
        } catch (e: GetCredentialException) {
            Resource.Error(e.message ?: "No se pudo iniciar sesión con Google")
        } catch (e: GoogleIdTokenParsingException) {
            Resource.Error(e.message ?: "Error al procesar el token de Google")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): UsuarioAuth? {
        val u = auth.currentUser ?: return null
        return UsuarioAuth(
            uid = u.uid,
            nombre = u.displayName ?: "Usuario",
            correo = u.email ?: "",
            fotoUrl = u.photoUrl?.toString()
        )
    }

    override fun isLoggedIn(): Boolean = auth.currentUser != null

    override fun observeAuthState(): kotlinx.coroutines.flow.Flow<Boolean> =
        kotlinx.coroutines.flow.callbackFlow {
            val listener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { firebaseAuth ->
                val logueado = firebaseAuth.currentUser != null
                android.util.Log.d("CERRAR_SESION", "3 - AuthState cambió: logueado=$logueado")
                trySend(logueado)
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }
}