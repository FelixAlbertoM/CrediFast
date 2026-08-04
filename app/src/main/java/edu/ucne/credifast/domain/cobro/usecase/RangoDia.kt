package edu.ucne.credifast.domain.cobro.usecase

import java.util.Calendar

object RangoDia {

    fun inicioDe(fecha: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = fecha
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun finDe(fecha: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = fecha
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun sumarDias(fecha: Long, dias: Int): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = fecha
        cal.add(Calendar.DAY_OF_YEAR, dias)
        return cal.timeInMillis
    }
}