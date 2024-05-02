package com.mani;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mani.dto.Orders;

import java.util.List;
import java.util.stream.Collectors;

public class ReadOrdersLambda {
    ObjectMapper objectMapper= new ObjectMapper();

    AmazonDynamoDB dynamoDB=AmazonDynamoDBClientBuilder.defaultClient();

    public APIGatewayProxyResponseEvent getOrder(APIGatewayProxyRequestEvent requestEvent) throws JsonProcessingException {
        ScanResult scanResult=dynamoDB.scan(new ScanRequest().withTableName(System.getenv("ORDERS_TABLE")));
        List<Orders> orders=scanResult.getItems().stream().map(item->
                new Orders(Integer.parseInt(item.get("id").getN()),item.get("itemName").getS()
                        ,Integer.parseInt(item.get("quantity").getN()))).collect(Collectors.toList());

        String jsonOutput=objectMapper.writeValueAsString(orders);


        return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody("Got all Orders "+jsonOutput);
    }
}
