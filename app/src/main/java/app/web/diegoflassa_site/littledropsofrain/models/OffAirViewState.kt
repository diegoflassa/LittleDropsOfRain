package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData

class OffAirViewState : LiveData<OffAirViewState>() {

    var text: String = ""
    var messageEn: String = ""
    var messagePt: String = ""

}
