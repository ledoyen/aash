package com.ledoyen.scala.aash.tool

import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter

object Files {

  def createFolderIfNotExists(folderPath: String) = {
    val file = new File(folderPath)
    val parent = file.getParentFile
    if(file.exists && !file.isDirectory) {
      if(!file.delete) {
        throw new IllegalStateException(s"Unable to delete file [$folderPath]")
      }
    }
    if(!file.exists) {
      if(!file.mkdirs) {
        throw new IllegalStateException(s"Unable to create folder [$folderPath]")
      }
    }
  }

  def writeToFile(fileName: String, content: String) = {
    val file = new File(fileName)
    val writer = new BufferedWriter(new FileWriter(file))
    try {
      writer.write(content)
    } finally {
      writer.close
    }
  }
}