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

object ProjectFileIO {
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

	def GetCachedData(filePath: String): Map[String, List[EnergyData]] = {
		readDataFromJSON(filePath).getOrElse(Map.empty[String, List[EnergyData]])
	}

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
