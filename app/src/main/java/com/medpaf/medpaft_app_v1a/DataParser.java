package com.medpaf.medpaft_app_v1a;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataParser {

    public List<Item> dataToItems(String data, String regex) {

        List<Item> items = new ArrayList<Item>();
        Map<String, String> map = new HashMap<>();
        Matcher m = Pattern.compile(regex)
                .matcher(data);
        while (m.find()) {
            String value = m.group();
            map.put(value.substring(0, 1), value.substring(1, value.length()));
        }

        map.entrySet().forEach(position -> {
            Item item = new Item();
            item.setId(position.getKey());
            item.setValue(Integer.valueOf(position.getValue()));
            items.add(item);
        });

        return items;
    }

    public void saveToDatabase(List<Item> itemsToSave) {
       //itemsToSave.forEach(item -> Log.d("XI", String.valueOf(item)));

    }


}
