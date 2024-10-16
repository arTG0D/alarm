package art.yang.alarm.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @Author arTGOD
 * @Date 2024/10/12 17:14
 * @Description
 */
@Component
public class ApplicationContextRegister implements ApplicationContextAware {
    private ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Resource getResource(String url) {
        return this.applicationContext.getResource(url);
    }
}
