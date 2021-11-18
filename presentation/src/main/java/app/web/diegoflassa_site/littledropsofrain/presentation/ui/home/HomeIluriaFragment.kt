package app.web.diegoflassa_site.littledropsofrain.presentation.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.viewModels
import app.web.diegoflassa_site.littledropsofrain.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.NonDisposableHandle.parent

@AndroidEntryPoint
class HomeIluriaFragment : Fragment() {

    companion object {
        fun newInstance() = HomeIluriaFragment()
    }

    private var viewModel: HomeIluriaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply{
            setContent{
                BuildUi()
            }
        }
    }

    @Composable
    @Preview
    private fun BuildUi() {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text("Little Drops Of Rain") },
                )
            },
            content = { buildContent() }
        )
    }

    @Composable
    private fun buildContent() {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = colorResource(R.color.colorAccent))
        ) {
            val (columnTop, boxMiddle, mainBox) = createRefs()
            val shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(231.dp)
                    .clip(shape)
                    .background(color = colorResource(R.color.background_white))
                    .constrainAs(columnTop) {
                        top.linkTo(parent.top)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val textState = remember { mutableStateOf(TextFieldValue()) }
                val shapeSearch = RoundedCornerShape(14.dp)
                val paddingValues = PaddingValues(0.dp, 53.dp, 0.dp, 0.dp)
                TextField(
                    value = textState.value,
                    placeholder = { Text("Procurar") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "",
                            tint = colorResource(R.color.hintTextColor),
                        )
                    },
                    textStyle = TextStyle(color = colorResource(R.color.hintTextColor)),
                    onValueChange = { textState.value = it },
                    modifier = Modifier
                        .padding(paddingValues)
                        .height(60.dp)
                        .width(300.dp)
                        .background(color = colorResource(R.color.white))
                        .clip(shapeSearch),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = colorResource(R.color.background_white)
                    )
                )
            }
            val shapeBox = RoundedCornerShape(0.dp, 0.dp, 19.dp, 19.dp)
            Box(
                modifier = Modifier
                    .width(303.dp)
                    .height(145.dp)
                    .clip(shapeBox)
                    .background(color = colorResource(R.color.background_white))
                    .constrainAs(boxMiddle) {
                        top.linkTo(parent.top, 134.dp)
                        start.linkTo(parent.start, 36.dp)
                        end.linkTo(parent.end, 36.dp)
                    },
            ) {
                getCarouselContent()
            }
            val mainBoxShape = RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(mainBoxShape)
                    .background(color = colorResource(R.color.background_white))
                    .constrainAs(mainBox) {
                        top.linkTo(boxMiddle.bottom, 134.dp)
                    },
            ) {
                getCarouselContent()
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun getCarouselContent() {
        val pagerState = rememberPagerState()
        HorizontalPager(
            count = viewModel.getCarouselItems().size,
            state = pagerState,
            modifier = Modifier
                .width(281.dp)
                .height(115.dp)
                .background(color = colorResource(R.color.background_white))
        ) { page ->
            getCarouselItemContent(page)
        }
    }

    @Composable
    private fun getCarouselItemContent(page: Int) {
        val item = viewModel.getCarouselItems()[page]
        Text("Carousel Item $page")
    }

}