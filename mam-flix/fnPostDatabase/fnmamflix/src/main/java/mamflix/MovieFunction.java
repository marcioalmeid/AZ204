package mamflix;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemResponse;
import com.google.gson.Gson;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class MovieFunction {

    @FunctionName("SaveMovie")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Processing movie save request. V3");

        String cosmosDBConnectionString = System.getenv("CosmosDBConnection");
        try {

            // Usage
            String[] endpointAndKey = getEndpointAndKey(cosmosDBConnectionString);
            String endpoint = endpointAndKey[0];
            String key = endpointAndKey[1];
            CosmosClient client = new CosmosClientBuilder()
                    .endpoint(endpoint)
                    .key(key)
                    .buildClient();

            CosmosDatabase database = client.getDatabase("mamFlixDB");
            CosmosContainer container = database.getContainer("movies");

            String requestBody = request.getBody().orElse("");
            context.getLogger().info("Request body: " + requestBody);
            Movie movie = new Gson().fromJson(requestBody, Movie.class);
            context.getLogger().warning( "Movie TITLE: " + movie.getTitle());

            CosmosItemResponse<Movie> response = container.createItem(movie);
            context.getLogger().info("Item created with request charge: " + response.getRequestCharge());
            return request.createResponseBuilder(HttpStatus.OK)
            .body("Movie saved successfully.")
            .build();
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while saving the movie.")
                .build();
        
    }

    private String[] getEndpointAndKey(String cosmosDBConnectionString) {
        

        String[] connectionParts = cosmosDBConnectionString.split(";");
        if (connectionParts.length < 2) {
            throw new IllegalArgumentException("Invalid CosmosDB connection string format.");
        }
        String endpoint = connectionParts[0].split("=")[1];
        String key = connectionParts[1].split("=")[1];
        return new String[]{endpoint, key};
    }

     
}