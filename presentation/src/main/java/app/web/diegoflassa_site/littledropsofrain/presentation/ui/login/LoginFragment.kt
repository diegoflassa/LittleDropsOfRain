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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentLoginBinding
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

@ExperimentalCoilApi
class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    var binding: FragmentLoginBinding by viewLifecycle()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
        /*
        return ComposeView(requireContext()).apply {
            setContent {
                BuildUi()
            }
        }
        */
    }

    @Composable
    @Preview
    private fun BuildUi() {
        Column {
            TextField(value = getString(R.string.email), onValueChange = { })
            TextField(value = getString(R.string.password), onValueChange = { })
            Row {
                Text(getString(R.string.keep_me_connected))
                Checkbox(checked = true, onCheckedChange = null)
            }
            Button(onClick = { /* To execute when button is clicked */ }) {
                Text(getString(R.string.signin))
            }
            Text(getString(R.string.does_not_have_account))
            Row {
                Image(
                    painter = rememberImagePainter(R.drawable.image_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Image(
                    painter = rememberImagePainter(R.drawable.image_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Image(
                    painter = rememberImagePainter(R.drawable.image_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }
            Text(getString(R.string.does_not_have_account))
        }
    }
}
