package edu.ucne.credifast.domain.cliente.usecase

object ClienteValidator {

    fun validarNombre(nombre: String): String? =
        if (nombre.isBlank()) "El nombre es obligatorio" else null

    fun validarCedula(cedula: String): String? = when {
        cedula.isBlank() -> "La cédula es obligatoria"
        !cedula.all { it.isDigit() } -> "La cédula solo puede contener números"
        cedula.length != 11 -> "La cédula debe tener 11 dígitos"
        else -> null
    }

    fun validarTelefono(telefono: String): String? = when {
        telefono.isBlank() -> "El teléfono es obligatorio"
        !telefono.all { it.isDigit() } -> "El teléfono solo puede contener números"
        telefono.length != 10 -> "El teléfono debe tener 10 dígitos"
        else -> null
    }

    fun validarDireccion(direccion: String): String? =
        if (direccion.isBlank()) "La dirección es obligatoria" else null

    fun esValido(nombre: String, cedula: String, telefono: String, direccion: String): Boolean =
        validarNombre(nombre) == null &&
                validarCedula(cedula) == null &&
                validarTelefono(telefono) == null &&
                validarDireccion(direccion) == null
}