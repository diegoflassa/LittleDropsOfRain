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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.composables.OnEnterWith
import coil.annotation.ExperimentalCoilApi

@ExperimentalCoilApi
@ExperimentalComposeUiApi
class RegistrationFragment : Fragment() {

    companion object {
        fun newInstance() = RegistrationFragment()
    }

    private lateinit var viewModel: RegistrationViewModel
    // var binding: FragmentRegistrationBinding by viewLifecycle()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]
        // binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        // return binding.root
        return ComposeView(requireContext()).apply {
            setContent {
                AndroidPreview_Registration()
            }
        }
    }

    @Composable
    @Preview
    fun AndroidPreview_Registration() {
        Box(Modifier.size(360.dp, 640.dp)) {
            Registration()
        }
    }

    @Composable
    fun Registration() {
        ConstraintLayout(
            modifier = Modifier
                .background(Color(0.51f, 0.66f, 0.64f, 1.0f))
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .fillMaxSize()
        ) {
            val (boxRefHeader, boxRefPhone, boxRefPassword, boxRefEmail, boxRefName, boxRefOrEnterWith) = createRefs()


            Box {
                ConstraintLayout(modifier = Modifier) {
                    val (_) = createRefs()


                    /* raw vector Vector should have an export setting */
                }
                Box {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(14.0.dp))
                            .size(303.0.dp, 60.0.dp)
                            .background(Color(0.93f, 0.48f, 0.75f, 1.0f))
                    ) {}

                    Text(
                        resourceString(R.string.register),
                        Modifier.wrapContentHeight(Alignment.Top),
                        style = LocalTextStyle.current.copy(
                            color = Color(1.0f, 1.0f, 1.0f, 1.0f),
                            textAlign = TextAlign.Left,
                            fontSize = 16.0.sp
                        )
                    )

                }
                Text(
                    resourceString(R.string.already_have_an_account),
                    Modifier.wrapContentHeight(Alignment.Top),
                    style = LocalTextStyle.current.copy(
                        color = Color.Black,
                        textAlign = TextAlign.Left,
                        fontSize = 14.0.sp
                    )
                )

                Box {
                    Text(
                        resourceString(R.string.or_enter_with),
                        Modifier.wrapContentHeight(Alignment.Top),
                        style = LocalTextStyle.current.copy(
                            color = Color(0.0f, 0.0f, 0.0f, 1.0f),
                            textAlign = TextAlign.Left,
                            fontSize = 14.0.sp
                        )
                    )

                    Box(
                        Modifier
                            .size(90.0.dp, 1.0.dp)
                            .background(Color(0.85f, 0.85f, 0.85f, 1.0f))
                    ) {}

                    Box(
                        Modifier
                            .size(90.0.dp, 1.0.dp)
                            .background(Color(0.85f, 0.85f, 0.85f, 1.0f))
                    ) {}

                }
                Box {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(14.0.dp))
                            .size(90.0.dp, 60.0.dp)
                            .background(Color(0.33f, 0.32f, 0.32f, 1.0f))
                    ) {}

                    /* raw vector Vector should have an export setting */
                }
                Box {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(14.0.dp))
                            .size(90.0.dp, 60.0.dp)
                            .background(Color(0.33f, 0.32f, 0.32f, 1.0f))
                    ) {}

                    /* raw vector Vector should have an export setting */
                }
                Box {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(14.0.dp))
                            .size(90.0.dp, 60.0.dp)
                            .background(Color(0.32f, 0.32f, 0.32f, 1.0f))
                    ) {}

                    /* raw vector Vector should have an export setting */
                }
                Box(Modifier.size(150.0.dp, 202.0.dp)) {}

                Text(
                    resourceString(R.string.register),
                    Modifier.wrapContentHeight(Alignment.Top),
                    style = LocalTextStyle.current.copy(
                        color = Color(0.12f, 0.12f, 0.12f, 1.0f),
                        textAlign = TextAlign.Left,
                        fontSize = 26.0.sp
                    )
                )

                /* raw vector icon should have an export setting */
                Text(
                    resourceString(R.string.lets_start),
                    Modifier.wrapContentHeight(Alignment.Top),
                    style = LocalTextStyle.current.copy(
                        color = Color(1.0f, 1.0f, 1.0f, 1.0f),
                        textAlign = TextAlign.Left,
                        fontSize = 18.0.sp
                    )
                )

                Text(
                    resourceString(R.string.create_new_account),
                    Modifier.wrapContentHeight(Alignment.Top),
                    style = LocalTextStyle.current.copy(
                        color = Color(0.0f, 0.0f, 0.0f, 1.0f),
                        textAlign = TextAlign.Left,
                        fontSize = 12.0.sp
                    )
                )

            }
            Box(modifier=Modifier.constrainAs(boxRefPhone) {
                top.linkTo(boxRefHeader.bottom, margin = 16.dp)
            }) {
                Text(
                    resourceString(R.string.phone),
                    Modifier.wrapContentHeight(Alignment.Top),
                    style = LocalTextStyle.current.copy(
                        color = Color(0.12f, 0.12f, 0.12f, 1.0f),
                        textAlign = TextAlign.Left,
                        fontSize = 14.0.sp
                    )
                )

                Box {
                    Box(
                        Modifier
                            .shadow(
                                15.0.dp,
                                shape = RoundedCornerShape(14.0.dp)
                            )
                            .clip(RoundedCornerShape(14.0.dp))
                            .size(303.0.dp, 60.0.dp)
                            .background(Color(0.96f, 0.96f, 0.96f, 1.0f))
                    ) {}

                    Text(
                        "(**)*********",
                        Modifier.wrapContentHeight(Alignment.Top),
                        style = LocalTextStyle.current.copy(
                            color = Color(
                                0.85f,
                                0.85f,
                                0.85f,
                                1.0f
                            ), textAlign = TextAlign.Left, fontSize = 14.0.sp
                        )
                    )

                }
            }
            Box(modifier=Modifier.constrainAs(boxRefPassword) {
                top.linkTo(boxRefPhone.bottom, margin = 16.dp)
            }) {
                Text(
                    resourceString(R.string.password),
                    Modifier.wrapContentHeight(Alignment.Top),
                    style = LocalTextStyle.current.copy(
                        color = Color(0.12f, 0.12f, 0.12f, 1.0f),
                        textAlign = TextAlign.Left,
                        fontSize = 14.0.sp
                    )
                )

                Box {
                    Box(
                        Modifier
                            .shadow(
                                15.0.dp,
                                shape = RoundedCornerShape(14.0.dp)
                            )
                            .clip(RoundedCornerShape(14.0.dp))
                            .size(303.0.dp, 60.0.dp)
                            .background(Color(0.96f, 0.96f, 0.96f, 1.0f))
                    ) {}

                    Text(
                        "**********",
                        Modifier.wrapContentHeight(Alignment.Top),
                        style = LocalTextStyle.current.copy(
                            color = Color(
                                0.85f,
                                0.85f,
                                0.85f,
                                1.0f
                            ), textAlign = TextAlign.Left, fontSize = 14.0.sp
                        )
                    )


                }
            }
            Box(modifier=Modifier.constrainAs(boxRefEmail) {
                top.linkTo(boxRefPassword.bottom, margin = 16.dp)
            })  {
                Text(
                    resourceString(R.string.email),
                    Modifier.wrapContentHeight(Alignment.Top),
                    style = LocalTextStyle.current.copy(
                        color = Color(0.12f, 0.12f, 0.12f, 1.0f),
                        textAlign = TextAlign.Left,
                        fontSize = 14.0.sp
                    )
                )

                Box {
                    Box(
                        Modifier
                            .shadow(
                                15.0.dp,
                                shape = RoundedCornerShape(14.0.dp)
                            )
                            .clip(RoundedCornerShape(14.0.dp))
                            .size(303.0.dp, 60.0.dp)
                            .background(Color(0.96f, 0.96f, 0.96f, 1.0f))
                    ) {}

                    Text(
                        "exemplo@gmail.com",
                        Modifier.wrapContentHeight(Alignment.Top),
                        style = LocalTextStyle.current.copy(
                            color = Color(
                                0.85f,
                                0.85f,
                                0.85f,
                                1.0f
                            ), textAlign = TextAlign.Left, fontSize = 14.0.sp
                        )
                    )

                }
            }
            Box(modifier=Modifier.constrainAs(boxRefName) {
                top.linkTo(boxRefEmail.bottom, margin = 16.dp)
            }) {
                Text(
                    resourceString(R.string.name),
                    Modifier.wrapContentHeight(Alignment.Top),
                    style = LocalTextStyle.current.copy(
                        color = Color(0.12f, 0.12f, 0.12f, 1.0f),
                        textAlign = TextAlign.Left,
                        fontSize = 14.0.sp
                    )
                )

                Box {
                    Box(
                        Modifier
                            .shadow(
                                15.0.dp,
                                shape = RoundedCornerShape(14.0.dp)
                            )
                            .clip(RoundedCornerShape(14.0.dp))
                            .size(303.0.dp, 60.0.dp)
                            .background(Color(0.96f, 0.96f, 0.96f, 1.0f))
                    ) {}

                    Text(
                        "Ana Pinheiro",
                        Modifier.wrapContentHeight(Alignment.Top),
                        style = LocalTextStyle.current.copy(
                            color = Color(
                                0.85f,
                                0.85f,
                                0.85f,
                                1.0f
                            ), textAlign = TextAlign.Left, fontSize = 14.0.sp
                        )
                    )

                }
            }
            Box(modifier=Modifier.constrainAs(boxRefOrEnterWith) {
                top.linkTo(boxRefName.bottom, margin = 16.dp)
            }) {
                OnEnterWith().Show()
            }
        }
    }
}
