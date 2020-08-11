package io.github.diegoflassa.littledropsofrain.helpers

import java.text.SimpleDateFormat
import java.util.*

class Helper {

    companion object{

        fun getDateTime(date: String): String? {
            return try {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                sdf.parse(date)?.toString()
            } catch (e: Exception) {
                e.toString()
            }
        }

        fun getDateTime(date: Date): String? {
            return try {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                sdf.format(date)
            } catch (e: Exception) {
                e.toString()
            }
        }
    }
}