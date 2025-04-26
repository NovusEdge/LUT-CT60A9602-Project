package api_client

import java.io.{BufferedReader, InputStreamReader}
import java.net.{HttpURLConnection, URL}
import java.nio.charset.StandardCharsets
import java.util.Base64

import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal
import project.consts.{ API_KEY, API_URL, TIMEOUT }


object FingridApiClient {
    def fetchEnergyData(sourceType: String): Either[String, String] = {
        // HTTP GET Request -> Return raw JSON as String or error as Left
    }
}