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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.progress

import android.os.Bundle
import android.view.View
import androidx.navigation.dynamicfeatures.fragment.ui.AbstractProgressFragment
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentProgressBinding
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle

class ProgressFragment : AbstractProgressFragment(R.layout.fragment_progress) {

    var binding: FragmentProgressBinding by viewLifecycle()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProgressBinding.bind(view)
    }

    override fun onProgress(status: Int, bytesDownloaded: Long, bytesTotal: Long) {
        binding.progressBar.progress =
            ((bytesDownloaded.toDouble() * 100 / bytesTotal.toInt()).toInt())
    }

    override fun onFailed(errorCode: Int) {
        binding.progressMessage.text = getString(R.string.installation_failed)
    }

    override fun onCancelled() {
        binding.progressMessage.text = getString(R.string.installation_cancelled)
    }
}
