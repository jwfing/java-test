package cn.leancloud.demo.todo;

import cn.leancloud.annotation.AVClassName;
import cn.leancloud.AVObject;

@AVClassName("Todo")
public class Todo extends AVObject {

  public String getContent() {
    return getString("content");
  }

}
