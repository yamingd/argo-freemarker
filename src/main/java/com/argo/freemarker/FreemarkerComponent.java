package com.argo.freemarker;

import com.argo.freemarker.spring.HtmlFreeMarkerConfigurer;
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
	 * @param filePath 相对templateFolder的路径.
	 * @return
	 * @throws Exception
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
}
