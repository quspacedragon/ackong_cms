package com.ackong.cms.web;

import com.ackong.cms.entity.Goods;
import com.ackong.cms.entity.GoodsImg;
import com.ackong.cms.service.GoodsImgManager;
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
import org.apache.log4j.Logger;
import org.apache.http.HttpStatus;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * goodsImg
 *
 * @author QuSpaceDragon
 * @create 2016-07-21
 */
@Results({
        @Result(name = "reload", location = "goodsImg.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/goodsImg/list.jsp"),
        @Result(name = "input", location = "/WEB-INF/pages/goodsImg/input_new.jsp")})
@Action("goodsImg")
public class GoodsImgAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(GoodsImgAction.class);
    @Autowired
    private GoodsImgManager goodsImgManager;
    @Autowired
    private GoodsManager goodsManager;
    private Page<GoodsImg> page = new MyPage<GoodsImg>(CoreActionSupport.DEFAULT_PAGESIZE);
    private Integer id;
    private GoodsImg goodsImg;
    private List<Goods> goodsList;

    @Override
    public String execute() throws Exception {
        return list();
    }

    public String delete() throws Exception {
        try {
            this.goodsImgManager.delete(Cue.getIds(this.id));
            addActionMessage("删除成功");
        } catch (Exception e) {
            addActionError(e.getMessage());
            this.log.error("删除失败:", e);
        }
        return "reload";
    }

    public String input() throws Exception {
        if (this.id != null) {
            this.goodsImg = this.goodsImgManager.getGoodsImg(this.id);
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
        this.page = this.goodsImgManager.getPage(this.page, filters);
        return "success";
    }


    public String save() throws Exception {
        Response re = new Response();
        try {
            this.goodsImgManager.save(this.goodsImg);
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

    public Page<GoodsImg> getPage() {
        return page;
    }

    public void setPage(Page<GoodsImg> page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GoodsImg getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(GoodsImg goodsImg) {
        this.goodsImg = goodsImg;
    }

    public List<Goods> getGoodsList() {
        goodsList = goodsManager.getAll();
        return goodsList;
    }
}
