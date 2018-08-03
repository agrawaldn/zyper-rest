package ai.zyp.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {

    private static final Logger logger = LoggerFactory.getLogger(AppProperties.class);

    @Value("${db.host}")
    private String host;

    @Value("${db.index}")
    private String index;

    @Value("${datetime.format}")
    private String dateTimeFormat;











    public static AppProperties getInstance(){
        AppProperties dbProperties = null;
        try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class, AppProperties.class);) {
            dbProperties = context.getBean(AppProperties.class);
        }catch(Exception e){
            logger.error("Could not get handle on DBProperties",e);
        }
        return dbProperties;
    }

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

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    //    public static void main(String[] args){
//        System.out.println("host = "+DBProperties.getInstance().getHost());
//    }
}
