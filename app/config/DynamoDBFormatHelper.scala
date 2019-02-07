package config

import java.util.UUID

import com.gu.scanamo._
import enums.Fuel
import enums.Fuel.Fuel

/**
  * Scanamo format helpers that help with serialization and deserialization of custom formats like
  * Fuel Enumerations and UUIDs
  */
object DynamoDBFormatHelpers {
  implicit def enumerationStringFormat: DynamoFormat[Fuel] =
    DynamoFormat.coercedXmap[Fuel, String, IllegalArgumentException] {
    fuelAsString => Fuel.withName(fuelAsString)
  } {
    fuel => fuel.toString
  }

  implicit def uuidStringFormat: DynamoFormat[UUID] = DynamoFormat.coercedXmap[UUID, String, IllegalArgumentException] {
    uuidString => UUID.fromString(uuidString)
  } {
    uuid => uuid.toString
  }
}
