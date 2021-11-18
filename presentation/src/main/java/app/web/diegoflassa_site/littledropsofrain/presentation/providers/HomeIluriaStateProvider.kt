package app.web.diegoflassa_site.littledropsofrain.presentation.providers

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.LiveData
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.homeIluria.HomeIluriaState

class HomeIluriaStateProvider: PreviewParameterProvider<LiveData<HomeIluriaState>> {
    override val values = sequenceOf(HomeIluriaState.getDummyData())
    override val count: Int = values.count()
}