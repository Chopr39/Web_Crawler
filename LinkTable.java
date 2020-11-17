package crawler;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class LinkTable {
    private HashMap<String,String> table;

    public LinkTable() {
        this.table = new LinkedHashMap<>();
    }


    public int parsedPages() {
        return this.table.size();
    }


    public String getLink(int numberOfRow) {
        return (String) this.table.keySet().toArray()[numberOfRow];
    }

    public String getTitle(int numberOfRow) {
        return this.table.get(getLink(numberOfRow));
    }

    public void put(String link, String tittle) {
        table.put(link, tittle);
    }

    public void put(HashMap<String,String> hashMap) {
        for (String key : hashMap.keySet()) {
            table.put(key,hashMap.get(key));
        }
    }

    public boolean contains(String key) {
        return table.containsKey(key);
    }
}
