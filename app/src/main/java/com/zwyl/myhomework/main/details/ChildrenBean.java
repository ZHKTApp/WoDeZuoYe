package com.zwyl.myhomework.main.details;

import java.util.List;

public class ChildrenBean {
    private String textBookChapterName; //:; //第一节",
    private int textBookChapterId; //:; //40f6fa5fbae049b1bf1ecb8b4018258a",
    private int textBookChapterParentId; //:; //9c4c6e3959c745029455d728266af382"
    private List<ChildrenBean> childrenList;

    public List<ChildrenBean> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<ChildrenBean> childrenList) {
        this.childrenList = childrenList;
    }

    public String getTextBookChapterName() {
        return textBookChapterName;
    }

    public void setTextBookChapterName(String textBookChapterName) {
        this.textBookChapterName = textBookChapterName;
    }


    public int getTextBookChapterId() {
        return textBookChapterId;
    }

    public void setTextBookChapterId(int textBookChapterId) {
        this.textBookChapterId = textBookChapterId;
    }

    public int getTextBookChapterParentId() {
        return textBookChapterParentId;
    }

    public void setTextBookChapterParentId(int textBookChapterParentId) {
        this.textBookChapterParentId = textBookChapterParentId;
    }

    @Override
    public String toString() {
        return "ChildrenBean{" +
                "textBookChapterName='" + textBookChapterName + '\'' +
                ", textBookChapterId='" + textBookChapterId + '\'' +
                ", textBookChapterParentId='" + textBookChapterParentId + '\'' +
                '}';
    }
}
