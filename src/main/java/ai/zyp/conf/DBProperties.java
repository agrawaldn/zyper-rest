package ai.zyp.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBProperties {

    private static final Logger logger = LoggerFactory.getLogger(DBProperties.class);

    @Value("${db.host}")
    private String host;

    @Value("${db.index}")
    private String index;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public static DBProperties getInstance(){
        DBProperties dbProperties = null;
        try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class, DBProperties.class);) {
            dbProperties = context.getBean(DBProperties.class);
        }catch(Exception e){
            logger.error("Could not get handle on DBProperties",e);
        }
        return dbProperties;
    }

//    public static void main(String[] args){
//        System.out.println("host = "+DBProperties.getInstance().getHost());
//    }
}
