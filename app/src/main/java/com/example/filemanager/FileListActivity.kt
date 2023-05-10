package com.example.filemanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easytutofilemanager.MyAdapter
import java.io.File
import java.sql.Time
import java.sql.Timestamp
import java.time.Instant


class FileListActivity : AppCompatActivity() {

    private lateinit var filesAndFolders: Array<File>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private var currentSortOrder: SortOrder = SortOrder.ASCENDING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)
        recyclerView = findViewById(R.id.recyclerView)
        val noFilesText = findViewById<TextView>(R.id.nofilesTV)
        val path = intent.getStringExtra("path")
        val root = File(path!!)
        filesAndFolders = root.listFiles() as Array<File>
        if (filesAndFolders.isEmpty()) {
            noFilesText.isInvisible = false
            return
        }
        noFilesText.isInvisible = true
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(applicationContext, filesAndFolders)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sortByName -> {
                currentSortOrder = if (currentSortOrder == SortOrder.ASCENDING) {
                    filesAndFolders.sortBy { it.name }
                    SortOrder.DESCENDING
                } else {
                    filesAndFolders.sortByDescending { it.name }
                    SortOrder.ASCENDING
                }
                adapter.notifyDataSetChanged()
                true
            }
            R.id.sortBySize -> {
                currentSortOrder = if(currentSortOrder == SortOrder.ASCENDING) {
                    filesAndFolders.sortBy { getFolderSize(it) }
                    SortOrder.DESCENDING
                } else {
                    filesAndFolders.sortByDescending { getFolderSize(it) }
                    SortOrder.ASCENDING
                }
                adapter.notifyDataSetChanged()
                true
            }
            R.id.sortByDate -> {
                currentSortOrder = if(currentSortOrder == SortOrder.ASCENDING) {
                    filesAndFolders.sortBy { it.lastModified() }
                    SortOrder.DESCENDING
                } else {
                    filesAndFolders.sortByDescending { it.lastModified() }
                    SortOrder.ASCENDING
                }
                adapter.notifyDataSetChanged()
                true
            }
            R.id.sortByExtension -> {
                currentSortOrder = if (currentSortOrder == SortOrder.ASCENDING) {
                    filesAndFolders.sortBy { it.extension }
                    SortOrder.DESCENDING
                } else {
                    filesAndFolders.sortByDescending { it.extension }
                    SortOrder.ASCENDING
                }
                adapter.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getFolderSize(file:File): Long {
        var size: Long = 0
        size = if (file.isDirectory) {
            file.walkTopDown()
                .map {it.length()}
                .sum()
        } else {
            file.length()
        }
        return size
    }
    private enum class SortOrder {
        ASCENDING,
        DESCENDING
    }
}
