package model

import api_client.FingridApiClient
import model.SourceType.SourceType
import scala.util.{Failure, Success, Try}

object SourceType extends Enumeration {
    type SourceType = Value

    val Surplus, Consumption, Wind, Solar, SolarCapacity, WindCapacity = Value
}

class EnergyData(
    timestamp: String,
    sourceType: SourceType.SourceType,
    energyData: Map[String, Double]
) {
    def getTimestamp: String = timestamp
    def getSourceType: SourceType.SourceType = sourceType
    def getEnergyData: Map[String, Double] = energyData

    override def toString: String = {
        s"EnergyData(timestamp=$timestamp, sourceType=$sourceType, energyData=$energyData)"
    }

    def fetchEnergyData(): Map[String, Double] = {
        val data = FingridApiClient.fetchEnergyData(sourceType)
        data match {
            case Right(json) =>
                json.asInstanceOf[Map[String, Double]]
            case Left(error) =>
                println(s"Error fetching data: $error")
                Map.empty[String, Double]
        }
    }

}