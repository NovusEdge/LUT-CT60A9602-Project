package project

object consts {
    val API_URL = "https://data.fingrid.fi/api/datasets/"
    val TIMEOUT = 5000 // in milliseconds
    val API_KEY = "3653879acc754a97b69d05bdb857f9d9"

    // Dataset IDs
    val SURPLUS_EPROD_ID                = 362 // https://data.fingrid.fi/en/datasets/362
    val ECONSUMPTION_FORECAST_ID        = 165 // https://data.fingrid.fi/en/datasets/165
    val WIND_GENERATION_FORECAST_ID     = 246 // https://data.fingrid.fi/en/datasets/246
    val SOLAR_GENERATION_FORECAST_ID    = 247 // https://data.fingrid.fi/en/datasets/247
    val SOLAR_PRODUCTION_CAPACITY_ID    = 267 // https://data.fingrid.fi/en/datasets/267
    val WIND_PRODUCTION_CAPACITY_ID     = 268 // https://data.fingrid.fi/en/datasets/268

    val PROD_TYPE_MAP = Map(
        "AV01" -> "Hydropower",
        "AV02" -> "Wind power",
        "AV03" -> "Nuclear power",
        "AV04" -> "Gas turbine",
        "AV05" -> "Diesel engine",
        "AV06" -> "Solar power",
        "AV07" -> "Wave power",
        "AV08" -> "Combined production",
        "AV09" -> "Biopower",
        "AV10" -> "Other production"
    )

    val MAIN_MENU = 
        """
          |1. View API datasets
          |2. Fetch Data
          |3. Show data statistics
          |4. Store data to file
          |5. Read data from file
          |6. Show stored data
          |0. Exit
          |""".stripMargin

    val DATASET_MENU =
        """
          |1. Surplus Energy Production
          |2. Consumption Forecast
          |3. Wind Generation Forecast
          |4. Solar Generation Forecast
          |5. Solar Production Capacity
          |6. Wind Production Capacity
          |""".stripMargin

    val E_INVALID_OPTION = "Invalid option. Please try again."
}