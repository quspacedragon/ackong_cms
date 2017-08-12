package com.ackong.cms.web;

import com.ackong.cms.entity.Activity;
import com.ackong.cms.entity.ActivityGoods;
import com.ackong.cms.entity.GoodsCategory;
import com.ackong.cms.service.ActivityGoodsManager;
import com.ackong.cms.service.ActivityManager;
import com.ackong.cms.service.GoodsCategoryManager;
import com.ackong.cms.service.GoodsManager;
import com.ackong.cms.utils.JSONUtil;
import com.ackong.common.Response;
import com.ackong.webframe.core.orm.MyPage;
import com.ackong.webframe.core.orm.Page;
import com.ackong.webframe.core.orm.PropertyFilter;
import com.ackong.webframe.core.util.Cue;
import com.ackong.webframe.core.util.Struts2Utils;
import com.ackong.webframe.core.web.CoreActionSupport;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.http.HttpStatus;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * activity
 *
 * @author QuSpaceDragon
 * @create 2016-06-14
 */
@Results({
        @Result(name = "reload", location = "activity.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/activity/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/activity/input.jsp")})
public class ActivityAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(ActivityAction.class);
    @Autowired
    private ActivityManager activityManager;
    @Autowired
    private GoodsCategoryManager goodsCategoryManager;
    @Autowired
    private GoodsManager goodsManager;
    @Autowired
    private ActivityGoodsManager activityGoodsManager;
    private Page<Activity> page = new MyPage<Activity>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private Activity activity;


    @Override
    public String execute() throws Exception {
        return list();
    }

    public String delete() throws Exception {
        try {
            this.activityManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.activity = this.activityManager.getActivity(this.id);
        }
        return "input";
    }

    public String list() throws Exception {
        List filters = PropertyFilter.buildPropertyFilters(Struts2Utils
                .getRequest());
        if (!this.page.isOrderBySetted()) {
            this.page.setOrderBy("id");
            this.page.setOrder("desc");
        }
        this.page = this.activityManager.getPage(this.page, filters);
        return "success";
    }


    public String save() throws Exception {
        Response re = new Response();
        try {
            this.activityManager.save(this.activity);
            re.setMessage("保存成功");
            re.setCode(HttpStatus.SC_OK);
        } catch (Exception e) {
            this.log.error("保存失败:", e);
            re.setMessage("保存失败");
            re.setCode(HttpStatus.SC_BAD_REQUEST);
        }
        Struts2Utils.renderJson(JSONUtil.json2String(re));
        return null;
    }

    public String getActivityGoods() throws Exception {
        List<ActivityGoods> list = activityGoodsManager.getAll();
        Struts2Utils.renderJson(JSONUtil.json2String(list));
        return null;
    }


    public Page<Activity> getPage() {
        return page;
    }

    public void setPage(Page<Activity> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
