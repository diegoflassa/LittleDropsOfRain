package io.github.diegoflassa.littledropsofrain.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.activities.SendMessageActivity
import io.github.diegoflassa.littledropsofrain.adapters.MessageAdapter
import io.github.diegoflassa.littledropsofrain.data.AppDatabase
import io.github.diegoflassa.littledropsofrain.data.DataChangeListener
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.data.entities.Product
import io.github.diegoflassa.littledropsofrain.databinding.FragmentAdminBinding
import io.github.diegoflassa.littledropsofrain.xml.ProductParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AdminFragment : Fragment(), DataChangeListener<List<Message>> {

    private lateinit var adminViewModel: AdminViewModel
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var binding : FragmentAdminBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        adminViewModel =
            ViewModelProvider.NewInstanceFactory().create(AdminViewModel::class.java)
        adminViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textAdmin.text = it
        })
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.card_item_divider
            )!!)
        binding.recyclerviewAdmin.addItemDecoration(itemDecoration)
        binding.btnReloadProducts.setOnClickListener {
            fetchProducts()
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(context, SendMessageActivity::class.java))
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        MessageDao.loadAll(this)
    }


    private fun fetchProducts(){
        // Coroutine has mutliple dispatchers suited for different type of workloads
        ioScope.launch {
            val productParser = ProductParser()
            val products = productParser.parse()
            AppDatabase.getDatabase(requireContext(),ioScope).productDao().deleteAll()
            AppDatabase.getDatabase(requireContext(),ioScope).productDao().insertAll(*products.toTypedArray<Product?>())
        }
    }

    override fun onDataLoaded(item: List<Message>) {
        val mutList : MutableList<Message> = ArrayList()
        mutList.addAll(item)
        val adapter = MessageAdapter(requireContext(), mutList)
        binding.recyclerviewAdmin.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewAdmin.adapter = adapter
        binding.recyclerviewAdmin.visibility = View.VISIBLE
    }

}