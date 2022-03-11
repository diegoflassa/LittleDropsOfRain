/*
 * Copyright 2021 The Little Drops of Rain Project
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.homeIluria

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.entities.CategoryItem
import app.web.diegoflassa_site.littledropsofrain.presentation.providers.HomeIluriaStateProvider
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.theme.LittleDropsOfRainTheme
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@AndroidEntryPoint
class HomeIluriaFragment : Fragment() {

    private val myViewModel: HomeIluriaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                myViewModel.refresh()
                val uiState = myViewModel.uiState
                BuildUi(uiState)
            }
        }
    }

    @Composable
    fun BuildUi(liveData: LiveData<HomeIluriaState>) {
        HomeIluriaContent(liveData.observeAsState())
    }

    @Preview
    @Composable
    fun BuildUiPreview(@PreviewParameter(HomeIluriaStateProvider::class) liveData: LiveData<HomeIluriaState>?) {
        if (liveData == null) {
            Log.i(tag, "LiveData object is null. Getting data from HomeIluriaState.getDummyData()")
        }
        val uiState = liveData?.observeAsState() ?: HomeIluriaState.getDummyData().observeAsState()
        HomeIluriaContent(uiState)
    }

    @Composable
    private fun HomeIluriaContent(uiState: State<HomeIluriaState?>) {
        LittleDropsOfRainTheme {
            BuildContent(uiState)
            /*
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
                content = { buildContent(uiState) }
            )
             */
        }
    }

    @Composable
    private fun BuildContent(uiState: State<HomeIluriaState?>) {
        val isRefreshing by uiState.value!!.isRefreshing.observeAsState()
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing!!),
            onRefresh = { myViewModel.refresh() },
        ) {
            val constrainLayoutScrollState = rememberScrollState()
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = colorResource(R.color.colorAccent))
                    .verticalScroll(constrainLayoutScrollState)
            ) {
                val (columnTop, boxMiddle, bkImage, carouselCategories, mainBox) = createRefs()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(231.dp)
                        .clip(RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
                        .background(color = colorResource(R.color.backgroundWhite))
                        .constrainAs(columnTop) {
                            top.linkTo(parent.top)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val textState = remember { mutableStateOf(TextFieldValue()) }
                    TextField(
                        value = textState.value,
                        placeholder = { Text(stringResource(R.string.search)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = stringResource(R.string.content_description_icon_search),
                                tint = colorResource(R.color.hintTextColor),
                            )
                        },
                        onValueChange = { textState.value = it },
                        modifier = Modifier
                            .padding(PaddingValues(0.dp, 53.dp, 0.dp, 0.dp))
                            .height(60.dp)
                            .width(300.dp)
                            .background(color = colorResource(R.color.white))
                            .clip(RoundedCornerShape(14.dp)),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = colorResource(android.R.color.white)
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .width(303.dp)
                        .height(145.dp)
                        .clip(RoundedCornerShape(0.dp, 0.dp, 19.dp, 19.dp))
                        .background(color = colorResource(android.R.color.white))
                        .constrainAs(boxMiddle) {
                            top.linkTo(parent.top, 134.dp)
                            start.linkTo(parent.start, 36.dp)
                            end.linkTo(parent.end, 36.dp)
                        },
                ) {
                    GetSpotlightCarouselContent(uiState)
                }
                Image(
                    painter = painterResource(R.drawable.ic_bk_image),
                    contentDescription = null,
                    modifier = Modifier
                        .width(347.dp)
                        .height(293.dp)
                        .zIndex(-1F)
                        .constrainAs(bkImage) {
                            top.linkTo(columnTop.bottom, 11.dp)
                            start.linkTo(parent.start, 14.dp)
                            end.linkTo(parent.end, 14.dp)
                        },
                )
                val scrollState = rememberScrollState()
                GetCategoriesCarouselContent(
                    Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .constrainAs(carouselCategories) {
                            top.linkTo(boxMiddle.bottom, 24.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .horizontalScroll(scrollState),
                    uiState,
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp))
                        .background(color = colorResource(R.color.backgroundWhite))
                        .constrainAs(mainBox) {
                            top.linkTo(boxMiddle.bottom, 134.dp)
                        },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(PaddingValues(0.dp, 14.dp, 0.dp, 0.dp))
                    ) {
                        GetNewCollectionItem(uiState)
                        GetRecommendationsItem(uiState)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun GetNewCollectionItem(uiState: State<HomeIluriaState?>) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.new_collection),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colorResource(R.color.tertiaryTextColor),
                modifier = Modifier.padding(PaddingValues(36.dp, 0.dp, 0.dp, 0.dp))
            )
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(
                        R.color.buttonColor
                    )
                ),
                modifier = Modifier.padding(PaddingValues(0.dp, 0.dp, 36.dp, 0.dp))
            ) {
                Text(
                    text = stringResource(R.string.see_all),
                    color = colorResource(R.color.secondaryTextColor),
                    fontSize = 12.sp
                )
            }
        }
        GetNewCollectionCarouselContent(uiState)
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun GetRecommendationsItem(uiState: State<HomeIluriaState?>) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.recommendations),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.tertiaryTextColor),
                fontSize = 18.sp,
                modifier = Modifier.padding(PaddingValues(36.dp, 0.dp, 0.dp, 0.dp))
            )
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(
                        R.color.buttonColor
                    )
                ),
                modifier = Modifier.padding(PaddingValues(0.dp, 0.dp, 36.dp, 0.dp))
            ) {
                Text(
                    text = stringResource(R.string.see_all),
                    color = colorResource(R.color.secondaryTextColor),
                    fontSize = 12.sp
                )
            }
        }
        GetRecommendationsCarouselContent(uiState)
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun GetCategoriesCarouselContent(modifier: Modifier, uiState: State<HomeIluriaState?>) {
        Row(
            modifier = modifier
        ) {
            for (item in uiState.value!!.carouselItemsCategories)
                GetCategoriesCarouselItemContent(item)
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun GetNewCollectionCarouselContent(uiState: State<HomeIluriaState?>) {
        val pagerState = rememberPagerState()
        HorizontalPager(
            count = uiState.value!!.carouselItemsNewCollection.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(233.dp)
                .background(color = colorResource(R.color.backgroundWhite))
        ) { page ->
            GetMainCarouselItemContent(page, uiState)
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun GetRecommendationsCarouselContent(uiState: State<HomeIluriaState?>) {
        val pagerState = rememberPagerState()
        HorizontalPager(
            count = uiState.value!!.carouselItemsRecommended.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(233.dp)
                .background(color = colorResource(R.color.backgroundWhite))
        ) { page ->
            GetMainCarouselItemContent(page, uiState)
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun GetSpotlightCarouselContent(uiState: State<HomeIluriaState?>) {
        val pagerState = rememberPagerState()
        val scope = rememberCoroutineScope()
        Column {
            HorizontalPager(
                count = uiState.value!!.carouselItemsSpotlight.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(94.dp)
                    .background(color = colorResource(R.color.backgroundWhite))
                    .padding(PaddingValues(12.dp, 15.dp, 12.dp, 0.dp))

            ) { page ->
                GetSpotlightCarouselItemContent(page, uiState)
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(0.dp, 16.dp, 0.dp, 0.dp))
            ) {
                for ((index, _) in uiState.value!!.carouselItemsSpotlight.withIndex()) {
                    var dotColor = colorResource(R.color.chipBackground)
                    if (pagerState.currentPage == index) {
                        dotColor = colorResource(R.color.primaryTextColor)
                    }
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                            .clickable {
                                scope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            }
                    )
                    Spacer(modifier = Modifier.padding(PaddingValues(3.75.dp, 0.dp, 0.dp, 0.dp)))
                }
            }
        }
    }

    @Composable
    private fun GetMainCarouselItemContent(page: Int, uiState: State<HomeIluriaState?>) {
        val item = uiState.value!!.carouselItemsNewCollection[page]
        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(item.getImageUrlAsUri())
                        .apply(block = fun ImageRequest.Builder.() {
                            placeholder(R.drawable.placeholder)
                            size(Size.ORIGINAL)
                            scale(Scale.FILL)
                        }).build()
                ),
                contentDescription = null,
                contentScale = ContentScale.Companion.FillWidth,
                modifier = Modifier
                    .width(147.dp)
                    .height(147.dp)
                    .clip(RoundedCornerShape(10.dp)),
            )
            Text(item.title!!)
            Row(horizontalArrangement = Arrangement.Start) {
                for (category in item.categories) {
                    BuildChip(label = category)
                }
            }
        }
    }

    @Composable
    private fun GetSpotlightCarouselItemContent(page: Int, uiState: State<HomeIluriaState?>) {
        val item = uiState.value!!.carouselItemsSpotlight[page]
        Row {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(item.getImageUrlAsUri())
                        .apply(block = fun ImageRequest.Builder.() {
                            placeholder(R.drawable.placeholder)
                            size(Size.ORIGINAL)
                            scale(Scale.FILL)
                        }).build()
                ),
                contentDescription = null,
                contentScale = ContentScale.Companion.FillWidth,
                modifier = Modifier
                    .width(164.dp)
                    .height(94.dp)
                    .clip(RoundedCornerShape(10.dp)),
            )
            Column(modifier = Modifier.padding(PaddingValues(24.dp, 0.dp, 0.dp, 0.dp))) {
                if (item.categories.isNotEmpty()) {
                    Text(
                        text = item.categories[0],
                        color = colorResource(R.color.secondaryTextColor),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(PaddingValues(0.dp, 8.dp, 0.dp, 0.dp))
                    )
                }
                Text(
                    item.title!!,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(PaddingValues(0.dp, 8.dp, 0.dp, 0.dp))
                )
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(
                            R.color.buttonColor
                        )
                    ),
                    modifier = Modifier.padding(PaddingValues(8.dp, 0.dp, 0.dp, 0.dp))
                ) {
                    Text(
                        text = stringResource(R.string.buy),
                        color = colorResource(R.color.secondaryTextColor),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    @Composable
    private fun GetCategoriesCarouselItemContent(item: CategoryItem) {
        Row {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(item.getImageUrlAsUri())
                            .apply(block = fun ImageRequest.Builder.() {
                                placeholder(R.drawable.placeholder)
                                size(Size.ORIGINAL)
                                scale(Scale.FILL)
                            }).build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .width(55.dp)
                        .height(55.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(3.dp, Color.White),
                )
                Text(
                    item.category,
                    modifier = Modifier.padding(PaddingValues(0.dp, 8.dp, 0.dp, 0.dp))
                )
            }
        }
    }

    @Composable
    fun BuildChip(label: String, icon: ImageVector? = null) {
        Box(modifier = Modifier.padding(8.dp)) {
            Surface(
                elevation = 1.dp,
                shape = MaterialTheme.shapes.small,
                color = colorResource(R.color.chipBackground)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 4.dp)
                    )
                    Text(
                        label,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 9.sp,
                        style = MaterialTheme.typography.button.copy(color = colorResource(R.color.chipTextColor))
                    )
                }
            }
        }
    }
}
