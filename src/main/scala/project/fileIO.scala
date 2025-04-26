package project

import model.EnergyData

import java.io.{BufferedReader, File, FileWriter, InputStreamReader}

object ProjectFileIO {
    def writeDataToCSV(data: List[EnergyData], filePath: String): Unit = {
        val file = new File(filePath)
        val writer = new FileWriter(file)

        // TODO: Implement the rest of the CSV writing logic
    }

    def readDataFromCSV(filePath: String): List[EnergyData] = {
        val file = new File(filePath)
        val reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))

        // TODO: Implement the rest of the CSV reading logic

        // val data = Iterator.continually(reader.readLine()).takeWhile(_ != null).drop(1).map { line =>
        //     val Array(date, value) = line.split(",")
        //     EnergyData(date, value.toDouble)
        // }.toList

        // reader.close()
        // data
    }
}