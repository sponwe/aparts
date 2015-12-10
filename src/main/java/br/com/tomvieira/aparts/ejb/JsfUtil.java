
package br.com.tomvieira.aparts.ejb;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class JsfUtil {
    
    public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "---");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }
    
    public static void addErrorMessage(Exception ex, String defaultMsg) {
        Throwable cause = ex;
        while(cause.getCause() != null){
            cause = cause.getCause();
        }
        String msg = null;
        if(cause.getMessage() != null){
            msg = cause.getMessage().replaceAll("\\n", ". ");
        }else{
            msg = cause.toString();
        }
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, defaultMsg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }
    
    public static void addErrorMessage(Exception ex, String defaultMsg, String baseName) {
        Throwable cause = ex;
        while(cause.getCause() != null){
            cause = cause.getCause();
        }
        String msg = cause.getMessage().replaceAll("\\n", ". ");
        msg = tratarResourceBundle(msg, baseName);
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, defaultMsg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }
    
    public static void addErrorMessages(List<String> messages) {
        for (String message : messages) {
            addErrorMessage(message);
        }
    }

    public static void addErrorMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }
    
    public static void addErrorMessage(Exception ex) {
        String msg = ex.getMessage();
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addSuccessMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, "");
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }
    
    public static void addWarnMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, "");
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }
    
    public static String getRequestParameter(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
    }
    
    public static String getRequestParameter(String key, boolean lastName) {
        if(!lastName) return getRequestParameter(key);
        Map<String,String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        for(String k : map.keySet()){
            String[] names = k.split(":");
            if(names[names.length-1].equals(key))
                return map.get(k);
        }
        return null;
    }
    
    public static Object getObjectFromRequestParameter(String requestParameterName, Converter converter, UIComponent component) {
        String theId = JsfUtil.getRequestParameter(requestParameterName);
        return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
    }

    public static void setSession(String nome, Object obj) {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.getSession().setAttribute(nome, obj);
    }

    public static Object getSession(String nome){
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getSession().getAttribute(nome);
    }

    public static void clearSession() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.getSession().invalidate();
    }

    public static Object getObjectFromElResolver(String el){
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getApplication().getELResolver().getValue(fc.getELContext(), null, el);
    }

    public static String getUrl() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        StringBuilder sb = new StringBuilder();
        sb.append(ec.getRequestScheme());
        sb.append("://");
        sb.append(ec.getRequestServerName());
        if(ec.getRequestServerPort() != 80){
            sb.append(":");
            sb.append(ec.getRequestServerPort());
        }
        sb.append(ec.getRequestContextPath());
        sb.append("/");
        return sb.toString();
    }

    public static String getViewId() {
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }
    
    /**
     * Busca componente por id. A expressão deve seguir a hierarquia de componentes, tipo: "frm:pnlResultado:tblResultado"
     * @param expr
     * @return Componente localizado
     */
    public static UIComponent findComponent(String expr){
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        String[] exprs = expr.split(":");
        UIComponent comp = viewRoot.findComponent(exprs[0]);
        for(int i=1; i<exprs.length;i++){
            if(comp == null) return null;
            comp = comp.findComponent(exprs[i]);
        }
        return comp;
    }
    
    public static String tratarResourceBundle(String msg, String baseName) {
        String result = msg;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Locale locale = facesContext.getViewRoot().getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
        Pattern pt = Pattern.compile("\\{("+ baseName +"\\.)?([^}]+)\\}");
        Matcher mt = pt.matcher(msg);
        while(mt.find()){
            String key = mt.groupCount() == 1 ? mt.group(1) : mt.group(2);
            try {
                result = result.replace(mt.group(), bundle.getString(key));
            } catch (MissingResourceException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    public static String tratarResourceBundle(String msg, ResourceBundle bundle) {
        String result = msg;
        Pattern pt = Pattern.compile("\\{([^}]+)\\}");
        Matcher mt = pt.matcher(msg);
        while (mt.find()) {
            String key = mt.groupCount() == 1 ? mt.group(1) : mt.group(2);
            try {
                result = result.replace(mt.group(), bundle.getString(key));
            } catch (MissingResourceException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    public static void addErrorMessage(Exception ex, String defaultMsg, ResourceBundle bundle) {
        Throwable cause = ex;
        while(cause.getCause() != null){
            cause = cause.getCause();
        }
        String msg = cause.getMessage().replaceAll("\\n", ". ");
        msg = tratarResourceBundle(msg, bundle);
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, defaultMsg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }
    
    public static Long getRequestLongValue(String cmb) {
        String vlr = JsfUtil.getRequestParameter(cmb);
        if (!StringUtils.isEmpty(vlr)) {
            return Long.valueOf(vlr);
        }
        return null;
    }
}