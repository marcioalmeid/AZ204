fnPostDataStorage -> Video
fnPostDataStorage -> Image
fnPostDatabase -> CosmosDB
fnGetAllMovies -> CosmosDB
fnGetMovieDetails -> CosmosDB

#Azure emulator
npm install -g azurite
azurite


## Dependencies
Install the Azure Tools extension pack 

```bash
brew tap azure/functions
brew install azure-functions-core-tools@4
 

brew update
brew install azure-cli


dotnet add package Azure.Storage.Blobs
```

## Create project function fnPostDataStorage
func init fnPostDataStorage --worker-runtime dotnet  

func new
Function name: fnPostDataStorage


## Create project function fnPostDatabase
```bash
dotnet add package Microsoft.Azure.WebJobs.Extensions.CosmosDB
dotnet add package Microsoft.Azure.Functions.Extensions

dotnet add package Microsoft.Azure.WebJobs.Extensions.Http
dotnet add package Microsoft.Azure.WebJobs
dotnet add package Microsoft.Azure.WebJobs.Extensions
dotnet add package Microsoft.Azure.WebJobs.Extensions.CosmosDB
dotnet build

dotnet restore
```

func init fnPostDataStorage --worker-runtime dotnet  

func new
Function name: fnPostDatabase

func start

# To publish
az login
func azure functionapp publish fnmamappeastdev001
 
# Modifique local.settings.json
   "FUNCTIONS_WORKER_RUNTIME": "dotnetIsolated"  

# Remove resource group
az group list   
az group delete --name   fnmamappeastdev001
