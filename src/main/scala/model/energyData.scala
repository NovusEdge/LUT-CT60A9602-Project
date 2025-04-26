package model

case class EnergyData(
    timestamp: String,
    sourceType: String,
    energyProduced: Double
)