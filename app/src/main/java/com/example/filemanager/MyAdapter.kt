package com.example.easytutofilemanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.FileListActivity
import com.example.filemanager.R
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
private val formatter = SimpleDateFormat("dd.MM.yyyy")
class MyAdapter(var context: Context, var filesAndFolders: Array<File>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedFile = filesAndFolders[position]
        //Название  файлов, размер, дата создания
        holder.fileNameTV.text = selectedFile.name
        if (selectedFile.isDirectory) {
            val sizeOfDirectory = selectedFile.walkTopDown()
                .map {it.length()}
                .sum()
            holder.fileSizeTV.text = "${sizeOfDirectory/1024} Кб"
        }
        else holder.fileSizeTV.text = "${selectedFile.length()/1024} Кб"
        holder.dateOfCreateTV.text = formatter.format(selectedFile.lastModified())
        when {
            selectedFile.isDirectory -> holder.imageView.setImageResource(R.drawable.baseline_folder_24)
            selectedFile.path.endsWith(".jpeg") || selectedFile.path.endsWith(".png") || selectedFile.path.endsWith(".jpg") ->
                holder.imageView.setImageResource(R.drawable.baseline_image_24)
            selectedFile.path.endsWith(".txt") -> holder.imageView.setImageResource(R.drawable.baseline_text_snippet_24)
            else -> holder.imageView.setImageResource(R.drawable.baseline_insert_drive_file_24)
        }
        //Переход в папки
        holder.itemView.setOnClickListener {
            if (selectedFile.isDirectory) {
                val intent = Intent(context, FileListActivity::class.java)
                val path = selectedFile.absolutePath
                intent.putExtra("path", path)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else {
                //Открытие файла
                try {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    val type = "image/*"
                    intent.setDataAndType(Uri.parse(selectedFile.absolutePath), type)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        context.applicationContext,
                        "Cannot open the file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        //Реализация функций удаления, перемещения и переименования
        holder.instrumentBtn.setOnClickListener{ v ->
            val popupMenu = PopupMenu(context, v)
            popupMenu.menu.add("DELETE")
            popupMenu.menu.add("MOVE")
            popupMenu.menu.add("RENAME")
            popupMenu.setOnMenuItemClickListener { item ->
                if (item.title == "DELETE") {
                    val deleted = selectedFile.delete()
                    if (deleted) {
                        Toast.makeText(
                            context.applicationContext,
                            "DELETED ",
                            Toast.LENGTH_SHORT
                        ).show()
                        v.visibility = View.GONE
                    }
                }
                if (item.title == "MOVE") {
                    Toast.makeText(context.applicationContext, "MOVED ", Toast.LENGTH_SHORT)
                        .show()
                }
                if (item.title == "RENAME") {
                    Toast.makeText(context.applicationContext, "RENAME ", Toast.LENGTH_SHORT)
                        .show()
                }
                true
            }
            popupMenu.show()
            true
        }
    }

    //Получение количества файлов
    override fun getItemCount(): Int {
        return filesAndFolders.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fileNameTV: TextView
        var fileSizeTV: TextView
        var dateOfCreateTV: TextView
        var imageView: ImageView
        var instrumentBtn: Button

        init {
            fileNameTV = itemView.findViewById(R.id.fileNameTV)
            fileSizeTV = itemView.findViewById(R.id.fileSizeTV)
            dateOfCreateTV = itemView.findViewById(R.id.dateOfCreateTV)
            imageView = itemView.findViewById(R.id.icon_view)
            instrumentBtn = itemView.findViewById(R.id.paramBtn)
        }
    }

    private fun getFileHash(file: File): Int {
        val buffer = ByteArray(1024)
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            while (true) {
                val count = input.read(buffer)
                if (count <= 0) break
                digest.update(buffer, 0, count)
            }
        }
        return digest.digest().contentHashCode()
    }

}