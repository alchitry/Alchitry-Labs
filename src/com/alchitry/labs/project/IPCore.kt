package com.alchitry.labs.project

import java.io.File
import java.io.IOException

data class IPCore(
    val name: String,
    var stub: File? = null,
    val files: MutableList<File> = mutableListOf()
) {
    companion object {
        fun delete(core: String?, projectFolder: String): Boolean {
            val coreGenFolder = File(projectFolder + File.separatorChar + Project.CORES_FOLDER)
            val fileList = coreGenFolder.listFiles()
            if (fileList != null) for (f in fileList) {
                val name = f.name
                if (name.startsWith(core!!)) {
                    try {
                        delete(f)
                    } catch (e: IOException) {
                        return false
                    }
                }
            }
            return true
        }

        @Throws(IOException::class)
        private fun delete(file: File) {
            if (file.isDirectory) {
                val files = file.list() ?: throw IOException("Could not read file list for $file")

                // directory is empty, then delete it
                if (files.isEmpty()) {
                    file.delete()
                } else {
                    // list all the directory contents
                    for (temp in files) {
                        // construct the file structure
                        val fileDelete = File(file, temp)
                        // recursive delete
                        delete(fileDelete)
                    }
                    // check the directory again, if empty then delete it
                    if (files.isEmpty()) {
                        file.delete()
                    }
                }
            } else if (file.isFile) {
                // if file, then delete it
                file.delete()
            }
        }
    }

}