package com.example;

import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.kickstart.servlet.GraphQLHttpServlet;
import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.annotation.WebServlet;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.example")
public class GraphqlSampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphqlSampleApplication.class, args);
    }

    @Bean
    public CorsFilter createCorsFilter() {
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        configurationSource.registerCorsConfiguration("/graphql/**", config);
        return new CorsFilter(configurationSource);
    }
}

@Component
@WebServlet(name = "FruitServlet", urlPatterns = "/graphql/fruit", loadOnStartup = 1)
class FruitServlet extends GraphQLHttpServlet {
    private final Resource schemaResource;

    FruitServlet(@Value("classpath:fruit.graphqls") Resource schemaResource) {
        this.schemaResource = schemaResource;
    }

    @Override
    protected GraphQLConfiguration getConfiguration() {
        GraphQLSchema graphQLSchema = FruitSchemaProvider.createSchema(schemaResource);
        return GraphQLConfiguration.with(graphQLSchema).build();
    }
}

@Component
@WebServlet(name = "CoffeeServlet", urlPatterns = "/graphql/coffee", loadOnStartup = 1)
class CoffeeServlet extends GraphQLHttpServlet {
    private final Resource schemaResource;

    CoffeeServlet(@Value("classpath:coffee.graphqls") Resource schemaResource) {
        this.schemaResource = schemaResource;
    }

    @Override
    protected GraphQLConfiguration getConfiguration() {
        GraphQLSchema graphQLSchema = CoffeeSchemaProvider.createSchema(schemaResource);
        return GraphQLConfiguration.with(graphQLSchema).build();
    }
}