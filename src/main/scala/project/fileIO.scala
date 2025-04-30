package project

import model.EnergyData
import api_client.FingridApiClient._
import consts._
import model.SourceType._
import model._

import java.io._

object ProjectFileIO {
    def CacheDataToFile(filePath: String): Unit = {
        // Get all kinda data:
        val energyDataTypes = List(
            Surplus,
            Consumption,
            Wind,
            Solar,
            SolarCapacity,
            WindCapacity
        )

        // Fetch data from the API
        val fetchedData = energyDataTypes.map { sourceType =>
            val data = GetEnergyData(sourceType)
            Thread.sleep(2000)
            // debug print statement
            println(s"Fetched updated date for $sourceType")
            data
        }

        writeDataToCSV(fetchedData, filePath);
    }

    def writeDataToCSV(data: List[EnergyData], filePath: String): Unit = {
        val file = new File(filePath)
        val writer = new FileWriter(file)

        val header = "Timestamp,SourceType,EnergyData,Unit\n"
        writer.write(header)

        data.foreach { energyData =>
            val line = s"${energyData.getTimestamp},${energyData.getSourceType},${energyData.getEnergyData},${energyData.getUnit}\n"
            writer.write(line)
        }
        writer.close()
    }

    // def readDataFromCSV(filePath: String): List[EnergyData] = {
    //     val file = new File(filePath)
    //     val reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))) 
    //     val data = scala.collection.mutable.ListBuffer[EnergyData]()
    //     var line: String = null
    //     try {
    //         while ({ line = reader.readLine(); line != null }) {
    //             val parts = line.split(",")
    //             if (parts.length == 4) {
    //                 val timestamp = parts(0)
    //                 val sourceType = SourceType.withName(parts(1))
    //                 val energyData = parts(2).split(";").map { item =>
    //                     val kv = item.split(":")
    //                     kv(0) -> kv(1).toDouble
    //                 }.toMap
    //                 val unit = parts(3)
    //                 data += new EnergyData(timestamp, sourceType, energyData, unit)
    //             }
    //         }
    //     } catch {
    //         case e: Exception => println(s"Error reading file: ${e.getMessage}")
    //     } finally {
    //         reader.close()
    //     }
    //     data.toList
    // }
}