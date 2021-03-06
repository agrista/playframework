package play.libs

import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import com.fasterxml.jackson.databind.ObjectMapper

class JavaJsonSpec extends Specification {
  sequential

  private class JsonScope(val mapper: ObjectMapper = new ObjectMapper()) extends Scope {
    val testJsonString =
      """{
        |  "foo" : "bar",
        |  "bar" : "baz",
        |  "a" : 2.5,
        |  "copyright" : "\u00a9",
        |  "baz" : [ 1, 2, 3 ]
        |}""".stripMargin

    val testJson = mapper.createObjectNode()
    testJson
      .put("foo", "bar")
      .put("bar", "baz")
      .put("a", 2.5)
      .put("copyright", "\u00a9") // copyright symbol
      .put("baz", mapper.createArrayNode().add(1).add(2).add(3))

    Json.setObjectMapper(mapper)
  }

  "Json" should {
    "use the correct object mapper" in new JsonScope {
      Json.mapper() must_== mapper
    }
    "parse" in {
      "from string" in new JsonScope {
        Json.parse(testJsonString) must_== testJson
      }
      "from UTF-8 byte array" in new JsonScope {
        Json.parse(testJsonString.getBytes("UTF-8")) must_== testJson
      }
    }
    "stringify" in {
      "stringify" in new JsonScope {
        Json.stringify(testJson) must_== Json.stringify(Json.parse(testJsonString))
      }
      "asciiStringify" in new JsonScope {
        val resultString = Json.stringify(Json.parse(testJsonString)).replace("\u00a9", "\\u00A9")
        Json.asciiStringify(testJson) must_== resultString
      }
      "prettyPrint" in new JsonScope {
        Json.prettyPrint(testJson) must_== testJsonString
      }
    }
  }
}
