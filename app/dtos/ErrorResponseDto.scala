package dtos

import play.api.data.validation.ValidationError
import play.api.libs.json.{JsPath, JsValue, Json, JsonValidationError}

case class ErrorResponseDto(code: String, errors: Map[String, String])

object ErrorResponseDto {
  implicit val writableJsonErrors = Json.writes[ErrorResponseDto]

  /**
    * Formats ValidationError case classes in addition to converting default missing messages to "not provided"
    * @param errors are the sequence of validation errors
    * @return error message
    */
  def fmtValidationErrors(errors: Seq[JsonValidationError]): String =
    errors
      .map(_.message)
      .map {
        case "error.path.missing" => "not provided"
        case normal => normal
      }
      .mkString(", ")

  /**
    * Formats Sequence of tuple2s of JSON Path information and corresponding Sequence of Validation Errors
    * @param errors the aforementioned sequence of tuple2s
    * @return a map of key value pairs representing all the fields that failed validation and their corresponding
    *         validation errors
    */
  def fmtValidationResults(errors: Seq[(JsPath, Seq[JsonValidationError])]): Map[String, String] =
    errors.foldLeft(Map.empty[String, String]) {
      // apply destructuring and then pattern match
      case (resultMap: Map[String, String], (jsPath: JsPath, validationErrors: Seq[JsonValidationError])) =>
        val fieldWithError = jsPath.toString
        val errorData = fmtValidationErrors(validationErrors)
        resultMap + (fieldWithError -> errorData)
    }

  implicit class ErrorResponseJsonOps(errorResponse: ErrorResponseDto) {
    def toJson: JsValue = Json.toJson(errorResponse)
  }
}