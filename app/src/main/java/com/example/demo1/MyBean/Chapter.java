package com.example.demo1.MyBean;

public class Chapter {
    private int id;
    private int novel_id;
    private String content;
    private String name;
    private int start_id;
    private int end_id;

    public int getStart_id() {
        return start_id;
    }

    public void setStart_id(int start_id) {
        this.start_id = start_id;
    }

    public int getEnd_id() {
        return end_id;
    }

    public void setEnd_id(int end_id) {
        this.end_id = end_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNovel_id() {
        return novel_id;
    }

    public void setNovel_id(int novel_id) {
        this.novel_id = novel_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
