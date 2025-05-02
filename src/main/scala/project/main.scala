package project

import api_client.FingridApiClient._

import model.SourceType._
import model._
import project.consts._
import project.ProjectFileIO._

import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal
import scala.annotation.tailrec


object Main extends App {
    val filePath = "energy_data.json"

    @tailrec
    def mainloop(): Unit = {
        println(MAIN_MENU);
        var fetchedData: Map[String, List[EnergyData]] = Map.empty[String, List[EnergyData]]

        val option = scala.io.StdIn.readLine("Select an option: ")
        option match {
            case "1" => {
                println(DATASET_MENU)
                mainloop()
            }
            case "2" => {
                println("Fetching data...")
                val energyDataTypes = List(
                    Surplus,
                    Consumption,
                    Wind,
                    Solar,
                    SolarCapacity,
                    WindCapacity
                )
                fetchedData = energyDataTypes.map { sourceType =>
                    val data = GetEnergyData(sourceType)
                    Thread.sleep(2000)
                    println(s"Fetched updated date for $sourceType")
                    (sourceType.toString(), List(data))
                }.toMap
                val _ = writeDataToJSON(fetchedData, filePath);
                println("Fetched all data and stored to file.")
                mainloop()
            }
            case "3" => {
                // println("Enter dataset number(s) (1,2,3...6): ")
                // val datasetNumber = scala.io.StdIn.readLine()
                // val _ = datasetNumber match {
                //     case "1" | "2" | "3" | "4" | "5" | "6" => {
                //         if (fetchedData.isEmpty) {
                //             fetchedData = GetCachedData(filePath)
                //         }

                //         // check if the fetchedData has the key according to the dataset numbers
                //         var datasetName = datasetNumber match {
                //             case "1" => "Surplus"
                //             case "2" => "Consumption"
                //             case "3" => "Wind"
                //             case "4" => "Solar"
                //             case "5" => "SolarCapacity"
                //             case "6" => "WindCapacity"
                //         }
                //         if (!fetchedData.contains(datasetName)) {
                //             println("No data found for the given dataset number.")
                //             println("Would you like to fetch the data? (y/n)")
                //             val fetchOption = scala.io.StdIn.readLine()
                //             if (fetchOption.toLowerCase == "y") {
                //                 fetchedData = Map(
                //                     datasetName -> List(GetEnergyData(datasetName match {
                //                         case "Surplus" => Surplus
                //                         case "Consumption" => Consumption
                //                         case "Wind" => Wind
                //                         case "Solar" => Solar
                //                         case "SolarCapacity" => SolarCapacity
                //                         case "WindCapacity" => WindCapacity
                //                     }))
                //                 )
                //             } else {
                //                 println("No data found. Returning to main menu.")
                //                 return
                //             }
                //         }
                        
                //         println(s"Statistics for $datasetNumber:")
                //         val data = datasetNumber match {
                //             case "1" => fetchedData("Surplus")
                //             case "2" => fetchedData("Consumption")
                //             case "3" => fetchedData("Wind")
                //             case "4" => fetchedData("Solar")
                //             case "5" => fetchedData("SolarCapacity")
                //             case "6" => fetchedData("WindCapacity")
                //         }

                //         data.foreach(_.calculateStatistics)
                //     }
                //     case "ALL" => {
                        println("Statistics for all datasets:")
                        if (fetchedData.isEmpty) {
                            fetchedData = GetCachedData(filePath)
                        }
                        fetchedData.foreach { case (key, data) =>
                            println(s"Statistics for $key:")
                            data.foreach(_.calculateStatistics)
                        }
                    // }
                    // case _   => println("Invalid option. Please try again."); return
                // }
                mainloop()
            }
            case "4" => {
                writeDataToJSON(fetchedData, filePath)
                mainloop()
            }
            case "5" => {
                println("Enter file path to read data from: ")
                val filePath = scala.io.StdIn.readLine()
                val data = readDataFromJSON(filePath)
                data.foreach { case (key, value) =>
                    println(s"Data for $key:")
                    value.foreach { energyData =>
                        println(energyData)
                    }
                }
                mainloop()
            }
            case "6" => {
                println("Stored data:")
                val data = readDataFromJSON(filePath)
                data.foreach { case (key, value) =>
                    println(s"Data for $key:")
                    value.foreach { energyData =>
                        println(energyData)
                    }
                }
                mainloop()
            }
            case "0" => {
                println("Exiting...")
                return
            }
            case _   => println(E_INVALID_OPTION); mainloop()
        }
    }

    mainloop()
}