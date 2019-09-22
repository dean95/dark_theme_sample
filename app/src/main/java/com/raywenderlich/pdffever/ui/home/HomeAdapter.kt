/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.pdffever.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.pdffever.R
import com.raywenderlich.pdffever.domain.model.FileModel
import com.raywenderlich.pdffever.ui.extensions.inflate
import kotlinx.android.synthetic.main.file_list_item.view.*

class HomeAdapter(private val clickListener: (FileModel) -> Unit) : RecyclerView.Adapter<HomeAdapter.FileHolder>() {

  private val files = mutableListOf<FileModel>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FileHolder(parent.inflate(R.layout.file_list_item), clickListener)

  override fun onBindViewHolder(holder: FileHolder, position: Int) = holder.bind(files[position])

  override fun getItemCount() = files.size

  fun onFilesChanged(files: List<FileModel>) {
    this.files.clear()
    this.files.addAll(files)
    notifyDataSetChanged()
  }

  class FileHolder(itemView: View, private val clickListener: (FileModel) -> Unit) : RecyclerView.ViewHolder(itemView) {

    fun bind(file: FileModel) = with(itemView) {
      filename.text = file.filename
      setOnClickListener { clickListener(file) }
    }
  }
}