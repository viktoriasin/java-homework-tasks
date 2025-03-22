package ru.sinvic.homework.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectForMessage implements Copyable<ObjectForMessage> {
    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ObjectForMessage{" + "data=" + data + '}';
    }

    @Override
    public ObjectForMessage copy() {
        List<String> newData = new ArrayList<>(data);
        ObjectForMessage copyObject = new ObjectForMessage();
        copyObject.setData(newData);
        return copyObject;
    }
}
