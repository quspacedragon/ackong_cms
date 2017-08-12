package com.ackong.cms.web;

import com.ackong.cms.entity.Goods;
import com.ackong.cms.entity.GoodsCategory;
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
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * goods
 *
 * @author QuSpaceDragon
 * @create 2016-06-14
 */
@Results({
        @Result(name = "reload", location = "goods.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/goods/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/goods/input_new.jsp")})
public class GoodsAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(GoodsAction.class);
    @Autowired
    private GoodsManager goodsManager;
    @Autowired
    private GoodsCategoryManager goodsCategoryManager;
    private Page<Goods> page = new MyPage<Goods>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private Goods goods;
    private List<GoodsCategory> goodsCategorys;

    @Override
    public String execute() throws Exception {
        return list();
    }

    public String delete() throws Exception {
        try {
            this.goodsManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.goods = this.goodsManager.getGoods(this.id);
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
        this.page = this.goodsManager.getPage(this.page, filters);
        return "success";
    }


    public String save() throws Exception {
        Response re = new Response();
        try {
            this.goodsManager.save(this.goods);
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

    public String getGoodsList() throws Exception {
        List<Goods> list = goodsManager.getGoodsByGoodsCategoryId(id);
        Struts2Utils.renderJson(JSONUtil.json2String(list));
        return null;
    }

    public String getGoodsAll() throws Exception {
        List<Goods> list = goodsManager.getAll();
        Struts2Utils.renderJson(JSONUtil.json2String(list));
        return null;
    }

    public Page<Goods> getPage() {
        return page;
    }

    public void setPage(Page<Goods> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public List<GoodsCategory> getGoodsCategorys() {
        goodsCategorys = goodsCategoryManager.findByParentId(1);
        return goodsCategorys;
    }
}
