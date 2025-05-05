package model

import api_client.FingridApiClient
import scala.util.{Failure, Success, Try}

import scala.math;

/**
 * Enumeration of different energy source types supported by the application.
 * 
 * These types correspond to different datasets available from the Fingrid API.
 */
object SourceType extends Enumeration {
    type SourceType = Value

    val Surplus, Consumption, Wind, Solar, SolarCapacity, WindCapacity = Value
}

/**
 * Represents energy data for a specific source type at a specific time.
 * 
 * This class encapsulates energy data retrieved from the Fingrid API and provides
 * methods for accessing the data and calculating statistics.
 * 
 * @param timestamp The timestamp when the data was recorded
 * @param sourceType The type of energy source for this data
 * @param energyData A map of energy data values by category
 * @param unit The unit of measurement for the energy data
 */
class EnergyData(
    timestamp: String,
    sourceType: SourceType.SourceType,
    energyData: Map[String, List[Double]],
    unit: String
) {
    /**
     * @return The timestamp when the data was recorded
     */
    def getTimestamp: String = timestamp
    
    /**
     * @return The type of energy source for this data
     */
    def getSourceType: SourceType.SourceType = sourceType
    
    /**
     * @return A map of energy data values by category
     */
    def getEnergyData: Map[String, List[Double]] = energyData
    
    /**
     * @return The unit of measurement for the energy data
     */
    def getUnit: String = unit

    /**
     * @return A string representation of this energy data
     */
    override def toString: String = {
        s"EnergyData(timestamp=$timestamp, sourceType=$sourceType, energyData=$energyData, unit=$unit)"
    }

    /**
     * Checks if this energy data object contains any data.
     * 
     * @return true if the energy data map is empty, false otherwise
     */
	def isEmpty: Boolean = {
		energyData.isEmpty
	}

    /**
     * Calculates various statistical measures for each category of energy data.
     * 
     * @return A map of statistical values by category, including mean, median, mode, range, and midrange
     */
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