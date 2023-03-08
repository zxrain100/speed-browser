package com.sdb.ber

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdb.ber.databinding.ActMarkBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SDBMActivity : BaseActivity() {
    private lateinit var binding: ActMarkBinding

    private val adapter: MHAdapter = MHAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActMarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.statusBar.setStatusBar()

        binding.markClose.setOnClickListener { finish() }

        binding.markRecycler.layoutManager = LinearLayoutManager(this)
        binding.markRecycler.adapter = adapter

        adapter.setOnDeleteListener { _, info ->
            deleteData(info)
            loadData()
        }
        adapter.setOnItemClickListener { _, info ->
            val intent = Intent()
            intent.putExtra("url", info.url)
            setResult(RESULT_OK, intent)
            finish()
        }

        loadData()
    }


    private fun loadData() {
        launch {
            val list = mutableListOf<Info>()
            val data = SDBBManager.instance.markDao().getAll()
            data.forEach {
                list.add(Info(it.url, it.title, it.time))
            }

            list.sortBy { -it.time }

            withContext(Dispatchers.Main) {
                adapter.submitList(list)
            }
        }

    }

    private fun deleteData(item: Info) {
        val page = SDBBManager.instance.markDao().getByUrl(item.url)
        if (page != null) {
            SDBBManager.instance.markDao().deletePage(page)
        }
    }
}