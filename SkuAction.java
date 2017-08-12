package com.ackong.cms.web;

import com.ackong.cms.entity.CarCategory;
import com.ackong.cms.entity.Goods;
import com.ackong.cms.entity.Sku;
import com.ackong.cms.service.CarCategoryManager;
import com.ackong.cms.service.GoodsManager;
import com.ackong.cms.service.SkuManager;
import com.ackong.cms.utils.JSONUtil;
import com.ackong.cms.vo.CarCategoryVo;
import com.ackong.common.Response;
import com.ackong.webframe.core.orm.MyPage;
import com.ackong.webframe.core.orm.Page;
import com.ackong.webframe.core.orm.PropertyFilter;
import com.ackong.webframe.core.util.Cue;
import com.ackong.webframe.core.util.Struts2Utils;
import com.ackong.webframe.core.web.CoreActionSupport;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * sku
 *
 * @author QuSpaceDragon
 * @create 2016-06-13
 */
@Results({
        @Result(name = "reload", location = "sku.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/sku/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/sku/input.jsp")})
public class SkuAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(SkuAction.class);
    @Autowired
    private SkuManager skuManager;
    @Autowired
    private GoodsManager goodsManager;
    @Autowired
    private CarCategoryManager carCategoryManager;
    private Page<Sku> page = new MyPage<Sku>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private Sku sku;
    private List<Goods> goods;
    private List<CarCategory> carCategories;

    @Override
    public String execute() throws Exception {
        return list();
    }

    public String delete() throws Exception {
        try {
            this.skuManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.sku = this.skuManager.getSku(this.id);
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
        this.page = this.skuManager.getPage(this.page, filters);
        return "success";
    }


    public String save() throws Exception {
        Response re = new Response();
        try {
            if (sku.getSkuId() != null) {
                Sku old = skuManager.getSku(sku.getSkuId());
                sku.setCreated(old.getCreated());
                sku.setSkuStatus(old.getSkuStatus());
            }
            this.skuManager.save(this.sku);
            re.setMessage("保存成功");
        } catch (Exception e) {
            this.log.error("保存失败:", e);
            re.setMessage("保存失败");
            re.setCode(HttpStatus.SC_BAD_REQUEST);
        }
        Struts2Utils.renderJson(JSONUtil.json2String(re));
        return null;
    }


    public List<Goods> getGoods() {
        goods = goodsManager.getAll();
        return goods;
    }

    public Page<Sku> getPage() {
        return page;
    }

    public void setPage(Page<Sku> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public String getCarCategories() {
        long start = System.currentTimeMillis();
        try {
            List<CarCategoryVo> list = carCategoryManager.getCarCategorys();
            System.out.println(System.currentTimeMillis() - start);
            Struts2Utils.renderJson(JSONUtil.json2String(list));
            System.out.println(System.currentTimeMillis() - start);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
