package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.topic.SendTopicMessageFragment

class TopicMessageViewModel : ViewModel() {
    private val mViewState = TopicMessageViewState().apply {
        value?.text = "This is ${SendTopicMessageFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}