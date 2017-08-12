package com.ackong.cms.web;

import com.ackong.cms.entity.Bill;
import com.ackong.cms.entity.OrderSku;
import com.ackong.cms.service.BillManager;
import com.ackong.cms.service.OrderSkuManager;
import com.ackong.cms.utils.JSONUtil;
import com.ackong.common.OrderStatus;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * orderSku
 *
 * @author QuSpaceDragon
 * @create 2016-06-30
 */
@Results({
        @Result(name = "reload", location = "orderSku.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/orderSku/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/orderSku/input.jsp")})
@Action("orderSku")
public class OrderSkuAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(OrderSkuAction.class);
    @Autowired
    private OrderSkuManager orderSkuManager;
    @Autowired
    private BillManager billManager;
    private Page<OrderSku> page = new MyPage<OrderSku>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private OrderSku orderSku;

    @Override
    public String execute() throws Exception {
        return list();
    }

    public String delete() throws Exception {
        try {
            this.orderSkuManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }


    public String list() throws Exception {
        List filters = PropertyFilter.buildPropertyFilters(Struts2Utils
                .getRequest());
        if (!this.page.isOrderBySetted()) {
            this.page.setOrderBy("id");
            this.page.setOrder("desc");
        }
        this.page = this.orderSkuManager.getPage(this.page, filters);
        return "success";
    }

    public String sendGoods() throws Exception {
        Map<String, String> response = new HashMap<String, String>();
        try {
            String ids = Struts2Utils.getParameter("ids");
            String shipping_name = Struts2Utils.getParameter("shipping_name");
            String tracking_number = Struts2Utils.getParameter("tracking_number");
            String[] sid = ids.split(",");
            for (String id : sid) {
                OrderSku orderSku = orderSkuManager.getOrderSku(Integer.parseInt(id));
                if (orderSku.getStatus().equals(OrderStatus.STATUS_PENDING_SHIPPED.getStatus())) {
                    orderSku.setSendTime(new Date());
                    orderSku.setStatus(OrderStatus.STATUS_PENDING_RECEIPT.getStatus());
                    orderSku.setShippingId(Integer.parseInt(shipping_name));
                    orderSku.setTrackingNumber(tracking_number);
                    orderSkuManager.save(orderSku);
                }
            }
            if (sid.length > 0) {
                OrderSku orderSku = orderSkuManager.getOrderSku(Integer.parseInt(sid[0]));
                Bill bill = billManager.getBill(orderSku.getOrderId());
                if (bill.getOrderStatus().equals(OrderStatus.STATUS_PENDING_SHIPPED.getStatus())) {
                    Integer num = orderSkuManager.countByBillIdAndStatus(orderSku.getOrderId(), OrderStatus.STATUS_PENDING_SHIPPED.getStatus());
                    if (num < 1) {
                        bill.setOrderStatus(OrderStatus.STATUS_PENDING_RECEIPT.getStatus());
                        billManager.save(bill);
                    }
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


    public Page<OrderSku> getPage() {
        return page;
    }

    public void setPage(Page<OrderSku> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OrderSku getOrderSku() {
        return orderSku;
    }

    public void setOrderSku(OrderSku orderSku) {
        this.orderSku = orderSku;
    }
}
