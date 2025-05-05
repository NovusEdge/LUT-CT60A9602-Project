package project

import api_client.FingridApiClient._

import model.SourceType._
import model._
import project.consts._
import project.ProjectFileIO._

import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal
import scala.annotation.tailrec

/**
 * Main application entry point for the energy data analysis program.
 * 
 * This application fetches, stores, and analyzes energy data from the Fingrid API,
 * providing functionality to view raw data and calculate statistics.
 */
object Main extends App {
    val filePath = "energy_data.json"
	var fetchedData: Map[String, List[EnergyData]] = Map.empty[String, List[EnergyData]]

	/**
	 * Main application loop that displays the menu and handles user input.
	 * 
	 * This is implemented as a tail-recursive function to avoid stack overflow
	 * while maintaining a functional programming approach.
	 */
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
						dataList.foreach { data => {
								val formattedTime = formatTimestamp(data.getTimestamp)
								println(s"\tDate: ${formattedTime._1}")
								println(s"\tTime: ${formattedTime._2}")
								println(s"\tUnit: ${data.getUnit}")
								data.getEnergyData.foreach { case (key, values) =>
									println(s"\t$key: ${values.mkString(", ")}")
								}
								println()
							}
						}
					}
				}
				mainloop()
			}
			case "3" => { 
				val cachedData = GetCachedData(filePath)
				if (cachedData.isEmpty) {
					println("No data found in the cache.")
				} else {
					cachedData.foreach { case (sourceType, dataList) =>
						println(s"Source Type: $sourceType")
						dataList.foreach { data =>
							val stats = data.calculateStatistics()
							stats.foreach { case (key, values) =>
								println(s"$key: ${values.mkString("\n\t")}")
							}
						}

						println()
					}
				}
				mainloop()
			}
			case "0" => {
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