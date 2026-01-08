package de.assecor.persons.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class JacksonMessageConverterConfig {

  @Bean
  public JacksonJsonHttpMessageConverter jacksonMessageConverter(JsonMapper mapper) {
    JacksonJsonHttpMessageConverter converter = new JacksonJsonHttpMessageConverter(mapper);

    converter.setSupportedMediaTypes(
        Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_PROBLEM_JSON));

    return converter;
  }
}
