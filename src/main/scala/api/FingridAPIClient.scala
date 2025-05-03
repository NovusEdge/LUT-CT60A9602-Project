package api_client

import requests._
import ujson._

import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

import model.SourceType._
import project.consts._
import model._


object FingridApiClient {
    def GetEnergyData(sourceType: SourceType): EnergyData = {
        val data = fetchEnergyData(sourceType)

        if (data.isLeft) {
            println(s"Error fetching data: ${data.left.get}")
            return new EnergyData("", sourceType, Map.empty[String, List[Double]], "")
        }
        
        val jsonData = data.right.get
        val parsedJson = ujson.read(jsonData)

        return sourceType match {
            case Surplus => new EnergyData(
                    parsedJson("data")(0)("startTime").str,
                    Surplus,
                    parsedJson("data").arr.map { item => 
                        var eType = item("additionalJson")("ProductionType").str
                        val value = item("value").num
                        (PROD_TYPE_MAP.getOrElse(eType, "Other Production"), List(value))
                    }.toMap,
                    "KWH"
                )
            case Consumption => new EnergyData(
                    parsedJson("data").arr(0)("startTime").str,
                    Consumption,
                    Map(
                        "Total Consumption" -> parsedJson("data").arr.map(item => item("value").num).toList
                    ),
                    "MWh/h"
                )
            case Wind => new EnergyData(
                    parsedJson("data").arr(0)("startTime").str,
                    Wind,
                    Map(
                        "Total Wind Power Generation" -> parsedJson("data").arr.map(item => item("value").num).toList
                    ),
                    "MWh/h"
                )
            case Solar => new EnergyData(
                    parsedJson("data").arr(0)("startTime").str,
                    Solar,
                    Map(
                        "Total Solar Power Generation" -> parsedJson("data").arr.map(item => item("value").num).toList
                    ),
                    "MWh/h"
                )
            case SolarCapacity => new EnergyData(
                    parsedJson("data").arr(0)("startTime").str,
                    SolarCapacity,
                    Map(
                        "Total Solar Power Generation Capacity" -> parsedJson("data").arr.map(item => item("value").num).toList
                    ),
                    "MW"
                )
            case WindCapacity => new EnergyData(
                    parsedJson("data").arr(0)("startTime").str,
                    WindCapacity,
                    Map(
                        "Total Wind Power Generation Capacity" -> parsedJson("data").arr.map(item => item("value").num).toList
                    ),
                    "MW"
                )
            case _ => new EnergyData("", sourceType, Map.empty[String, List[Double]], "")
        };
    }

    def fetchEnergyData(sourceType: SourceType): Either[String, String] = sourceType match {
        case Surplus => fetchData(SURPLUS_EPROD_ID)
        case Consumption => fetchData(ECONSUMPTION_FORECAST_ID)
        case Wind => fetchData(WIND_GENERATION_FORECAST_ID)
        case Solar => fetchData(SOLAR_GENERATION_FORECAST_ID)
        case SolarCapacity => fetchData(SOLAR_PRODUCTION_CAPACITY_ID)
        case WindCapacity => fetchData(WIND_PRODUCTION_CAPACITY_ID)
        case _ => Left("Invalid source type")
    }

    def fetchData(datasetId: Int, retryCount: Int = 3): Either[String, String] = {
        try {
            val response = requests.get(
                s"$API_URL$datasetId/data?format=json",
                params = Map("X-Api-Key" -> API_KEY),
            )
            
            try {
                if (response.statusCode == 429) {
                    println("Rate limit exceeded. Trying again...")
                    Thread.sleep(2000)
                    return fetchData(datasetId, retryCount)
                }
            } catch {
            case e: Exception => 
                println(s"Error while handling rate limit: ${e.getMessage}. Retrying...")
                Thread.sleep(2000)
                return fetchData(datasetId, retryCount) 
            }

            if (response.statusCode != 200) {
                return Left(s"Error: ${response.statusCode} - ${response.text()}")
            }

            Try(response.text()) match {
                case Success(data) => Right(data)
                case Failure(exception) => Left(s"Failed to fetch data: ${exception.getMessage}")
            }
        } catch {
            case e: Exception if retryCount > 0 => 
            println(s"Unexpected error: ${e.getMessage}. Retries left: ${retryCount - 1}")
            Thread.sleep(2000)
            fetchData(datasetId, retryCount - 1)
            case e: Exception => 
            Left(s"Failed after multiple retries: ${e.getMessage}")
        }
    }
}