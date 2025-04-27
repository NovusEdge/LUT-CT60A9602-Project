package api_client

import requests._

import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal
import model.SourceType._
import project.consts._


object FingridApiClient {
    def fetchEnergyData(sourceType: SourceType): Either[String, String] = sourceType match {
        case Surplus => fetchData(SURPLUS_EPROD_ID)
        case Consumption => fetchData(ECONSUMPTION_FORECAST_ID)
        case Wind => fetchData(WIND_GENERATION_FORECAST_ID)
        case Solar => fetchData(SOLAR_GENERATION_FORECAST_ID)
        case SolarCapacity => fetchData(SOLAR_PRODUCTION_CAPACITY_ID)
        case WindCapacity => fetchData(WIND_PRODUCTION_CAPACITY_ID)
        case _ => Left("Invalid source type")
    }
    

    def fetchData(datasetId: Int): Either[String, String] = {
        val response = requests.get(
                s"$API_URL$datasetId/data?format=json",
                params = Map("X-Api-Key" -> API_KEY),
            )
        
        if (response.statusCode != 200) {
            return Left(s"Error: ${response.statusCode} - ${response.text()}");
        }

        Try(response.text()) match {
            case Success(data) => Right(data)
            case Failure(exception) => Left(s"Failed to fetch data: ${exception.getMessage}")
        }

    }
}