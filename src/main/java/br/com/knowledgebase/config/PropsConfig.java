package br.com.knowledgebase.config;

import br.com.knowledgebase.adapters.outbound.security.JwtProps;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ JwtProps.class })
public class PropsConfig { }
