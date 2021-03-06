package cn.leancloud.demo.todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.leancloud.AVException;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import cn.leancloud.utils.AVUtils;

import cn.leancloud.EngineFunction;
import cn.leancloud.EngineFunctionParam;
import cn.leancloud.EngineHook;
import cn.leancloud.EngineHookType;
import cn.leancloud.EngineRequestContext;
import cn.leancloud.utils.StringUtil;

public class Cloud {

  @EngineFunction("foo")
  public static String foo() {
    return "bar";
  }

  @EngineFunction("hello")
  public static Map<String, Object> hello(@EngineFunctionParam("name") String name) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("action", "hello");
    result.put("name", name);
    return result;
  }

  @EngineFunction("remoteAddress")
  public static String remoteAddress() {
    return EngineRequestContext.getRemoteAddress();
  }

  @EngineHook(className = "TestReview", type = EngineHookType.beforeSave)
  public static AVObject beforeSave(AVObject obj) throws Exception {
    System.out.println("before save TestReview: " + obj);
    if (obj.getInt("stars") < 1) {
      throw new Exception("you cannot give less than one stars");
    } else if (obj.getInt("stars") > 5) {
      throw new Exception("you cannot give more than five stars");
    }
    String comment = obj.getString("comment");
    if (comment.length() > 140) {
      obj.put("comment", comment.substring(0, 137) + "...");
    }
    return obj;
  }

  @EngineHook(className = "TestReview", type = EngineHookType.beforeUpdate)
  public static AVObject testBeforeUpdate(AVObject obj) throws Exception {
    System.out.println("before update TestReview: " + obj);
    List<String> updateKeys = EngineRequestContext.getUpdateKeys();
    System.out.println("updateKeys: " + updateKeys);
    if (updateKeys == null || updateKeys.isEmpty()) {
      throw new AVException(400, "nothing to update");
    } else {
      String comment = obj.getString("comment");
      if (comment.length() > 140) {
        throw new Exception("you cannot give more than 140 letters for comment.");
      }
      return obj;
    }
  }

  @EngineHook(className = "TestReview", type = EngineHookType.afterUpdate)
  public static void testAfterUpdate(AVObject obj) throws Exception {
    System.out.println("after update TestReview: " + obj);
    if (obj == null) {
      throw new AVException(400, "empty object");
    }
    if (StringUtil.isEmpty(obj.getObjectId())) {
      throw new AVException(400, "object not saved");
    } else if (!"TestReview".equals(obj.getClassName())) {
      throw new AVException(400, "className not match");
    } else if (obj.getInt("star") > 5 && obj.getInt("star") < 1) {
      throw new AVException(400, "invalid star value");
    }
  }

  @EngineHook(className = "TestReview", type = EngineHookType.beforeDelete)
  public static void testBeforeDelete(AVObject obj) throws Exception {
    System.out.println("before delete TestReview: " + obj);
    if ("1234567890".equals(obj.getObjectId())) {
      throw new AVException(400, "Object is being protected");
    } else {
      return;
    }
  }

  @EngineHook(className = "TestReview", type = EngineHookType.afterDelete)
  public static void testAfterDelete(AVObject obj) throws Exception {
    System.out.println("after delete TestReview: " + obj);
    if ("1234567890".equals(obj.getObjectId())) {
      throw new AVException(400, "Object is being protected");
    } else {
      return;
    }
  }

  @EngineFunction("getTodos")
  public static List<Todo> getTodos(@EngineFunctionParam("offset") int offset) throws AVException {
    AVQuery<Todo> query = AVObject.getQuery(Todo.class);
    query.orderByDescending("createdAt");
    query.include("createdAt");
    query.skip(offset);
    return query.find();
  }

  @EngineHook(className = "Todo", type = EngineHookType.beforeSave)
  public static Todo beforeSaveTodo(Todo todo) {
    System.out.println("before save Todo: " + todo);
    return todo;
  }

  @EngineHook(className = "Todo", type = EngineHookType.afterSave)
  public static void afterSaveTodo(Todo todo) {
    System.out.println("after save Todo: " + todo);
  }

  @EngineHook(className = "Todo", type = EngineHookType.beforeUpdate)
  public static Todo beforeUpdateTodo(Todo todo) {
    System.out.println("before update Todo: " + todo);
    return todo;
  }

  @EngineHook(className = "Todo", type = EngineHookType.afterUpdate)
  public static void afterUpdateTodo(Todo todo) {
    System.out.println("after update Todo: " + todo);
  }

  @EngineHook(className = "_User", type = EngineHookType.beforeSave)
  public static AVObject userBeforeSaveHook(AVObject user) throws Exception {
    System.out.println("userBeforeSaveHook");
    user.add("beforeSave", true);
    return user;
  }

  @EngineHook(className = "_User", type = EngineHookType.onLogin)
  public static void testOnLogin(AVUser user) throws Exception {
    if ("spamUser".equals(user.getUsername())) {
      throw new AVException(400, "forbidden");
    }
  }

  @EngineHook(className = "_User", type = EngineHookType.onVerifiedEmail)
  public static void testSMSVerified(AVUser user) throws Exception {
    if ("onVerifiedEmailUser".equals(user.getUsername())) {
      throw new AVException(400, "wrong user");
    }
  }

  @EngineHook(className = "_User", type = EngineHookType.beforeUpdate)
  public static void userBeforeUpdateHook(AVUser user) throws AVException {
    System.out.println("before update user: " + user);
    List<String> updateKeys = EngineRequestContext.getUpdateKeys();
    System.out.println("updateKeys: " + updateKeys);
    if ("beforeUpdateUser".equals(user.getUsername())) {
      throw new AVException(400, "wrong user");
    }
    return;
  }

  @EngineHook(className = "_User", type = EngineHookType.afterUpdate)
  public static void userAfterUpdateHook(AVUser user) {
    System.out.println("after update user: " + user);
  }

}
