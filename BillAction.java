package com.ackong.cms.web;

import com.ackong.cms.entity.Bill;
import com.ackong.cms.service.BillManager;
import com.ackong.cms.service.OrderSkuManager;
import com.ackong.cms.utils.JSONUtil;
import com.ackong.common.OrderStatus;
import com.ackong.webframe.core.orm.BootPage;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * bill
 *
 * @author QuSpaceDragon
 * @create 2016-05-23
 */
@Results({
        @Result(name = "reload", location = "bill.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/bill/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/bill/input.jsp")})
public class BillAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(BillAction.class);
    @Autowired
    private BillManager billManager;
    @Autowired
    private OrderSkuManager orderSkuManager;
    private Page<Bill> page = new BootPage<Bill>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private Bill bill;

    public String delete() throws Exception {
        try {
            this.billManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String getGoods() throws Exception {
        Map<String, Object> response = new HashMap<String, Object>();
        try {
            String id = Struts2Utils.getParameter("id");
            Bill bill = billManager.getingBill(Integer.parseInt(id));
            bill.setOrderSkus(orderSkuManager.findByBillId(bill.getOrderId()));
            response.put("code", "200");
            response.put("data", bill);
            Struts2Utils.renderJson(JSONUtil.json2String(response));
        } catch (Exception e) {
            response.put("code", "201");
        }
        return null;
    }

    public String sendGoods() throws Exception {
        Map<String, String> response = new HashMap<String, String>();
        try {
            String ids = Struts2Utils.getParameter("ids");
            String shipping_name = Struts2Utils.getParameter("shipping_name");
            String tracking_number = Struts2Utils.getParameter("tracking_number");
            String[] sid = ids.split(",");
            for (String id : sid) {
                Bill bill = billManager.getBill(Integer.parseInt(id));
                System.out.println(bill.getOrderStatus());
                System.out.println(OrderStatus.STATUS_PENDING_SHIPPED.getStatus());
                if (bill.getOrderStatus().equals(OrderStatus.STATUS_PENDING_SHIPPED.getStatus())) {
                    bill.setSendTime(new Date());
                    bill.setOrderStatus(OrderStatus.STATUS_PENDING_RECEIPT.getStatus());
//                    bill.setShippingName(Integer.parseInt(shipping_name));
//                    bill.setTrackingNumber(Integer.parseInt(tracking_number));
                    billManager.save(bill);
                }
            }
        } catch (Exception e) {
            response.put("code", "201");
            response.put("message", "发货失败:" + e.getMessage());
            Struts2Utils.renderJson(JSONUtil.json2String(response));
            this.log.error("发货失败:", e);
        }
        response.put("code", "200");
        response.put("message", "发货成功");
        Struts2Utils.renderJson(JSONUtil.json2String(response));
        return null;
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.bill = this.billManager.getBill(this.id);
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
        this.page = this.billManager.getPage(this.page, filters);
        return "success";
    }


    public String save() throws Exception {
        try {
            this.billManager.save(this.bill);
            addActionMessage("保存成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("保存失败:", e);
            return "input";
        }
        return "reload";
    }

    public Page<Bill> getPage() {
        return page;
    }

    public void setPage(Page<Bill> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}
