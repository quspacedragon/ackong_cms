package com.ackong.cms.web;

import com.ackong.cms.entity.CarCategory;
import com.ackong.cms.service.CarCategoryManager;
import com.ackong.webframe.core.orm.MyPage;
import com.ackong.webframe.core.orm.Page;
import com.ackong.webframe.core.orm.PropertyFilter;
import com.ackong.webframe.core.util.Cue;
import com.ackong.webframe.core.util.Struts2Utils;
import com.ackong.webframe.core.web.CoreActionSupport;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * carCategory
 *
 * @author QuSpaceDragon
 * @create 2016-06-14
 */
@Results({
        @Result(name = "reload", location = "carCategory.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/carCategory/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/carCategory/input.jsp")})
@Action("carCategory")
public class CarCategoryAction extends ActionSupport {

    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(CarCategoryAction.class);
    @Autowired
    private CarCategoryManager carCategoryManager;
    private Page<CarCategory> page = new MyPage<CarCategory>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private CarCategory carCategory;

    @Override
    public String execute() throws Exception {
        return list();
    }

    public String delete() throws Exception {
        try {
            this.carCategoryManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.carCategory = this.carCategoryManager.getCarCategory(this.id);
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
        this.page = this.carCategoryManager.getPage(this.page, filters);
        return "success";
    }


    public String save() throws Exception {
        try {
            this.carCategoryManager.save(this.carCategory);
            addActionMessage("保存成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("保存失败:", e);
            return "input";
        }
        return "reload";
    }

    public Page<CarCategory> getPage() {
        return page;
    }

    public void setPage(Page<CarCategory> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CarCategory getCarCategory() {
        return carCategory;
    }

    public void setCarCategory(CarCategory carCategory) {
        this.carCategory = carCategory;
    }
}
