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

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.pdffever.R
import com.raywenderlich.pdffever.domain.model.FileModel
import com.raywenderlich.pdffever.ui.reader.ReaderActivity
import com.raywenderlich.pdffever.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {

  private val viewModel: HomeViewModel by viewModel()

  private val adapter by lazy { HomeAdapter(::onFileItemClick) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)
    initialize()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_main_settings -> {
        showSettingsScreen()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun initialize() {
    supportActionBar?.title = getString(R.string.home_activity_title)
    initializeRecyclerView()
    subscribeToViewModel()
  }

  private fun initializeRecyclerView() {
    val layoutManager = LinearLayoutManager(this)

    filesList.layoutManager = layoutManager
    filesList.adapter = adapter
    filesList.setHasFixedSize(true)

    val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
    filesList.addItemDecoration(itemDecoration)
  }

  private fun subscribeToViewModel() {
    viewModel.getFiles().observe(this, Observer { adapter.onFilesChanged(it) })
  }

  private fun onFileItemClick(file: FileModel) {
    val intent = ReaderActivity.getStartIntent(this, file.filename)
    startActivity(intent)
  }

  private fun showSettingsScreen() {
    startActivity(SettingsActivity.getStartIntent(this))
  }
}
