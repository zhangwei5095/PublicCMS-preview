package com.publiccms.controller.admin.sys;

import static com.sanluan.common.tools.RequestUtils.getIpAddress;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.publiccms.common.base.AbstractController;
import com.publiccms.entities.log.LogOperate;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.logic.component.file.FileComponent;
import com.publiccms.logic.component.template.TemplateComponent;
import com.publiccms.logic.service.log.LogLoginService;

/**
 * 
 * CmsTemplateAdminController
 *
 */
@Controller
@RequestMapping("taskTemplate")
public class TaskTemplateAdminController extends AbstractController {
    @Autowired
    private TemplateComponent templateComponent;
    @Autowired
    private FileComponent fileComponent;

    /**
     * @param path
     * @param type
     * @param content
     * @param request
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("save")
    public String save(String path, String content, HttpServletRequest request, HttpSession session, ModelMap model) {
        SysSite site = getSite(request);
        if (notEmpty(path)) {
            try {
                String filePath = siteComponent.getTaskTemplateFilePath(site, path);
                File templateFile = new File(filePath);
                if (notEmpty(templateFile)) {
                    fileComponent.updateFile(templateFile, content);
                    logOperateService.save(new LogOperate(site.getId(), getAdminFromSession(session).getId(),
                            LogLoginService.CHANNEL_WEB_MANAGER, "update.task.template", getIpAddress(request), getDate(), path));
                } else {
                    fileComponent.createFile(templateFile, content);
                    logOperateService.save(new LogOperate(site.getId(), getAdminFromSession(session).getId(),
                            LogLoginService.CHANNEL_WEB_MANAGER, "save.task.template", getIpAddress(request), getDate(), path));
                }
                templateComponent.clear();
            } catch (IOException e) {
                model.put(ERROR, e.getMessage());
                log.error(e.getMessage());
                return TEMPLATE_ERROR;
            }
        }
        return TEMPLATE_DONE;
    }

    /**
     * @param path
     * @param request
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("delete")
    public String delete(String path, HttpServletRequest request, HttpSession session, ModelMap model) {
        if (notEmpty(path)) {
            SysSite site = getSite(request);
            String filePath = siteComponent.getTaskTemplateFilePath(site, path);
            if (verifyCustom("notExist.template", !fileComponent.deleteFile(filePath), model)) {
                return TEMPLATE_ERROR;
            }
            templateComponent.clear();
            logOperateService.save(new LogOperate(site.getId(), getAdminFromSession(session).getId(),
                    LogLoginService.CHANNEL_WEB_MANAGER, "delete.task.template", getIpAddress(request), getDate(), path));
        }
        return TEMPLATE_DONE;
    }

}
