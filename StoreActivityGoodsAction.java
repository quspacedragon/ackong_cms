package com.ackong.cms.web;

import com.ackong.cms.entity.*;
import com.ackong.cms.entity.StoreActivityGoods;
import com.ackong.cms.service.GoodsManager;
import com.ackong.cms.service.SaleManager;
import com.ackong.cms.service.StoreActivityGoodsManager;
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
 * StoreActivityGoods
 * storeActivityGoods
 *
 * @author QuSpaceDragon
 * @create 2016-12-15
 */
@Results({
        @Result(name = "reload", location = "storeActivityGoods.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/storeActivityGoods/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/storeActivityGoods/input.jsp")})
@Action("storeActivityGoods")
public class StoreActivityGoodsAction extends CoreActionSupport<StoreActivityGoods> {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(StoreActivityGoodsAction.class);
    @Autowired
    private StoreActivityGoodsManager storeActivityGoodsManager;
    @Autowired
    private GoodsManager goodsManager;
    @Autowired
    private StoreActivityManager storeActivityManager;
    private Page<StoreActivityGoods> page = new MyPage<StoreActivityGoods>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private StoreActivityGoods storeActivityGoods;
    private List<StoreActivity> storeActivityList;
    private List<Goods> goodsList;


    @Override
    public String execute() throws Exception {
        return list();
    }

    @Override
    protected void prepareModel() throws Exception {
        if (this.id != null)
            this.storeActivityGoods = this.storeActivityGoodsManager.get(this.id);
        else
            this.storeActivityGoods = new StoreActivityGoods();

    }

    public String delete() throws Exception {
        try {
            this.storeActivityGoodsManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.storeActivityGoods = this.storeActivityGoodsManager.get(this.id);
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
        this.page = this.storeActivityGoodsManager.getPage(this.page, filters);
        return "success";
    }

    public String recover() throws Exception {
        Response re = new Response();
        try {

            this.storeActivityGoodsManager.save(this.storeActivityGoods);
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

            this.storeActivityGoodsManager.save(this.storeActivityGoods);
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

    public Page<StoreActivityGoods> getPage() {
        return page;
    }

    public void setPage(Page<StoreActivityGoods> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StoreActivityGoods getStoreActivityGoods() {
        return storeActivityGoods;
    }

    public void setStoreActivityGoods(StoreActivityGoods storeActivityGoods) {
        this.storeActivityGoods = storeActivityGoods;
    }


    @Override
    public StoreActivityGoods getModel() {
        return this.storeActivityGoods;
    }

    public List<StoreActivity> getStoreActivityList() {
        storeActivityList = storeActivityManager.getAll();
        return storeActivityList;
    }

    public List<Goods> getGoodsList() {
        goodsList = goodsManager.getAll();
        return goodsList;
    }
}
