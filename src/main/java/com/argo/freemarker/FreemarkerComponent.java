package com.argo.freemarker;

import com.argo.freemarker.spring.HtmlFreeMarkerConfigurer;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Freemarker模板封装
 * @author yaming_deng
 *
 */
public class FreemarkerComponent implements InitializingBean{

	private Configuration configuration ;

	private final Logger logger = LoggerFactory.getLogger(FreemarkerComponent.class);

    private HtmlFreeMarkerConfigurer freeMarkerConfig;

    public HtmlFreeMarkerConfigurer getFreeMarkerConfig() {
        return freeMarkerConfig;
    }

    public void setFreeMarkerConfig(HtmlFreeMarkerConfigurer freeMarkerConfig) {
        this.freeMarkerConfig = freeMarkerConfig;
    }

    @Override
	public void afterPropertiesSet() throws Exception {
        if (this.freeMarkerConfig == null){
            logger.warn("freeMarkerConfig is missing.");
        }else {
            logger.info("found freeMarkerConfig.");
            this.configuration = freeMarkerConfig.getConfiguration();
        }
	}

	/**
	 * 设置模板加载类.
	 * @param templateLoader
     */
	public void configTemplateLoader(TemplateLoader templateLoader){
		this.configuration.setTemplateLoader(templateLoader);
	}

	/**
	 * 配置文件
	 * @return Configuration
     */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * 添加全局变量
	 * @param name 参数名
	 * @param object 参数值
     */
	public void addGlobalParams(String name, Object object){
        if (null == object){
            return;
        }

        try {
            this.configuration.setSharedVariable(name, object);
        } catch (TemplateModelException e) {
            logger.error(e.getMessage(), e);
        }
    }

	/**
	 * 重新加载模板
	 * 比较适合模板存放在数据库中. 例如邮件模板
	 * @param name 模板全名
     */
	public void reloadTemplate(String name){
		try {
			this.configuration.removeTemplateFromCache(name);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 解析FTL模板
	 * @param filePath 相对templateFolder的路径.
	 * @return Template 模板实例
	 * @throws Exception 模板解析异常
	 */
	public Template parseTemplateFromFile(String filePath) throws Exception{
		Template template;
		try{
			//根据模板路径获取模板对象
			template = configuration.getTemplate(filePath);
			return template;
		}catch (IOException e) {
			logger.error("==============load FTL template error:,filePath="+filePath, e);
			throw new Exception("系统IO异常！,filePath="+filePath,e);
		}
	}

	/**
	 * 渲染模板
	 * @param templateFile 模板路径
	 * @param params 参数
	 * @return 输出内容
	 * @throws Exception
     */
	public String render(String templateFile, Map<String, Object> params) throws Exception{
		Template template;
		try {
			template = this.parseTemplateFromFile(templateFile);
		} catch (Exception e1) {
			logger.warn("页面模板不存在: templateFile="+templateFile);
			return null;
		}

		try{

			//加载插件类获取的数据
			if(params == null){
                params = new HashMap<String, Object>();
			}

			StringWriter sw = new StringWriter();
			template.process(params, sw);

			StringBuffer sb = sw.getBuffer();
			return sb.toString();
			
		}catch (Exception e) {
			throw new Exception("输出页面出错！templateFile="+templateFile,e);
		}
	}

	/**
	 * 添加一个模板
	 * @param name
	 * @param content
     */
	public void putTemplate(String name, String content){
		TemplateLoader templateLoader = this.configuration.getTemplateLoader();
		if (templateLoader instanceof StringTemplateLoader){
			StringTemplateLoader stringTemplateLoader = (StringTemplateLoader)templateLoader;
			stringTemplateLoader.putTemplate(name, content);
		}
	}

}
