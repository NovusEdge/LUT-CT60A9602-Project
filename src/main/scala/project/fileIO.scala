package project

import model.EnergyData
import api_client.FingridApiClient._
import consts._
import model.SourceType._
import model._

import scala.io.Source
import scala.util.{Failure, Success, Try}
import ujson.{Value => UjsonValue, Obj, Arr, write, read}
import java.io._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Provides file I/O operations for energy data, including caching, reading, and writing.
 *
 * This object handles the persistence of energy data to and from JSON files,
 * as well as data format conversions between the application model and JSON.
 */
object ProjectFileIO {
	/**
	 * Fetches energy data from the API and caches it to a file.
	 *
	 * @param filePath Path to the file where data will be cached
	 * @return A map containing the fetched energy data by source type
	 */
	def CacheDataToFile(filePath: String): Map[String, List[EnergyData]] = {
		val energyDataTypes = List(
			Surplus,
			Consumption,
			Wind,
			Solar,
			SolarCapacity,
			WindCapacity
		)

		val fetchedData = energyDataTypes.map { sourceType =>
			val data = GetEnergyData(sourceType)
			if (data.getEnergyData.isEmpty) {
				println(s"No data found for $sourceType")
				return Map.empty[String, List[EnergyData]]
			} else {
				Thread.sleep(2000)
				println(s"Fetched updated date for $sourceType")
				(sourceType.toString(), List(data))
			}
		}.toMap

		writeDataToJSON(fetchedData, filePath);

		return fetchedData;
	}

	/**
	 * Retrieves cached energy data from a file.
	 *
	 * @param filePath Path to the file containing cached data
	 * @return A map containing the cached energy data by source type
	 */
	def GetCachedData(filePath: String): Map[String, List[EnergyData]] = {
		readDataFromJSON(filePath).getOrElse(Map.empty[String, List[EnergyData]])
	}

	/**
	 * Writes energy data to a JSON file.
	 *
	 * @param data Map of energy data to write, keyed by source type
	 * @param filePath Path to the file where data will be written
	 * @return A Try containing Unit if successful, or an exception if failed
	 */
	def writeDataToJSON(
		data: Map[String, List[EnergyData]],
		filePath: String
	): Try[Unit] = Try {
		val file = new File(filePath)
		val parentDir = file.getParentFile
		if (parentDir != null && !parentDir.exists()) {
		parentDir.mkdirs()
		}

		// Convert the map to a proper JSON object instead of an array
		val jsonObj = ujson.Obj()
		data.foreach { case (sourceType, dataList) =>
		jsonObj(sourceType) = dataList.map { data =>
			val energyDataObj = ujson.Obj()
			data.getEnergyData.foreach { case (key, values) =>
			energyDataObj(key) = values
			}

			ujson.Obj(
			"timestamp" -> data.getTimestamp,
			"sourceType" -> data.getSourceType.toString,
			"energyData" -> energyDataObj,
			"unit" -> data.getUnit
			)
		}
		}

		val writer = new FileWriter(file)
		try {
		writer.write(write(jsonObj, indent = 4))
		} finally {
		writer.close()
		}
	}

	/**
	 * Reads energy data from a JSON file.
	 *
	 * @param filePath Path to the file containing JSON data
	 * @return A Try containing a map of energy data by source type if successful,
	 *         or an exception if failed
	 */
	def readDataFromJSON(filePath: String): Try[Map[String, List[EnergyData]]] =
		Try {
		val source = Source.fromFile(filePath)
		try {
			val jsonString = source.mkString
			val json = read(jsonString)

			// Parse the JSON object format
			json.obj.map { case (sourceType, dataArray) =>
			val dataList = dataArray.arr.map { entry =>
				val timestamp = entry("timestamp").str
				val sourceTypeStr = entry("sourceType").str
				val unit = entry("unit").str

				val energyData = entry("energyData").obj.map { case (key, values) =>
				(key, values.arr.map(_.num).toList)
				}.toMap

				val sourceTypeEnum = sourceTypeStr match {
				case "Surplus"       => Surplus
				case "Consumption"   => Consumption
				case "Wind"          => Wind
				case "Solar"         => Solar
				case "SolarCapacity" => SolarCapacity
				case "WindCapacity"  => WindCapacity
				case _               => Surplus // Default case
				}

				new EnergyData(timestamp, sourceTypeEnum, energyData, unit)
			}.toList

			(sourceType, dataList)
			}.toMap
		} finally {
			source.close()
		}
		}

	/**
	 * Converts an energy data entry to JSON format.
	 *
	 * @param entry A tuple containing the source type and a list of energy data
	 * @return A ujson.Value representing the JSON format of the energy data
	 */
	def dataToJson(entry: (String, List[EnergyData])): ujson.Value = {
		val (sourceType, dataList) = entry
		ujson.Obj(
		"type" -> sourceType,
		"data" -> dataList.map { data =>
			val energyDataObj = ujson.Obj()
			data.getEnergyData.foreach { case (key, values) =>
			energyDataObj(key) = values
			}

			ujson.Obj(
			"timestamp" -> data.getTimestamp,
			"sourceType" -> data.getSourceType.toString,
			"energyData" -> energyDataObj,
			"unit" -> data.getUnit
			)
		}
		)
	}

	/**
	 * Converts JSON data to an energy data entry.
	 *
	 * @param json A ujson.Value containing energy data in JSON format
	 * @return An Option containing a tuple of source type and a list of energy data if successful,
	 *         or None if parsing failed
	 */
	def jsonToData(json: ujson.Value): Option[(String, List[EnergyData])] = {
		try {
		val sourceType = json("type").str
		val data = json("data").arr.map { entry =>
			val timestamp = entry("timestamp").str
			val sourceTypeStr = entry("sourceType").str
			val unit = entry("unit").str

			val sourceTypeEnum = SourceType.withName(sourceTypeStr)

			val energyDataMap = entry("energyData").obj.map { case (key, values) =>
			(key, values.arr.map(_.num).toList)
			}.toMap

			new EnergyData(
			timestamp,
			sourceTypeEnum,
			energyDataMap,
			unit
			)
		}.toList

		Some(sourceType -> data)
		} catch {
		case e: ujson.Value.InvalidData =>
			println(s"Invalid JSON structure: ${e.getMessage}")
			None
		case e: NoSuchElementException =>
			println(s"Missing expected JSON field: ${e.getMessage}")
			None
		case e: Exception =>
			println(s"Failed to parse JSON object: ${e.getMessage}")
			None
		}
	}

	/**
	 * Formats a timestamp string into a date and time string pair.
	 *
	 * @param timestamp The timestamp string in ISO format
	 * @return A tuple containing the formatted date and time strings
	 */
	def formatTimestamp(timestamp: String): (String, String) = {
		try {
			val parsedTimestamp = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)
			val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
			val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
			(parsedTimestamp.format(dateFormatter), parsedTimestamp.format(timeFormatter))
		} catch {
			case _: Exception => ("Invalid date", "Invalid time")
		}
	}
}
