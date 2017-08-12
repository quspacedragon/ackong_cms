package com.ackong.cms.web;


import com.ackong.cms.entity.Admin;
import com.ackong.cms.service.AdminManager;
import com.ackong.cms.utils.JSONUtil;
import com.ackong.cms.utils.UserUtil;
import com.ackong.webframe.core.util.Struts2Utils;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


@Results({
        @Result(name = "index", location = "/WEB-INF/pages/login.jsp"),
        @Result(name = "main", location = "/WEB-INF/pages/index/index.jsp"),
        @Result(name = "content", location = "/WEB-INF/pages/index/content.jsp"),
        @Result(name = "left", location = "/WEB-INF/pages/index/left.jsp")
})
@Action("index")
public class IndexAction extends ActionSupport {
    private Logger log = LoggerFactory.getLogger(IndexAction.class);
    @Autowired
    private AdminManager adminManager;

    @Override
    public String execute() throws Exception {
        return index();
    }

    public String index() {
        return "index";
    }

    public String main() {
        return "main";
    }

    public String content() {
        return "content";
    }

    public String left() {
        return "left";
    }

    public String login() {
        String username = Struts2Utils.getParameter("username");
        String password = Struts2Utils.getParameter("password");
        Map<String, String> response = new HashMap<String, String>();
        try {
            Admin admin = adminManager.findByUsernameAndPassword(username, password);
            if (admin == null) {
                response.put("code", "201");
                response.put("message", "用户名或者密码错误");
            } else {
                Struts2Utils.getSession()
                        .setAttribute(UserUtil.SESSION_ADMIN, admin);
                response.put("code", "200");
                response.put("message", "ok");
            }
        } catch (Exception e) {
            response.put("code", "200");
            response.put("message", "用户名或者密码错误");
            e.printStackTrace();
        }
        Struts2Utils.renderJson(JSONUtil.json2String(response));
        return null;
    }

}
