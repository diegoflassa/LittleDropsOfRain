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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.admin.categories

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import androidx.navigation.fragment.navArgs
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.CategoriesDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.FilesDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.CategoryItem
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnFileUploadedFailureListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnFileUploadedListener
import app.web.diegoflassa_site.littledropsofrain.data.repository.CategoriesRepository
import app.web.diegoflassa_site.littledropsofrain.data.repository.FilesRepository
import app.web.diegoflassa_site.littledropsofrain.presentation.contracts.CropImageResultContract
import app.web.diegoflassa_site.littledropsofrain.presentation.providers.CategoriesEditorStateProvider
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.theme.LittleDropsOfRainTheme
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

class CategoriesEditorFragment(private val category: CategoryItem?) :
    Fragment(),
    OnFileUploadedListener,
    OnFileUploadedFailureListener {

    constructor() : this(null)

    private val myViewModel: CategoriesEditorViewModel by viewModels()
    private lateinit var getContentLauncher: ActivityResultLauncher<String>
    private lateinit var cropImageLauncher: ActivityResultLauncher<Pair<Uri, Pair<Float, Float>>>
    private val args: CategoriesEditorFragmentArgs by navArgs()

    private var filesRepository: FilesRepository = FilesRepository(FilesDao)
    private var categoriesRepository: CategoriesRepository = CategoriesRepository(CategoriesDao)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (category != null) {
            myViewModel.uiState.value?.category = category
        } else if (args.category != null) {
            myViewModel.uiState.value?.category = args.category!!
        }
        return ComposeView(requireContext()).apply {
            setContent {
                myViewModel.refresh()
                val uiState = myViewModel.uiState
                BuildUi(uiState)
            }
        }
    }

    @Composable
    fun BuildUi(liveData: LiveData<CategoriesEditorState>) {
        CategoriesEditorContent(liveData.observeAsState())
    }

    @Preview
    @Composable
    fun BuildUiPreview(@PreviewParameter(CategoriesEditorStateProvider::class) liveData: LiveData<CategoriesEditorState>?) {
        if (liveData == null) {
            Log.i(
                tag,
                "LiveData object is null. Getting data from CategoriesEditorState.getDummyData()"
            )
        }
        val uiState =
            liveData?.observeAsState() ?: CategoriesEditorState.getDummyData().observeAsState()
        CategoriesEditorContent(uiState)
    }

    @Composable
    private fun CategoriesEditorContent(uiState: State<CategoriesEditorState?>) {
        var showLoadingScreen by remember { mutableStateOf(false) }
        val painter = painterResource(R.drawable.placeholder)
        var imagePainter by remember { mutableStateOf(painter) }
        cropImageLauncher = rememberLauncherForActivityResult(CropImageResultContract()) {
            if (it != null) {
                filesRepository.insert(it, this, this)
                showLoadingScreen = false
                uiState.value?.category?.imageUrl = it.toString()
            } else {
                uiState.value?.category?.imageUrl = null
            }
        }
        getContentLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                if (it == null) {
                    uiState.value?.category?.imageUrl = null
                    showLoadingScreen = false
                } else {
                    showLoadingScreen = true
                    val data = Pair(it, CropImageResultContract.ASPECT_RATIO_BOX)
                    cropImageLauncher.launch(data)
                }
            }

        LittleDropsOfRainTheme {
            val constrainLayoutScrollState = rememberScrollState()
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = colorResource(R.color.colorAccent))
                    .verticalScroll(constrainLayoutScrollState)
            ) {
                val (image, loading, buttonOpen, buttonCrop, textCategory, buttonSave, buttonDelete) = createRefs()
                imagePainter = if (uiState.value?.category?.getImageUrlAsUri() != null) {
                    rememberAsyncImagePainter(uiState.value?.category?.getImageUrlAsUri())
                } else {
                    painterResource(R.drawable.placeholder)
                }
                Image(
                    painter = imagePainter,
                    contentDescription = null,
                    modifier = Modifier
                        .width(256.dp)
                        .height(256.dp)
                        .zIndex(-1F)
                        .constrainAs(image) {
                            top.linkTo(parent.top, 64.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                )
                if (showLoadingScreen) {
                    CircularProgressIndicator(
                        modifier = Modifier.constrainAs(loading) {
                            top.linkTo(image.top)
                            bottom.linkTo(image.bottom)
                            start.linkTo(image.start)
                            end.linkTo(image.end)
                        },
                    )
                }
                Button(
                    onClick = { getContentLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(
                            R.color.buttonColor
                        )
                    ),
                    modifier = Modifier
                        .padding(PaddingValues(8.dp, 0.dp, 0.dp, 0.dp))
                        .constrainAs(buttonOpen) {
                            top.linkTo(image.bottom, 16.dp)
                            start.linkTo(parent.start)
                            if (imagePainter is AsyncImagePainter) {
                                end.linkTo(buttonCrop.start)
                            } else {
                                end.linkTo(parent.end)
                            }
                        },
                ) {
                    Text(
                        text = stringResource(R.string.open),
                        color = colorResource(R.color.secondaryTextColor),
                        fontSize = 12.sp
                    )
                }
                if (imagePainter is AsyncImagePainter) {
                    Button(
                        onClick = {
                            if (uiState.value?.category?.getImageUrlAsUri() != null) {
                                val data = Pair(
                                    uiState.value?.category?.getImageUrlAsUri()!!,
                                    CropImageResultContract.ASPECT_RATIO_BOX
                                )
                                cropImageLauncher.launch(data)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(
                                R.color.buttonColor
                            )
                        ),
                        modifier = Modifier
                            .padding(PaddingValues(8.dp, 0.dp, 0.dp, 0.dp))
                            .constrainAs(buttonCrop) {
                                top.linkTo(image.bottom, 16.dp)
                                start.linkTo(buttonOpen.end, 16.dp)
                                end.linkTo(parent.end)
                            },
                    ) {
                        Text(
                            text = stringResource(R.string.crop),
                            color = colorResource(R.color.secondaryTextColor),
                            fontSize = 12.sp
                        )
                    }
                }

                val textState =
                    remember { mutableStateOf(TextFieldValue(uiState.value?.category?.category!!)) }
                TextField(
                    value = textState.value,
                    placeholder = { Text(stringResource(R.string.category)) },
                    onValueChange = { textState.value = it },
                    modifier = Modifier
                        .padding(PaddingValues(0.dp, 16.dp, 0.dp, 0.dp))
                        .background(color = colorResource(R.color.white))
                        .clip(RoundedCornerShape(14.dp))
                        .constrainAs(textCategory) {
                            top.linkTo(buttonOpen.bottom, 16.dp)
                            start.linkTo(image.start)
                            end.linkTo(image.end)
                        },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = colorResource(android.R.color.white)
                    )
                )

                Button(
                    onClick = {
                        val category = uiState.value?.category
                        category?.category = textState.value.text
                        if (category?.uid == null) {
                            categoriesRepository.insert(category!!)
                        } else {
                            categoriesRepository.update(category)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(
                            R.color.buttonColor
                        )
                    ),
                    modifier = Modifier
                        .padding(PaddingValues(8.dp, 0.dp, 0.dp, 60.dp))
                        .constrainAs(buttonSave) {
                            bottom.linkTo(parent.bottom, 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(buttonDelete.start)
                        },
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        color = colorResource(R.color.secondaryTextColor),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = {
                        val category = uiState.value?.category
                        if (category?.uid == null) {
                            categoriesRepository.insert(category!!)
                        } else {
                            categoriesRepository.update(category)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(
                            R.color.buttonColor
                        )
                    ),
                    modifier = Modifier
                        .padding(PaddingValues(8.dp, 0.dp, 0.dp, 60.dp))
                        .constrainAs(buttonDelete) {
                            bottom.linkTo(parent.bottom, 16.dp)
                            start.linkTo(buttonSave.end, 16.dp)
                            end.linkTo(parent.end)
                        },
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = colorResource(R.color.secondaryTextColor),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    override fun onFileUploadedFailure(file: Uri, exception: Exception?) {
        Toast.makeText(context, getString(R.string.file_upload_failure), Toast.LENGTH_LONG).show()
    }

    override fun onFileUploaded(local: Uri, remote: Uri) {
        if (myViewModel.uiState.value?.category?.getImageUrlAsUri() != null) {
            filesRepository.remove(myViewModel.uiState.value?.category?.getImageUrlAsUri()!!)
        }
        myViewModel.uiState.value?.category?.imageUrl = remote.toString()
        Toast.makeText(context, getString(R.string.file_upload_success), Toast.LENGTH_LONG).show()
    }
}
