package enums

import dtos.EnumerationHelpers
import play.api.libs.json.{Reads, Writes}
import enums.{Fuel => FuelModel}

/**
  * Fuel types.
  */
object Fuel extends Enumeration {
  type Fuel = Value
  val Diesel, Gasoline, Biodisel = Value

  implicit val enumReads: Reads[Fuel] = EnumerationHelpers.enumReads(Fuel)
  implicit val enumWrites: Writes[Fuel] = EnumerationHelpers.enumWrites

  implicit class FuelDtoToModel(fuel: Fuel) {
    def toModel: FuelModel.Fuel = fuel match {
      case Fuel.Diesel => FuelModel.Diesel
      case Fuel.Gasoline => FuelModel.Gasoline
      case Fuel.Biodisel => FuelModel.Biodisel
    }
  }

  implicit class FuelModelToDto(fuel: FuelModel.Fuel) {
    def toDto: Fuel.Fuel = fuel match {
      case FuelModel.Diesel => Fuel.Diesel
      case FuelModel.Gasoline => Fuel.Gasoline
      case FuelModel.Biodisel => Fuel.Biodisel
    }
  }
}
