package Node

import spray.json.{JsonFormat, _}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

sealed trait JsonProtocal

package object api extends DefaultJsonProtocol with SprayJsonSupport{

	final case class UserApi(api: String, param: String) extends JsonProtocal

	implicit val userApiJsonFormat = jsonFormat2(UserApi)

	implicit object JsonProtocalFormat extends JsonFormat[JsonProtocal] {
		def write(json: JsonProtocal) = json match{
			case user: UserApi => user.toJson
		}

		def read(value: JsValue) = {
			val api = value.asJsObject.fields("api").asInstanceOf[JsString].value
			api match{
				case "UserApi" => value.convertTo[UserApi]
				case _         => deserializationError("Not a defined api!")
			}
		}
	}

}
