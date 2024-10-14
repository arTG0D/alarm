package art.yang.alarm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.system.ApplicationHome;
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

    private static final String schema = "classpath:db/schema-h2.sql";

    @Autowired
    DataSource dataSource;

    @Autowired
    ApplicationContextRegister applicationContextRegister;

    @PostConstruct
    public void init() throws Exception {
        //String userHome= System.getProperty("user.home");
        ApplicationHome h = new ApplicationHome(getClass());
        String jarParentFilePath = h.getSource().getParentFile().toString();
        File dbDir = new File(jarParentFilePath, "db");
        if (!dbDir.exists()) {
            dbDir.mkdir();
        }
        File f = new File(jarParentFilePath + File.separator + "db" + File.separator + "h2db.lock");
        if (!f.exists()) {
            log.info("--------------初始化h2数据----------------------");
            f.createNewFile();
            Resource resource = applicationContextRegister.getResource(schema);
            ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
        } else {
            log.info("h2db.lock 已存在");
        }
    }
}
