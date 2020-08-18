package io.github.diegoflassa.littledropsofrain.data.entities

import java.util.*
import kotlin.collections.HashMap


data class User(var email : String? = null, var name : String? = null, var uid: String? = null, @field:JvmField var isAdmin : Boolean = false ) {

    companion object{
        private const val UID= "uid"
        private const val EMAIL= "email"
        private const val NAME= "name"
        private const val IS_ADMIN= "isAdmin"
    }

    constructor(map: Map<String, Any>) : this() {
        fromMap(map)
    }

    override fun equals(other: Any?): Boolean {
        // self check
        if (this === other) return true
        // null check
        if (other == null) return false
        // type check and cast
        if (javaClass != other.javaClass) return false
        val user: User = other as User
        // field comparison
        return (Objects.equals(uid, user.uid)
                && Objects.equals(name, user.name)
                && Objects.equals(email, user.email))
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (uid == null) 0 else uid.hashCode()
        result = prime * result + if (name == null) 0 else name.hashCode()
        result = prime * result + if (email == null) 0 else email.hashCode()
        return result
    }

    fun toMap(): Map<String, Any?> {
        val result: HashMap<String, Any?> = HashMap()
        result[UID] = uid
        result[EMAIL] = email
        result[NAME] = name
        result[IS_ADMIN] = isAdmin
        return result
    }

    private fun fromMap(map: Map<String, Any>){
        uid = map[UID] as String?
        email = map[EMAIL] as String?
        name = map[NAME] as String?
        isAdmin = map[IS_ADMIN] as Boolean
    }

    override fun toString():String{
        return "$name - $email"
    }


    fun fromString(text : String){
        val separated = text.split("-")
        name = separated[0].trim()
        email = separated[1].trim()
    }
}