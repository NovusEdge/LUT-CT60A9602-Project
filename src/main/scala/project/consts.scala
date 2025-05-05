package project

/**
 * Contains constants used throughout the application.
 * 
 * This object defines various constants including API configuration, dataset IDs,
 * production type mappings, and user interface text.
 */
object consts {
    /** The base URL for the Fingrid API */
    val API_URL = "https://data.fingrid.fi/api/datasets/"
    
    /** API key for accessing the Fingrid API */
    val API_KEY = "3653879acc754a97b69d05bdb857f9d9"

    // Dataset IDs
    /** ID for surplus energy production dataset */
    val SURPLUS_EPROD_ID                = 362 // https://data.fingrid.fi/en/datasets/362
    
    /** ID for energy consumption forecast dataset */
    val ECONSUMPTION_FORECAST_ID        = 165 // https://data.fingrid.fi/en/datasets/165
    
    /** ID for wind generation forecast dataset */
    val WIND_GENERATION_FORECAST_ID     = 246 // https://data.fingrid.fi/en/datasets/246
    
    /** ID for solar generation forecast dataset */
    val SOLAR_GENERATION_FORECAST_ID    = 247 // https://data.fingrid.fi/en/datasets/247
    
    /** ID for solar production capacity dataset */
    val SOLAR_PRODUCTION_CAPACITY_ID    = 267 // https://data.fingrid.fi/en/datasets/267
    
    /** ID for wind production capacity dataset */
    val WIND_PRODUCTION_CAPACITY_ID     = 268 // https://data.fingrid.fi/en/datasets/268

    /** 
     * Mapping of production type codes to human-readable names.
     * 
     * These codes are used in the Fingrid API response to identify different
     * types of energy production.
     */
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

    /** Text for the main menu displayed to the user */
    val MAIN_MENU = 
        """
        |1. Fetch and store data
        |2. Show stored data
        |3. Display Statistics
        |0. Exit""".stripMargin

    /** Error message for invalid user input */
    val E_INVALID_OPTION = "Invalid option. Please try again."
}