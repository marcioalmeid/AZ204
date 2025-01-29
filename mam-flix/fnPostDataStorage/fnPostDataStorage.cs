using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Azure.Storage.Blobs;

namespace fnPostDataStorage
{
    public static class fnPostDataStorage
    {
        [FunctionName("fnPostDataStorage")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Function, "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            try
            {
                log.LogInformation("Processing image ...");
                log.LogInformation($"ContentLength: {req.ContentLength}");
                log.LogInformation($"REQ: {req.Body}");
                if (req.ContentLength > 100 * 1024 * 1024)
                {
                    return new BadRequestObjectResult("File size exceeds the 100MB limit.");
                }
    
                // Check if the form contains the file
                if (req.Form.Files.Count == 0)
                {
                    return new BadRequestObjectResult("The form-data does not contain a file.");
                }
     
                // Read the file content
                if(!req.Headers.TryGetValue("file-type", out var fileTypeHeader))
                {
                    return new BadRequestObjectResult("The Content-Type header is missing.");
                }
    
                var fileType = fileTypeHeader.ToString();
                 log.LogInformation($"File type: {fileType}");
                var form = await req.ReadFormAsync();
                var file = form.Files["file"];
    
    
                // using (var memoryStream = new MemoryStream())
                // {
                //     await req.Body.CopyToAsync(memoryStream);
                //     byte[] fileBytes = memoryStream.ToArray();
                //     // Process the fileBytes as needed
                //     log.LogInformation($"File size: {fileBytes.Length}");
                // }
                string connectionString = Environment.GetEnvironmentVariable("AzureWebJobsStorage");
                string containerName = fileType;
                BlobClient blobClient = new BlobClient(connectionString, containerName, file.FileName);
                BlobContainerClient containerClient = new BlobContainerClient(connectionString, containerName);
                
                await containerClient.CreateIfNotExistsAsync();
                await containerClient.SetAccessPolicyAsync(Azure.Storage.Blobs.Models.PublicAccessType.BlobContainer);
                
                string blobName = file.FileName;
                var blob = containerClient.GetBlobClient(blobName);
    
                using (var fileStream = file.OpenReadStream())
                {
                    await blob.UploadAsync(fileStream, true);
                }
    
                log.LogInformation($"File {file.FileName} processed successfully.");
    
                return new OkObjectResult( new{ 
                    Message = $"File {file.FileName} stored successfully.",
                    BlobUri = blob.Uri.ToString()
                }
                );
            }
            catch (Exception e)
            {
                  log.LogError("Error processing image ..."+e.GetBaseException().Message);
               
                return new BadRequestObjectResult(new { Message = "Error processing image:"+e.GetBaseException().Message});
            }
        }
    }
}
