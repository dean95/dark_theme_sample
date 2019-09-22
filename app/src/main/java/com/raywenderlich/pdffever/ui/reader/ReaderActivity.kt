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

package com.raywenderlich.pdffever.ui.reader

import android.content.Context
import android.content.Intent
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Observer
import com.raywenderlich.pdffever.R
import kotlinx.android.synthetic.main.activity_reader.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.IOException

private const val FIRST_PAGE_INDEX = 0

class ReaderActivity : AppCompatActivity() {

  private val viewModel: ReaderViewModel by viewModel()

  private lateinit var filename: String
  private lateinit var readerUiModel: ReaderUiModel

  companion object {
    private const val EXTRAS_FILENAME = "filename_extra"
    private const val EXTRA_CURRENT_PAGE_INDEX = "current_page_index"

    fun getStartIntent(context: Context, filename: String) =
        Intent(context, ReaderActivity::class.java).apply {
          putExtra(EXTRAS_FILENAME, filename)
        }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val pageIndex = savedInstanceState?.getInt(EXTRA_CURRENT_PAGE_INDEX) ?: FIRST_PAGE_INDEX
    setContentView(R.layout.activity_reader)
    extractArguments()
    subscribeToViewModel()
    initUi(pageIndex)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putInt(EXTRA_CURRENT_PAGE_INDEX, readerUiModel.currentPageIndex)
    super.onSaveInstanceState(outState)
  }

  override fun onStop() {
    super.onStop()
    try {
      viewModel.closeRenderer()
    } catch (exception: IOException) {
      exception.printStackTrace()
    }
  }

  private fun render(readerUiModel: ReaderUiModel) {
    this.readerUiModel = readerUiModel
    documentPage.setImageBitmap(readerUiModel.bitmap)
    val index = readerUiModel.currentPageIndex
    val pageCount = readerUiModel.pageCount
    previousPageButton.isEnabled = index != FIRST_PAGE_INDEX
    nextPageButton.isEnabled = index.inc() < pageCount
  }

  private fun initUi(pageIndex: Int) {
    viewModel.getPageToShow(filename, pageIndex)
    supportActionBar?.title = filename
    previousPageButton.setOnClickListener {
      viewModel.getPageToShow(
          filename,
          readerUiModel.currentPageIndex.dec()
      )
    }
    nextPageButton.setOnClickListener {
      viewModel.getPageToShow(
          filename,
          readerUiModel.currentPageIndex.inc()
      )
    }
  }

  private fun subscribeToViewModel() {
    viewModel.getPage().observe(this, Observer {
      render(it)
    })
  }

  private fun extractArguments() {
    filename = intent.getStringExtra(EXTRAS_FILENAME) ?: ""
  }
}
