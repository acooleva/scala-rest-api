package models

import java.util.UUID

import enums.Fuel.Fuel

case class CarAdvert(
  val id: UUID,
  val title: String,
  val fuel: Fuel,
  val price: Int,
  val isNew: Boolean,
  val mileAge: Int,
  val firstRegistration: String
)

