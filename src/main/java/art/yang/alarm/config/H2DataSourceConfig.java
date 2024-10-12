package art.yang.alarm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;

/**
 * @Author arTGOD
 * @Date 2024/10/12 17:15
 * @Description
 */
@Configuration
@Slf4j
@AutoConfigureAfter(DataSource.class)
public class H2DataSourceConfig {
    private static final String schema="classpath:db/schema-h2.sql";

    @Autowired
    DataSource dataSource;

    @Autowired
    ApplicationContextRegister applicationContextRegister;

    @PostConstruct
    public  void init() throws Exception {
        String userHome= System.getProperty("user.home");
        File f = new File(userHome+ File.separator+"my.lock");
        if(!f.exists()){
            log.info("--------------初始化h2数据----------------------");
            f.createNewFile();
            Resource resource= applicationContextRegister.getResource(schema);
            ScriptUtils.executeSqlScript(dataSource.getConnection(),resource);
        }
    }
}
