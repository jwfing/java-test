package cn.leancloud.demo.todo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import cn.leancloud.AVCloud;
import cn.leancloud.AVLogger;
import cn.leancloud.core.AVOSCloud;
import cn.leancloud.AVObject;

import cn.leancloud.LeanEngine;

@WebListener
public class AppInitListener implements ServletContextListener {

  private String appId = System.getenv("LEANCLOUD_APP_ID");
  private String appKey = System.getenv("LEANCLOUD_APP_KEY");
  private String appMasterKey = System.getenv("LEANCLOUD_APP_MASTER_KEY");

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {}

  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    // 注册子类化
    AVObject.registerSubclass(Todo.class);
    // 初始化AVOSCloud，请保证在整个项目中间只初始化一次
    LeanEngine.initialize(appId, appKey, appMasterKey);
    // 在请求签名中使用masterKey以激活云代码的最高权限
    LeanEngine.setUseMasterKey(true);
    // 打开 debug 日志
    AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);
    // 向云引擎注册云函数
    LeanEngine.register(Cloud.class);
    if (System.getenv("LEANCLOUD_APP_ENV").equals("development")) {
      // 如果是开发环境，则设置 AVCloud.callFunction 和 AVCloud.rpcFunction 调用本地云函数实现
      // 如果需要本地开发时调用云端云函数实现，则注释掉下面语句。
      LeanEngine.setLocalEngineCallEnabled(true);
    } else if ("stage".equals(System.getenv("LEANCLOUD_APP_ENV"))) {
      AVCloud.setProductionMode(false);
    }
  }
}
