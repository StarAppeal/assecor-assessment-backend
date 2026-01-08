package de.assecor.persons.config;

import de.assecor.persons.service.initialdata.CsvInitialDataProvider;
import de.assecor.persons.service.initialdata.InitialDataProvider;
import de.assecor.persons.service.initialdata.ListInitialDataProvider;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class InitialDataConfig {

  private static final Logger logger = LoggerFactory.getLogger(InitialDataConfig.class);

  @Value("${initial.data.csv.path:#{null}}")
  private Resource csvResource;

  @Bean
  public InitialDataProvider initialDataProvider() {
    if (csvResource != null && csvResource.exists()) {
      logger.info("Configuring CSV initial data provider with file: {}", csvResource);
      return new CsvInitialDataProvider(csvResource);
    } else {
      logger.info("No CSV data configured, using empty initial data provider");
      return new ListInitialDataProvider(Collections.emptyList());
    }
  }
}
