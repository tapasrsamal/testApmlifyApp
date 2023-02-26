

/* Amplify Params - DO NOT EDIT
	API_VERICARBONPORTAL_GRAPHQLAPIIDOUTPUT
	API_VERICARBONPORTAL_METHANPREERTTABLE_ARN
	API_VERICARBONPORTAL_METHANPREERTTABLE_NAME
	API_VERICARBONPORTAL_PROJECTTABLE_ARN
	API_VERICARBONPORTAL_PROJECTTABLE_NAME
	ENV
	REGION
Amplify Params - DO NOT EDIT */

package example;

import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class LambdaRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{   
	
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context){
    	try{
    	System.out.print("in the java handler")	;	
    	//System.out.print("request:->"+request)	;
        //String greetingString = String.format("Hello %s %s!", request.firstName, request.lastName);
        String greetingString = "200 OK";
        ResponseClass responseString = new ResponseClass("API Call to Java Lambda works");
        System.out.print("Table NAme : " +System.getenv("API_VERICARBONPORTAL_METHANPREERTTABLE_NAME"))	;
		ScanResult scanResult = dynamoDB.scan(new ScanRequest().withTableName(System.getenv("API_VERICARBONPORTAL_METHANPREERTTABLE_NAME")));
		List<ProjectedOffset> projectedOffset = new ArrayList<ProjectedOffset>();
		List<Map<String, AttributeValue>> items = scanResult.getItems();
		items.forEach(item -> {
			ProjectedOffset offset = new ProjectedOffset();
			offset.setId(item.get("id").getS());
			offset.setProjectId(item.get("projectId").getS());
			offset.setApinumber(item.get("apinumber").getS());
			offset.setProjectYear(Integer.parseInt(item.get("projectYear").getN()));
			offset.setCalendarYear(Integer.parseInt(item.get("calendarYear").getN()));
			offset.setOffsetValue(Float.parseFloat(item.get("offsetValue").getN()));
			offset.setOffsetPrice(Float.parseFloat(item.get("offsetPrice").getN()));
			projectedOffset.add(offset);
			
		});
		
		String responseBody = gson.toJson(projectedOffset);
        Map<String, String> responseHeaders = new HashMap<>();
        //responseHeaders.put("Content-Type", "application/json");
        // allow CORS for dev, but need to remove for prod
        responseHeaders.put("Access-Control-Allow-Origin", "*");
        APIGatewayProxyResponseEvent response =  new APIGatewayProxyResponseEvent(); 
        response.setHeaders(responseHeaders);
        response.setStatusCode(200);
        return response.withBody(responseBody);
        

    	}catch (Exception ex) {
    		System.out.print("exception"+ex.getMessage())	;
    		return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(gson.toJson(ex));
    	}
        
    }
}
