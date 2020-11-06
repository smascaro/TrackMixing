package com.smascaro.trackmixing.base.utils

import android.content.Context
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.inject.Inject

class FilesStorageHelper @Inject constructor(private val mContext: Context) {
    fun getBaseDirectory(): String {
        return mContext.filesDir.path
    }

    fun getBaseDirectoryByVideoId(videoId: String): String {
        return "${mContext.filesDir}/$videoId"
    }

    fun writeFileToStorage(baseDirectory: String, videoId: String, stream: InputStream): String {
        Timber.d("Writing to storage download with id $videoId and a length of ${stream.available()} bytes")
        val targetDirectoryFile = File(baseDirectory, videoId)
        targetDirectoryFile.mkdirs()
        val targetFile = File(targetDirectoryFile, "$videoId.zip")
        return try {
            stream.saveToFile(targetFile.path)
            targetFile.path
        } catch (e: Exception) {
            Timber.e(e)
            throw e
        }
    }

    fun checkContent(baseDirectory: String): Boolean {
        return try {
            val directoryToCheck = File(baseDirectory)
            if (!directoryToCheck.exists()) {
                throw FileNotFoundException("Directory $baseDirectory does not exist")
            }
            if (!directoryToCheck.isDirectory) {
                throw IllegalArgumentException("Path supplied is not a directory: $baseDirectory")
            }
            val countFiles = directoryToCheck.listFiles { _, file ->
                file.startsWith("vocals.") ||
                        file.startsWith("bass.") ||
                        file.startsWith("other.") ||
                        file.startsWith("drums.")
            }?.count()

            return if (countFiles == null) {
                false
            } else countFiles >= 4
        } catch (e: Exception) {
            Timber.w(e)
            false
        }
    }

    private fun InputStream.saveToFile(file: String) = use { input ->
        File(file).outputStream().use { output ->
            input.copyTo(output)
        }
    }

    data class ZipIO(val entry: ZipEntry, val output: File)

    fun unzipContent(pathToZipFile: String) {
        val zipFile = File(pathToZipFile)
        try {
            ZipFile(pathToZipFile).use { zip ->
                zip.entries().asSequence().map { entry ->
                    val outputFile = File(zipFile.parent, entry.name)
                    ZipIO(
                        entry,
                        outputFile
                    ).also { zipio ->
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
        } catch (e: Exception) {
            Timber.e(e)
            throw Exception("Exception unzipping contents", e)
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

    fun deleteData(path: String) {
        try {
            File(path).deleteRecursively()
        } catch (e: Exception) {
            Timber.e("Exception while deleting $path")
            Timber.e(e)
        }
    }
}