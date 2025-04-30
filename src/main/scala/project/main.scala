package project

import api_client.FingridApiClient._
import model.SourceType._
import model._
import project.consts._
import project.ProjectFileIO

import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal
import scala.annotation.tailrec


object Main extends App {
    // fetch data and write to file:
    val filePath = "energy_data.csv"
    ProjectFileIO.CacheDataToFile(filePath)
}