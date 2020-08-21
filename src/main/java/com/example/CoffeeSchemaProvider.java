package com.example;

import graphql.schema.*;
import graphql.schema.idl.*;
import lombok.Builder;
import lombok.Value;
import org.springframework.core.io.Resource;

import java.io.*;

public class CoffeeSchemaProvider {
    public static GraphQLSchema createSchema(Resource schemaResource) {
        try {
            RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                    .type("QueryType", typeWiring -> typeWiring
                            .dataFetcher("mocha", new MochaDataFetcher())
                            .dataFetcher("latte", new LatteDataFetcher()))
                    .build();

            SchemaParser schemaParser = new SchemaParser();
            SchemaGenerator schemaGenerator = new SchemaGenerator();
            InputStream schemas = schemaResource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(schemas));
            TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(reader);
            return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static class MochaDataFetcher implements DataFetcher<Mocha> {
        @Override
        public Mocha get(DataFetchingEnvironment env) {
            String color = env.getArgument("temperature");
            return Mocha.builder()
                    .temperature(color)
                    .build();
        }
    }

    private static class LatteDataFetcher implements DataFetcher<Latte> {
        @Override
        public Latte get(DataFetchingEnvironment env) {
            String size = env.getArgument("size");
            return Latte.builder()
                    .size(size)
                    .build();
        }
    }

    @Value
    @Builder
    private static class Mocha {
        String temperature;
    }

    @Value
    @Builder
    private static class Latte {
        String size;
    }
}
