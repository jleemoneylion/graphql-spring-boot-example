package com.example;

import graphql.schema.*;
import graphql.schema.idl.*;
import lombok.Builder;
import lombok.Value;
import org.springframework.core.io.Resource;

import java.io.*;

public class FruitSchemaProvider {
    public static GraphQLSchema createSchema(Resource schemaResource) {
        try {
            RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                    .type("QueryType", typeWiring -> typeWiring
                            .dataFetcher("apple", new AppleDataFetcher())
                            .dataFetcher("orange", new OrangeDataFetcher()))
                    .type("Orange", typeWriting -> typeWriting
                            .dataFetcher("color", new StaticDataFetcher("orange")))
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

    private static class AppleDataFetcher implements DataFetcher<Apple> {
        @Override
        public Apple get(DataFetchingEnvironment env) {
            String color = env.getArgument("color");
            return Apple.builder()
                    .color(color)
                    .build();
        }
    }

    private static class OrangeDataFetcher implements DataFetcher<Orange> {
        @Override
        public Orange get(DataFetchingEnvironment env) {
            String size = env.getArgument("size");
            return Orange.builder()
                    .size(size)
                    .build();
        }
    }

    @Value
    @Builder
    private static class Apple {
        String color;
    }

    @Value
    @Builder
    private static class Orange {
        String size;
    }
}
