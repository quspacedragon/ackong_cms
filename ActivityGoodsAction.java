package com.ackong.cms.web;

import com.ackong.cms.entity.Activity;
import com.ackong.cms.entity.ActivityGoods;
import com.ackong.cms.entity.Goods;
import com.ackong.cms.service.ActivityGoodsManager;
import com.ackong.cms.service.ActivityManager;
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
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * activityGoods
 *
 * @author QuSpaceDragon
 * @create 2016-07-17
 */
@Results({
        @Result(name = "reload", location = "activityGoods.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/activityGoods/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/activityGoods/input.jsp")})
@Action("activityGoods")
public class ActivityGoodsAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(ActivityGoodsAction.class);
    @Autowired
    private ActivityGoodsManager activityGoodsManager;
    @Autowired
    private GoodsManager goodsManager;
    @Autowired
    private ActivityManager activityManager;
    private Page<ActivityGoods> page = new MyPage<ActivityGoods>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private ActivityGoods activityGoods;

    private List<Activity> activityList;
    private List<Goods> goodsList;

    @Override
    public String execute() throws Exception {
        return list();
    }

    public String delete() throws Exception {
        try {
            this.activityGoodsManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.activityGoods = this.activityGoodsManager.getActivityGoods(this.id);
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
        this.page = this.activityGoodsManager.getPage(this.page, filters);
        return "success";
    }

    public String save() throws Exception {
        Response re = new Response();
        try {
            if (activityGoods.getActivityGoodsId() == null) {
                activityGoods.setCreated(new Date());
            }
            this.activityGoodsManager.save(this.activityGoods);
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

    public Page<ActivityGoods> getPage() {
        return page;
    }

    public void setPage(Page<ActivityGoods> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ActivityGoods getActivityGoods() {
        return activityGoods;
    }

    public void setActivityGoods(ActivityGoods activityGoods) {
        this.activityGoods = activityGoods;
    }

    public List<Activity> getActivityList() {
        activityList = activityManager.getAll();
        return activityList;
    }

    public List<Goods> getGoodsList() {
        goodsList = goodsManager.getAll();
        return goodsList;
    }
}
