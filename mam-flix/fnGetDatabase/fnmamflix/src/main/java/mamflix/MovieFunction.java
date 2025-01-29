package mamflix;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure.cosmos.models.CosmosQueryRequestOptions;

import com.google.gson.Gson;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class MovieFunction {

    @FunctionName("GetMovie")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                route = "movies/{id}",
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
                @BindingName("id") String id,

            final ExecutionContext context) {

                context.getLogger().info("Processing request to get movie with ID: " + id);

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

                 // Consultar o item pelo ID
        try {

            String query = "SELECT * FROM c  ";
            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
            CosmosPagedIterable<Movie> movies = container.queryItems(query, options, Movie.class);

            // Iterar sobre os resultados e logar os itens
            for (Movie movie : movies) {
                System.out.println("Movie ID: " + movie.getId());
                System.out.println("Title: " + movie.getTitle());
                System.out.println("Year: " + movie.getYear());
                System.out.println("Video: " + movie.getVideo());
                System.out.println("Thumb: " + movie.getThumb());
                System.out.println("---------------------------");
            }


            CosmosItemResponse<Movie> itemResponse = container.readItem(id, new PartitionKey(id), Movie.class);
            Movie movie = itemResponse.getItem();

            return request.createResponseBuilder(HttpStatus.OK)
                    .body(movie)
                    .build();
        } catch (CosmosException e) {
            context.getLogger().severe("Error retrieving movie: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Movie not found")
                    .build();
        }

           
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while loading the movie.")
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