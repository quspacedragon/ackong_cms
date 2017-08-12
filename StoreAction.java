package com.ackong.cms.web;

import com.ackong.cms.entity.Sale;
import com.ackong.cms.entity.Store;
import com.ackong.cms.service.SaleManager;
import com.ackong.cms.service.StoreManager;
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

/**
 * Store
 * store
 *
 * @author QuSpaceDragon
 * @create 2016-12-15
 */
@Results({
        @Result(name = "reload", location = "store.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/store/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/store/input.jsp")})
public class StoreAction extends CoreActionSupport<Store> {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(StoreAction.class);
    @Autowired
    private StoreManager storeManager;
    @Autowired
    private SaleManager saleManager;
    private Page<Store> page = new MyPage<Store>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private Store store;
    private List<Sale> saleList;


    @Override
    public String execute() throws Exception {
        return list();
    }

    @Override
    protected void prepareModel() throws Exception {
        if (this.id != null)
            this.store = this.storeManager.getStore(this.id);
        else
            this.store = new Store();

    }

    public String delete() throws Exception {
        try {
            this.storeManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.store = this.storeManager.getStore(this.id);
            List<Sale> salesByStoreId = saleManager.getSalesByStoreId(store.getStoreId());
            int totalQuotat = 0;
            int num = 0;
            for (Sale sale : salesByStoreId) {
                num++;
                totalQuotat += sale.getQuota();
            }
            store.setTotalQuota(totalQuotat);
            store.setSaleNum(num);
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
        this.page = this.storeManager.getPage(this.page, filters);
        for (Store store1 : page.getResult()) {
            List<Sale> salesByStoreId = saleManager.getSalesByStoreId(store1.getStoreId());
            int totalQuotat = 0;
            int num = 0;
            for (Sale sale : salesByStoreId) {
                num++;
                totalQuotat += sale.getQuota();
            }
            store1.setTotalQuota(totalQuotat);
            store1.setSaleNum(num);
        }
        return "success";
    }

    public String recover() throws Exception {
        Response re = new Response();
        try {

            this.storeManager.save(this.store);
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

            this.storeManager.save(this.store);
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

    public Page<Store> getPage() {
        return page;
    }

    public void setPage(Page<Store> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<Sale> getSaleList() {
        this.saleList = saleManager.getSalesByStoreId(id);
        return saleList;
    }


    @Override
    public Store getModel() {
        return this.store;
    }
}
