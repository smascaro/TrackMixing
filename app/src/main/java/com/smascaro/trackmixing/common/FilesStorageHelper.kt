package com.smascaro.trackmixing.common

import android.content.Context
import android.os.Environment
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class FilesStorageHelper(private val mContext: Context) {

    fun getBaseDirectory(): String {
        return mContext.filesDir.path
    }

    fun getBaseDirectoryByVideoId(videoId: String): String {
        return "${mContext.filesDir}/$videoId"
    }

    fun writeFileToStorage(baseDirectory: String, videoId: String, body: ResponseBody): String {
        Timber.d("Writing to storage download with id $videoId and a length of ${body.contentLength()} bytes")
        val targetDirectoryFile = File(baseDirectory, videoId)
        targetDirectoryFile.mkdirs()
        val targetFile = File(targetDirectoryFile, "$videoId.zip")
        return try {
            body.byteStream()?.saveToFile(targetFile.path)
            targetFile.path
        } catch (e: Exception) {
            Timber.e(e)
            ""
        }

    }


    private fun InputStream.saveToFile(file: String) = use { input ->
        File(file).outputStream().use { output ->
            input.copyTo(output)
        }
    }


    data class ZipIO(val entry: ZipEntry, val output: File)

    fun unzipContent(pathToZipFile: String): Boolean {
        val zipFile = File(pathToZipFile)
        return try {
            ZipFile(pathToZipFile).use { zip ->
                zip.entries().asSequence().map { entry ->
                    val outputFile = File(zipFile.parent, entry.name)
                    ZipIO(entry, outputFile).also { zipio ->
                        zipio.output.parentFile?.run {
                            if (!exists()) {
                                mkdirs()
                            }
                        }
                    }
                }.filter {
                    !it.entry.isDirectory
                }.forEach { (entry, output) ->
                    zip.getInputStream(entry).use { input ->
                        output.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    fun deleteFile(pathToFile: String): Boolean {
        return try {
            File(pathToFile).delete()
            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }
}