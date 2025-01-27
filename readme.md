## Dependencies
Install the Azure Tools extension pack 

```bash
brew tap azure/functions
brew install azure-functions-core-tools@4

brew update
brew install azure-cli
```

## Create project function
func init httpValidaCpf --worker-runtime dotnet --force

func new
Function name: fnvalidacpf

func start

# To publish
az login
func azure functionapp publish fnmamappeastdev001
 
# Modifique local.settings.json
   "FUNCTIONS_WORKER_RUNTIME": "dotnetIsolated"  

# Remove resource group
az group list   
az group delete --name   fnmamappeastdev001