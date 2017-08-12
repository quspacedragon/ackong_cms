package com.ackong.cms.web;

import com.ackong.common.Constant;
import com.ackong.webframe.core.util.Struts2Utils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author QuSpaceDragon
 * @create 2016-06-04
 */
@Action("UeditorAction")
public class UeditorAction {
    public String config(String callback) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("imageUrl", Constant.DOMAIN
                    + "/ueditor/php/controller.php?action=uploadimage");
            jo.put("imagePath", "/ueditor/php/");
            jo.put("imageFieldName", "upfile");
            jo.put("imageMaxSize", "2048");
            jo.put("imageAllowFiles", new String[]{".png", ".jpg", ".jpeg",
                    ".gif", ".bmp"});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Struts2Utils.renderJson(jo.toString());
        return null;
    }

}
