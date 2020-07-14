package io.github.diegoflassa.littledropsofrain.data.entities

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity

//DFL - Classe de DADOS. Ela armazena os DADOS ARMAZENADOS NA TABELA ( cada registro inserido )
@Entity
class User // but they're required for Room to work.
    (
    @field:ColumnInfo(name = "first_name") var firstName: String, // Getters and setters are ignored for brevity,
    @field:ColumnInfo(name = "last_name") var lastName: String
) {
    //Essa anotação garante que será gerada automaticamente
    @PrimaryKey(autoGenerate = true)
    var uid = 0
}