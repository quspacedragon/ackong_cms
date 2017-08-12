package com.ackong.cms.web;

import com.ackong.cms.entity.GoodsCategory;
import com.ackong.cms.service.GoodsCategoryManager;
import com.ackong.cms.utils.JSONUtil;
import com.ackong.common.Response;
import com.ackong.webframe.core.orm.MyPage;
import com.ackong.webframe.core.orm.Page;
import com.ackong.webframe.core.orm.PropertyFilter;
import com.ackong.webframe.core.util.Cue;
import com.ackong.webframe.core.util.Struts2Utils;
import com.ackong.webframe.core.web.CoreActionSupport;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * goodsCategory
 *
 * @author QuSpaceDragon
 * @create 2016-06-03
 */
@Results({
        @Result(name = "reload", location = "goodsCategory.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/goodsCategory/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/goodsCategory/input.jsp")})
@Action("goodsCategory")
public class GoodsCategoryAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(GoodsCategoryAction.class);
    @Autowired
    private GoodsCategoryManager goodsCategoryManager;
    private Page<GoodsCategory> page = new MyPage<GoodsCategory>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private GoodsCategory goodsCategory;
    private List<GoodsCategory> parentGoodsCategory;

    @Override
    public String execute() throws Exception {
        return list();
    }

    public String delete() throws Exception {
        try {
            this.goodsCategoryManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.goodsCategory = this.goodsCategoryManager.getGoodsCategory(this.id);
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
        this.page = this.goodsCategoryManager.getPage(this.page, filters);
        return "success";
    }


    public String save() throws Exception {
        Response re = new Response();
        try {
            if (goodsCategory.getGoodsCategoryId() != null) {
                GoodsCategory gc = goodsCategoryManager.getGoodsCategory(goodsCategory.getGoodsCategoryId());
                this.goodsCategory.setHasNext(gc.getHasNext());
                this.goodsCategory.setMutexId(gc.getMutexId());
            }
            this.goodsCategoryManager.save(this.goodsCategory);
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

    public String getGoodsCategorys() {
        List<GoodsCategory> list = goodsCategoryManager.findByParentId(id);
        Struts2Utils.renderJson(JSONUtil.json2String(list));
        return null;
    }

    public Page<GoodsCategory> getPage() {
        return page;
    }

    public void setPage(Page<GoodsCategory> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GoodsCategory getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(GoodsCategory goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public List<GoodsCategory> getParentGoodsCategory() {
        parentGoodsCategory = goodsCategoryManager.findByParentId(1);
        return parentGoodsCategory;
    }
}
