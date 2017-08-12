package com.ackong.cms.web;

import com.ackong.cms.entity.ActivityGoods;
import com.ackong.cms.entity.ActivitySku;
import com.ackong.cms.entity.Sku;
import com.ackong.cms.service.ActivityGoodsManager;
import com.ackong.cms.service.ActivitySkuManager;
import com.ackong.cms.service.SkuManager;
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

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * activitySku
 *
 * @author QuSpaceDragon
 * @create 2016-07-17
 */
@Results({
                 @Result(name = "reload", location = "activitySku.action", type = "redirect"),
                 @Result(name = "success", location = "/WEB-INF/pages/activitySku/list.jsp"),
                 @Result(name = "input", location = "/WEB-INF/pages/activitySku/input.jsp")})
@Action("activitySku")
public class ActivitySkuAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(ActivitySkuAction.class);
    @Autowired
    private ActivitySkuManager activitySkuManager;
    @Autowired
    private SkuManager skuManager;
    @Autowired
    private ActivityGoodsManager activityGoodsManager;
    private Page<ActivitySku> page = new MyPage<ActivitySku>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private ActivitySku activitySku;
    private List<Sku> skuList;
    private List<ActivityGoods> activityGoodsList;

    @Override
    public String execute() throws Exception {
        return list();
    }

    public String delete() throws Exception {
        try {
            this.activitySkuManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.activitySku = this.activitySkuManager.getActivitySku(this.id);
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
        this.page = this.activitySkuManager.getPage(this.page, filters);
        return "success";
    }


    public String save() throws Exception {
        Response re = new Response();
        try {

            this.activitySkuManager.save(this.activitySku);
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

    public Page<ActivitySku> getPage() {
        return page;
    }

    public void setPage(Page<ActivitySku> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ActivitySku getActivitySku() {
        return activitySku;
    }

    public void setActivitySku(ActivitySku activitySku) {
        this.activitySku = activitySku;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_goods_id", insertable = false, updatable = false)
    public List<ActivityGoods> getActivityGoodsList() {
        activityGoodsList = activityGoodsManager.getAll();
        return activityGoodsList;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", insertable = false, updatable = false)
    public List<Sku> getSkuList() {
        if (activitySku == null) {
            skuList = skuManager.getSkusAll();
        } else {
            skuList = skuManager.getSkusByGoodsId(activitySku.getActivityGoods().getGoodsId());
        }
        return skuList;
    }
}
