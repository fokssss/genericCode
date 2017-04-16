package xyy.tools.generic;

import java.util.HashMap;

/**
 * Created by xyy on 2017/4/12.
 */
public class EntityInfo {

    private HashMap<String, String> mDefaultValue = new HashMap<>();

    public EntityInfo(String fieldname, String defaultValue) {
        mDefaultValue.put(fieldname, defaultValue);
    }

    public EntityInfo(String field1, String default1, String field2, String default2) {
        mDefaultValue.put(field1, default1);
        mDefaultValue.put(field2, default2);
    }

    public HashMap<String, String> getDefaultValueCollection() {
        return mDefaultValue;
    }

    public void setDefaultValueCollection(HashMap<String, String> defaultValueCollection) {
        this.mDefaultValue = defaultValueCollection;
    }

    public String getDefultValue(String fieldname) {
        return mDefaultValue.get(fieldname);
    }
}
