package model

import api_client.FingridApiClient
import scala.util.{Failure, Success, Try}

import scala.math;

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

    def calculateStatistics(): Map[String, Map[String, Double]] = {
        energyData.map { case (key, values) =>
            val mean = Statistics.mean(values)
            val median = Statistics.median(values)
            val mode = Statistics.mode(values)
            val range = Statistics.range(values)
            val midrange = Statistics.midrange(values)

            key -> Map(
                "Mean" -> mean,
                "Median" -> median,
                "Mode" -> mode,
                "Range" -> range,
                "Midrange" -> midrange
            ).mapValues(
				value => BigDecimal(value)
							.setScale(2, BigDecimal.RoundingMode.HALF_UP)
							.toDouble
				).toMap
        }
    }
}