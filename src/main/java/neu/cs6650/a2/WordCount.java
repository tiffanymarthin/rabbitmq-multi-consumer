package neu.cs6650.a2;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;

/**
 * Util class to update word count hash map
 */
public class WordCount {

  /**
   * Updates the word count in the specified map given a new message
   *
   * @param message specified message to be processed and added to the map
   * @param map     specified map to be updated
   */
  public static void updateMap(String message, Map<String, Integer> map) {
    Gson gson = new Gson();
    JsonElement jsonElement = gson.fromJson(message, JsonElement.class);
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
      String key = entry.getKey();
      JsonElement value = entry.getValue();
      map.put(key, map.getOrDefault(key, 0) + value.getAsInt());
    }
  }

//  public static void main(String[] args) {
//    String json = "{ \"name\": \"1\", \"java\": \"33\"}";
//    String json2 = "{ \"namE\": \"1\", \"java\": \"22\"}";
//    Map<String, Integer> map = new HashMap<>();
//    ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
//    updateMap(json, concurrentHashMap);
//    updateMap(json2, concurrentHashMap);
//    for (Map.Entry<String, Integer> entry : concurrentHashMap.entrySet()) {
//      System.out.println(entry.getKey() + " val: " + entry.getValue());
//    }
//  }

}
