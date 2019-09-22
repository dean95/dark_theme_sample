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

package com.raywenderlich.pdffever.pdfManager

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileOutputStream

private const val BYTE_ARRAY_SIZE = 1024
private const val END_OF_STREAM = -1
private const val OFFSET = 0
private const val ERROR_VALUE = -1

class PdfManagerImpl(private val cacheDir: File, private val assets: AssetManager) : PdfManager {
  private lateinit var fileDescriptor: ParcelFileDescriptor

  private var pdfRenderer: PdfRenderer? = null
  private var currentPage: PdfRenderer.Page? = null

  override fun getPageToShow(index: Int, filename: String): Bitmap? {
    if (pdfRenderer == null) {
      openRenderer(filename)
    }

    if (pdfRenderer?.pageCount!! <= index) {
      return null
    }

    currentPage?.close()

    currentPage = pdfRenderer?.openPage(index)
    val bitmap = Bitmap.createBitmap(currentPage?.width!!, currentPage?.height!!, Bitmap.Config.ARGB_8888)
    currentPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
    return bitmap
  }

  override fun getCurrentPageIndex() = currentPage?.index ?: ERROR_VALUE

  override fun getPageCount() = pdfRenderer?.pageCount ?: ERROR_VALUE

  /**
   * Sets up [PdfRenderer] and related resources.
   */
  private fun openRenderer(filename: String) {

    /**
     * Files are read from the assets directory.
     */
    val file = File(cacheDir, filename)

    if (file.exists().not()) {
      /**
       * The Android Asset Packaging Tool, or aapt, selectively compresses various assets to save space on the device.
       * Since [PdfRenderer] cannot handle the compressed asset file directly, we copy it into the cache directory.
       */
      val asset = assets.open(filename)
      val output = FileOutputStream(file)
      val buffer = ByteArray(BYTE_ARRAY_SIZE)

      var size = asset.read(buffer)
      while (size != END_OF_STREAM) {
        output.write(buffer, OFFSET, size)
        size = asset.read(buffer)
      }
      asset.close()
      output.close()
    }

    fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

    pdfRenderer = PdfRenderer(fileDescriptor)
  }

  /**
   * Closes the {@link android.graphics.pdf.PdfRenderer} and related resources.
   *
   * @throws java.io.IOException When the PDF file cannot be closed.
   */
  override fun closeRenderer() {
    currentPage?.close()
    currentPage = null
    pdfRenderer?.close()
    pdfRenderer = null
    fileDescriptor.close()
  }
}