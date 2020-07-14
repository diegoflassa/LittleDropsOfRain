package io.github.diegoflassa.littledropsofrain.data.entities

class Bus {
    private var bus: String? = null
    private var city: String? = null
    var title = ""

    override fun toString(): String {
        return "$bus - $city"
    }

    constructor(bus: String?, city: String?) {
        this.bus = bus
        this.city = city
    }

    constructor()
}