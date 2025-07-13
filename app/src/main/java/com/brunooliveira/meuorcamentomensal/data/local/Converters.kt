package com.brunooliveira.meuorcamentomensal.data.local

import androidx.room.TypeConverter
import com.brunooliveira.meuorcamentomensal.domain.model.PaymentStatus
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)

    @TypeConverter
    fun fromStatus(status: PaymentStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): PaymentStatus = PaymentStatus.valueOf(value)

}