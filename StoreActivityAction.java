package com.ackong.cms.web;

import com.ackong.cms.entity.Sale;
import com.ackong.cms.entity.StoreActivity;
import com.ackong.cms.service.SaleManager;
import com.ackong.cms.service.StoreActivityManager;
import com.ackong.cms.utils.JSONUtil;
import com.ackong.common.Response;
import com.ackong.webframe.core.orm.MyPage;
import com.ackong.webframe.core.orm.Page;
import com.ackong.webframe.core.orm.PropertyFilter;
import com.ackong.webframe.core.util.Cue;
import com.ackong.webframe.core.util.Struts2Utils;
import com.ackong.webframe.core.web.CoreActionSupport;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * StoreActivity
 * storeActivity
 *
 * @author QuSpaceDragon
 * @create 2016-12-15
 */
@Results({
        @Result(name = "reload", location = "storeActivity.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/storeActivity/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/storeActivity/input.jsp")})
@Action("storeActivity")
public class StoreActivityAction extends CoreActionSupport<StoreActivity> {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(StoreActivityAction.class);
    @Autowired
    private StoreActivityManager storeActivityManager;
    @Autowired
    private SaleManager saleManager;
    private Page<StoreActivity> page = new MyPage<StoreActivity>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private StoreActivity storeActivity;
    private List<Sale> saleList;


    @Override
    public String execute() throws Exception {
        return list();
    }

    @Override
    protected void prepareModel() throws Exception {
        if (this.id != null)
            this.storeActivity = this.storeActivityManager.get(this.id);
        else
            this.storeActivity = new StoreActivity();

    }

    public String delete() throws Exception {
        try {
            this.storeActivityManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.storeActivity = this.storeActivityManager.get(this.id);
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
        this.page = this.storeActivityManager.getPage(this.page, filters);
        return "success";
    }

    public String recover() throws Exception {
        Response re = new Response();
        try {

            this.storeActivityManager.save(this.storeActivity);
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


    public String save() throws Exception {
        Response re = new Response();
        try {

            this.storeActivityManager.save(this.storeActivity);
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

    public Page<StoreActivity> getPage() {
        return page;
    }

    public void setPage(Page<StoreActivity> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StoreActivity getStoreActivity() {
        return storeActivity;
    }

    public void setStoreActivity(StoreActivity storeActivity) {
        this.storeActivity = storeActivity;
    }


    @Override
    public StoreActivity getModel() {
        return this.storeActivity;
    }
}
