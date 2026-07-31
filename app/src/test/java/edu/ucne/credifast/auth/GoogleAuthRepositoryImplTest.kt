package edu.ucne.credifast.auth

import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.ucne.credifast.data.auth.GoogleAuthRepositoryImpl
import edu.ucne.credifast.data.local.dao.UsuarioDao
import io.mockk.mockk
import io.mockk.every
import org.junit.Before
import org.junit.Test

class GoogleAuthRepositoryImplTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var usuarioDao: UsuarioDao
    private lateinit var repository: GoogleAuthRepositoryImpl

    @Before
    fun setUp() {
        auth = mockk(relaxed = true)
        usuarioDao = mockk(relaxed = true)
        val context = mockk<android.content.Context>(relaxed = true)
        repository = GoogleAuthRepositoryImpl(context, auth, "web-client-id-de-prueba", usuarioDao)
    }

    @Test
    fun `cuando no hay usuario, isLoggedIn devuelve false`() {
        every { auth.currentUser } returns null

        val resultado = repository.isLoggedIn()

        assertThat(resultado).isFalse()
    }

    @Test
    fun `cuando hay usuario, isLoggedIn devuelve true`() {
        every { auth.currentUser } returns mockk<FirebaseUser>(relaxed = true)

        val resultado = repository.isLoggedIn()

        assertThat(resultado).isTrue()
    }

    @Test
    fun `getCurrentUser mapea correctamente el usuario de Firebase a UsuarioAuth`() {
        val firebaseUser = mockk<FirebaseUser>(relaxed = true)
        every { firebaseUser.uid } returns "abc123"
        every { firebaseUser.displayName } returns "María Peña"
        every { firebaseUser.email } returns "maria.pena@gmail.com"
        every { firebaseUser.photoUrl } returns null
        every { auth.currentUser } returns firebaseUser

        val usuario = repository.getCurrentUser()

        assertThat(usuario).isNotNull()
        assertThat(usuario!!.uid).isEqualTo("abc123")
        assertThat(usuario.nombre).isEqualTo("María Peña")
        assertThat(usuario.correo).isEqualTo("maria.pena@gmail.com")
    }

    @Test
    fun `getCurrentUser devuelve null cuando no hay sesion`() {
        every { auth.currentUser } returns null

        val usuario = repository.getCurrentUser()

        assertThat(usuario).isNull()
    }
}