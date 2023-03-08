package com.sdb.ber

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdb.ber.databinding.ActHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SDBHActivity : BaseActivity() {
    private lateinit var binding: ActHistoryBinding
    private val adapter: MHAdapter = MHAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.statusBar.setStatusBar()

        binding.back.setOnClickListener { finish() }
        binding.hisRecycle.layoutManager = LinearLayoutManager(this)
        binding.hisRecycle.adapter = adapter

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
            val manager = SDBBManager.instance
            val data = manager.historyDao().getAll()
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
        val manager = SDBBManager.instance
        val page = manager.historyDao().getByUrl(item.url)
        if (page != null) {
            manager.historyDao().deletePage(page)
        }
    }
}