package app.web.diegoflassa_site.littledropsofrain.presentation.ui.homeIluria

import android.graphics.fonts.FontStyle
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.presentation.providers.HomeIluriaStateProvider
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.theme.MyMaterialTheme
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@AndroidEntryPoint
class HomeIluriaFragment : Fragment() {

    private val myViewModel: HomeIluriaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myViewModel.getCarouselItemsSpotlight()
        myViewModel.getCarouselItemsCategories()
        myViewModel.getCarouselItemsNewCollection()
        myViewModel.getCarouselItemsRecommended()
        return ComposeView(requireContext()).apply {
            setContent {
                val uiState = myViewModel.uiState
                BuildUi(uiState)
            }
        }
    }

    @Preview
    @Composable
    fun BuildUi(@PreviewParameter(HomeIluriaStateProvider::class) liveData: LiveData<HomeIluriaState>) {
        val uiState = liveData.observeAsState()
        CraneHomeContent(uiState)
    }

    @Composable
    private fun CraneHomeContent(uiState: State<HomeIluriaState?>) {
        MyMaterialTheme.ApplyTheme {
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
                content = { buildContent() }
            )
             */
        }
    }

    @Composable
    private fun BuildContent(uiState: State<HomeIluriaState?>) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = colorResource(R.color.colorAccent))
        ) {
            val (columnTop, boxMiddle, bkImage, carouselCategories, mainBox) = createRefs()
            val shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(231.dp)
                    .clip(shape)
                    .background(color = colorResource(R.color.backgroundWhite))
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
                        backgroundColor = colorResource(android.R.color.white)
                    )
                )
            }
            val shapeBox = RoundedCornerShape(0.dp, 0.dp, 19.dp, 19.dp)
            Box(
                modifier = Modifier
                    .width(303.dp)
                    .height(145.dp)
                    .clip(shapeBox)
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
            GetCategoriesCarouselContent(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .constrainAs(carouselCategories) {
                        top.linkTo(boxMiddle.bottom, 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                uiState,
            )
            val mainBoxShape = RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(mainBoxShape)
                    .background(color = colorResource(R.color.backgroundWhite))
                    .constrainAs(mainBox) {
                        top.linkTo(boxMiddle.bottom, 134.dp)
                    },
            ) {
                val paddingValuesLazyColumn = PaddingValues(0.dp, 14.dp, 0.dp, 0.dp)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(paddingValuesLazyColumn)
                ) {
                    item {
                        GetNewCollectionItem(uiState)
                    }
                    item {
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
            val paddingText = PaddingValues(36.dp, 0.dp, 0.dp, 0.dp)
            val paddingButton = PaddingValues(0.dp, 0.dp, 36.dp, 0.dp)
            Text(
                text = "New Collection",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colorResource(R.color.tertiaryTextColor),
                modifier = Modifier.padding(paddingText)
            )
            Button(
                onClick = {}, colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(
                        R.color.buttonColor
                    )
                ),
                modifier = Modifier.padding(paddingButton)
            ) {
                Text(
                    text = "See All",
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
            val paddingText = PaddingValues(36.dp, 0.dp, 0.dp, 0.dp)
            val paddingButton = PaddingValues(0.dp, 0.dp, 36.dp, 0.dp)
            Text(
                text = "Recommendations",
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.tertiaryTextColor),
                fontSize = 18.sp,
                modifier = Modifier.padding(paddingText)
            )
            Button(
                onClick = {}, colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(
                        R.color.buttonColor
                    )
                ),
                modifier = Modifier.padding(paddingButton)
            ) {
                Text(
                    text = "See All",
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
        val pagerState = rememberPagerState()
        HorizontalPager(
            count = uiState.value!!.carouselItemsCategories.size,
            state = pagerState,
            modifier = modifier
        ) { page ->
            GetCategoriesCarouselItemContent(page, uiState)
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
        HorizontalPager(
            count = uiState.value!!.carouselItemsSpotlight.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = colorResource(R.color.backgroundWhite))
        ) { page ->
            GetSpotlightCarouselItemContent(page, uiState)
        }
    }

    @Composable
    private fun GetMainCarouselItemContent(page: Int, uiState: State<HomeIluriaState?>) {
        val item = uiState.value!!.carouselItemsNewCollection[page]
        val shape = RoundedCornerShape(10.dp)
        Column {
            Image(
                painter = rememberImagePainter(
                    item.getImageUrlAsUri(),
                    builder = {
                        placeholder(R.drawable.placeholder)
                    }),
                contentDescription = null,
                modifier = Modifier
                    .width(147.dp)
                    .height(147.dp)
                    .clip(shape),
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
        val shape = RoundedCornerShape(10.dp)
        Row {
            Image(
                painter = rememberImagePainter(
                    item.getImageUrlAsUri(),
                    builder = {
                        placeholder(R.drawable.placeholder)
                    }),
                contentDescription = null,
                modifier = Modifier
                    .width(164.dp)
                    .height(94.dp)
                    .clip(shape),
            )
            Column {
                Text(item.title!!)
                if (item.categories.isNotEmpty()) {
                    Text(
                        text = item.categories[0],
                        color = colorResource(R.color.secondaryTextColor),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(
                            R.color.buttonColor
                        )
                    ),
                    //modifier = Modifier.padding(paddingButton)
                ) {
                    Text(
                        text = "Buy",
                        color = colorResource(R.color.secondaryTextColor),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    @Composable
    private fun GetCategoriesCarouselItemContent(page: Int, uiState: State<HomeIluriaState?>) {
        val item = uiState.value!!.carouselItemsCategories[page]
        val paddingText = PaddingValues(0.dp, 8.dp, 0.dp, 0.dp)
        val shape = RoundedCornerShape(12.dp)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = rememberImagePainter(
                    item.image,
                    builder = {
                        placeholder(R.drawable.placeholder)
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .width(55.dp)
                    .height(55.dp)
                    .clip(shape)
                    .border(3.dp, Color.White),
            )
            Text(item.category, modifier = Modifier.padding(paddingText))
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