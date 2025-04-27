package project

import api_client.FingridApiClient
import model.EnergyData
import project.ProjectFileIO
import project.consts.{API_KEY, API_URL, TIMEOUT}
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

object Main extends App {
    // Test out the API client:
    val sourceType = model.SourceType.Surplus
    val energyData = FingridApiClient.fetchEnergyData(sourceType)
    energyData match {
        case Right(data) =>
            println(s"Fetched data: $data")
        case Left(error) =>
            println(s"Error fetching data: $error")
    }
}