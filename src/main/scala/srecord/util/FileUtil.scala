package srecord.util

import java.io.File
import java.io.IOException

/**
 * File-related utilities
 */
object FileUtil {

    /**
     * Delete a file/directory, recursively.
     */
    def deleteRecursively(file: File) {
      if (file.exists()) {
        if (file.isDirectory()) {
          val files = file.listFiles
          files.foreach { deleteRecursively _ }
        } else if (!file.delete()) throw new IOException("Unable to delete: "+ file);
      }
    }
    
}