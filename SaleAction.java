package com.ackong.cms.web;

import com.ackong.cms.entity.Sale;
import com.ackong.cms.entity.Sku;
import com.ackong.cms.service.SaleManager;
import com.ackong.cms.utils.JSONUtil;
import com.ackong.common.Response;
import com.ackong.webframe.core.orm.BootPage;
import com.ackong.webframe.core.orm.Page;
import com.ackong.webframe.core.orm.PropertyFilter;
import com.ackong.webframe.core.util.Struts2Utils;
import com.ackong.webframe.core.web.CoreActionSupport;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * sku
 *
 * @author QuSpaceDragon
 * @create 2016-06-13
 */
@Results({
        @Result(name = "reload", location = "sale.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/sale/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/sale/input.jsp")
})
public class SaleAction extends CoreActionSupport<Sale> {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(SaleAction.class);
    @Autowired
    private SaleManager saleManager;
    private Page<Sale> page = new BootPage<Sale>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private Sale sale;

    @Override
    public String save() throws Exception {
        this.saleManager.save(sale);
        return list();
    }

    @Override
    public String delete() throws Exception {
        return null;
    }

    @Override
    protected void prepareModel() throws Exception {
        if (this.id != null)
            this.sale = this.saleManager.getSale(this.id);
        else
            this.sale = new Sale();
    }

    @Override
    public Sale getModel() {
        return this.sale;
    }


    public String list() throws Exception {
        List filters = PropertyFilter.buildPropertyFilters(Struts2Utils
                .getRequest());
        if (!this.page.isOrderBySetted()) {
            this.page.setOrderBy("id");
            this.page.setOrder("desc");
        }
        this.page = this.saleManager.getPage(this.page, filters);
        return "success";
    }


    public String input() throws Exception {
        if (this.id != null) {
            this.sale = this.saleManager.getSale(this.id);
        }
        return "input";
    }

    public String pass() throws Exception {
        Response re = new Response();
        try {
            Sale sale = saleManager.getSale(id);
            if (sale != null) {
                sale.setIsValidate(true);
                saleManager.save(sale);
            }
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


    public Page<Sale> getPage() {
        return page;
    }

    public void setPage(Page<Sale> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }
}
