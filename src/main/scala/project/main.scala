package project

import api_client.FingridApiClient
import model.SourceType._
import model._
import project.consts._

import project.ProjectFileIO
import project.consts._
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

object Main extends App {
    // Get all types of energy data from the API
    val energyDataTypes = List(
        Surplus,
        Consumption,
        Wind,
        Solar,
        SolarCapacity,
        WindCapacity
    )

    val fetchedData = energyDataTypes.map { sourceType =>
        val data = FingridApiClient.GetEnergyData(sourceType)
        Thread.sleep(2000)
        println(s"Fetched data for $sourceType: $data")
        data
    }
}