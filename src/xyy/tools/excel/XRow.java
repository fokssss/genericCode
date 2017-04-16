package xyy.tools.excel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xyy on 2017/4/16.
 */
public class XRow {
    private List<String> data = new ArrayList<>();

    public JSONArray toJson() {
        JSONArray rs = new JSONArray();
        for (int i = 0; i < data.size(); i++) {
            rs.put(data.get(i));
        }
        return rs;
    }

    public void add(String s) {
        data.add(s);
    }
}
