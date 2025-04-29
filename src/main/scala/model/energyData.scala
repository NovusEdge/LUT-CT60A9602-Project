package model

import api_client.FingridApiClient
import scala.util.{Failure, Success, Try}

object SourceType extends Enumeration {
    type SourceType = Value

    val Surplus, Consumption, Wind, Solar, SolarCapacity, WindCapacity = Value
}

class EnergyData(
    timestamp: String,
    sourceType: SourceType.SourceType,
    energyData: Map[String, List[Double]],
    unit: String
) {
    def getTimestamp: String = timestamp
    def getSourceType: SourceType.SourceType = sourceType
    def getEnergyData: Map[String, List[Double]] = energyData
    def getUnit: String = unit

    override def toString: String = {
        s"EnergyData(timestamp=$timestamp, sourceType=$sourceType, energyData=$energyData, unit=$unit)"
    }
}