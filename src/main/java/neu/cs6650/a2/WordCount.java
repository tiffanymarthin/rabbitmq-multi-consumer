package neu.cs6650.a2;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WordCount {

  public static int updateMap(String message, Map<String, Integer> map) {
    Gson gson = new Gson();
//    String jsonObject = gson.toJson(message);

    JsonElement jelem = gson.fromJson(message, JsonElement.class);
    JsonObject jsonObject = jelem.getAsJsonObject();
    int initialSize = map.size();
    int afterSize = map.size();
    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
      String key = entry.getKey();
//      System.out.println(key);
      JsonElement value = entry.getValue();
      if (!map.containsKey(key)) afterSize++;
      map.put(key, map.getOrDefault(key, 0) + value.getAsInt());
    }
    return afterSize;
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
