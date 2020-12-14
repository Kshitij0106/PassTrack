package com.safe.passtrack.Model;

public class Text_Details {

    String name,text,type;

    public Text_Details() {
    }

    public Text_Details(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public Text_Details(String name, String text, String type) {
        this.name = name;
        this.text = text;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
