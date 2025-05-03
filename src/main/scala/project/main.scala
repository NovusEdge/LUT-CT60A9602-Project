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
	var fetchedData: Map[String, List[EnergyData]] = Map.empty[String, List[EnergyData]]

	@tailrec
    def mainloop(): Unit = {
		println(MAIN_MENU)
		println("Please select an option: ")
		val option = scala.io.StdIn.readLine()

		option match {
			case "1" => { 
				val fetchedData = CacheDataToFile(filePath);
				println(s"Data fetched and stored successfully in $filePath.")
				mainloop()
			}
			case "2" => { 
				val cachedData = GetCachedData(filePath)
				if (cachedData.isEmpty) {
					println("No data found in the cache.")
				} else {
					cachedData.foreach { case (sourceType, dataList) =>
						println(s"Source Type: $sourceType")
						dataList.foreach { data =>
							println(data)
						}
					}
				}
				mainloop()
			}
			case "3" => { 
				// Just display the statistics for all data present; use fetchedData
				val cachedData = GetCachedData(filePath)
				if (cachedData.isEmpty) {
					println("No data found in the cache.")
				} else {
					cachedData.foreach { case (sourceType, dataList) =>
						println(s"Source Type: $sourceType")
						dataList.foreach { data =>
							val stats = data.calculateStatistics()
							stats.foreach { case (key, values) =>
								println(s"$key: ${values.mkString(", ")}")
							}
						}
					}
				}
				mainloop()
			}
			case "4" => {
				println("Exiting the program.")
				return
			}
			case _ => {
				println(E_INVALID_OPTION)
				mainloop()
			}
		}
	}
	mainloop()
}