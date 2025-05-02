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
      Thread.sleep(2000)
      println(s"Fetched updated date for $sourceType")
      (sourceType.toString(), List(data))
    }.toMap

    writeDataToJSON(fetchedData, filePath);

    return fetchedData;
  }

  def GetCachedData(filePath: String): Map[String, List[EnergyData]] = {
    readDataFromJSON(filePath)
  }

  def writeDataToJSON(
      data: Map[String, List[EnergyData]],
      filePath: String
  ): Unit = {
    val jsonArray = Arr.from(data.map(dataToJson))

    val writer = new FileWriter(new File(filePath))
    try {
      writer.write(write(jsonArray, indent = 2))
    } finally {
      writer.close()
    }
  }

  def readDataFromJSON(filePath: String): Map[String, List[EnergyData]] = {
    val source = Source.fromFile(filePath)
    try {
      val jsonString = source.mkString
      val json = read(jsonString)

      json.arr
        .map(jsonToData)
        .filter(_.isDefined)
        .map(_.get)
        .toMap
    } catch {
      case e: Exception =>
        println(s"Error reading file: ${e.getMessage}")
        Map.empty[String, List[EnergyData]]
    } finally {
      source.close()
    }
  }

  def dataToJson(entry: (String, List[EnergyData])): ujson.Value = {
    val (sourceType, dataList) = entry
    ujson.Obj(
      "type" -> sourceType,
      "data" -> dataList.map { data =>
        ujson.Obj(
          "timestamp" -> data.getTimestamp,
          "sourceType" -> data.getSourceType.toString,
          "energyData" -> data.getEnergyData.map { case (key, values) =>
            ujson.Obj(
              key -> values
            )
          },
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

        val energyDataMap = entry("energyData").arr.flatMap { obj =>
          obj.obj.headOption.map { case (key, valueJson) =>
            val values = valueJson.arr.map(_.num).toList
            key -> values
          }
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
}
