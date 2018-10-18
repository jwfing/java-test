package cn.leancloud.demo.todo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.leancloud.AVCloud;
import cn.leancloud.AVException;
import cn.leancloud.AVObject;
import cn.leancloud.utils.StringUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

@WebServlet(name = "AppServlet", urlPatterns = {"/todos"})
public class TodoServlet extends HttpServlet {

  private static final long serialVersionUID = -225836733891271748L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String offsetParam = req.getParameter("offset");
    int offset = 0;
    if (!StringUtil.isEmpty(offsetParam)) {
      offset = Integer.parseInt(offsetParam);
    }
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("offset", offset);
    Observable<List<Todo>> result = AVCloud.callFunctionInBackground("getTodos", params);
    List<Todo> todos = result.blockingFirst();
    req.setAttribute("todos", todos);
    req.getRequestDispatcher("/todos.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String content = req.getParameter("content");

    AVObject note = new Todo();
    note.put("content", content);
    note.save();

    resp.sendRedirect("/todos");
  }
}
