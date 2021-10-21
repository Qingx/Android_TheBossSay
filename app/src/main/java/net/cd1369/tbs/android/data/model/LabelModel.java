package net.cd1369.tbs.android.data.model;

/**
 * Created by Xiang on 2021/8/25 15:11
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public class LabelModel {
    private String id;
    private String name;

    public LabelModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public LabelModel() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static LabelModel empty = new LabelModel("-1", "全部");
}
