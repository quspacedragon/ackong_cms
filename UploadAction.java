package com.ackong.cms.web;

import com.ackong.cms.utils.JSONUtil;
import com.ackong.common.Constant;
import com.ackong.common.Response;
import com.ackong.util.ImageUtil;
import com.ackong.util.RandomUtil;
import com.ackong.util.ValidateUtil;
import com.ackong.webframe.core.util.Struts2Utils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Results({
        @Result(name = "reload", location = "upload.action", type = "redirect"),
        @Result(name = "success", location = "/WEB-INF/pages/upload/list.jsp")})
public class UploadAction extends ActionSupport {
    private static final Logger log = Logger.getLogger(UploadAction.class);

    private File file;
    private File[] fileData;
    private String[] fileDataFileName;
    private String fileFileName;
    private String fileContentType;
    private String fileName;

    @Override
    public String execute() throws Exception {
        return SUCCESS;
    }

    @Action(value = "upload!upload.action", interceptorRefs = {@InterceptorRef(value = "fileUpload", params = {
            "maximumSize", "2097152", "allowedTypes",
            "image/bmp,image/png,image/gif,image/jpeg,image/jpg"})})
    @RequestMapping(method = RequestMethod.POST)
    public String upload() throws Exception {
        Response re = new Response();


        if (file == null) {
            addActionMessage("文件不能为空");
            return "reload";
        }
        Long size = file.length();
        if (size > 2 * 1024 * 1024) {
            addActionMessage("图片过大");
            return "reload";
        }
        if (!ValidateUtil.isImg(fileFileName)) {
            addActionMessage("图片格式错误");
            return "reload";
        }
        String dir = ImageUtil.getFileDir();
        String ext = fileFileName.substring(fileFileName.indexOf(".") + 1);
        fileName = RandomUtil.generateString(6) + "." + ext;
        try {
            String tar = Constant.UPLOADFILE + "/" + dir + "/" + fileName;
            log.debug("tarFile:" + tar);
            System.out.println(tar);
            FileUtils.copyFile(file, new File(tar));
            // ImageUtil.addWaterMark(file, "", tar, 0, 0, -45);
            fileName = Constant.WEBURL + "/" + dir + "/" + fileName;
            re.setCode(HttpStatus.SC_OK);
            addActionMessage("上传成功");
            return SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            addActionMessage("上传失败");
            return SUCCESS;
        }
    }

    @Action(value = "upload!uploadJson.action", interceptorRefs = {@InterceptorRef(value = "fileUpload", params = {
            "maximumSize", "2097152", "allowedTypes",
            "image/bmp,image/png,image/gif,image/jpeg,image/jpg"})})
    @RequestMapping(method = RequestMethod.POST)
    public String uploadJson() throws Exception {
        Map re = Maps.newHashMap();
        List<String> list = Lists.newArrayList();
        List<String> urls = Lists.newArrayList();
        for (int i = 0; i < fileData.length; i++) {
            File file = fileData[i];
            if (file == null) {
                re.put("error", "文件不能为空");
                Struts2Utils.renderJson(JSONUtil.json2String(re));
                return null;
            }
            Long size = file.length();
            if (size > 2 * 1024 * 1024) {
                re.put("error", "图片过大");
                Struts2Utils.renderJson(JSONUtil.json2String(re));
                return null;
            }
            if (!ValidateUtil.isImg(fileDataFileName[i])) {
                re.put("error", "图片格式错误");
                Struts2Utils.renderJson(JSONUtil.json2String(re));
                return null;
            }
            String dir = ImageUtil.getFileDir();
            String ext = fileDataFileName[i].substring(fileDataFileName[i].indexOf(".") + 1);
            fileName = RandomUtil.generateString(6) + "." + ext;
            try {
                String tar = Constant.UPLOADFILE + "/" + dir + "/" + fileName;
                log.debug("tarFile:" + tar);
                System.out.println(tar);
                FileUtils.copyFile((File) file, new File(tar));
                // ImageUtil.addWaterMark(file, "", tar, 0, 0, -45);
                fileName = Constant.WEBURL + "/" + dir + "/" + fileName;
                String img = "<img src='" + fileName + "' class='file-preview-image' alt='Desert' title='Desert'>";
                urls.add(fileName);
                list.add(img);
            } catch (IOException e) {
                e.printStackTrace();
                re.put("error", "上传失败");
            }
        }
        re.put("initialPreview", list);
        re.put("urls", urls);
        re.put("append", true);
        Struts2Utils.renderJson(JSONUtil.json2String(re));
        return null;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getFileFileName() {
        return fileFileName;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }

    public File[] getFileData() {
        return fileData;
    }

    public void setFileData(File[] fileData) {
        this.fileData = fileData;
    }

    public String[] getFileDataFileName() {
        return fileDataFileName;
    }

    public void setFileDataFileName(String[] fileDataFileName) {
        this.fileDataFileName = fileDataFileName;
    }
}
