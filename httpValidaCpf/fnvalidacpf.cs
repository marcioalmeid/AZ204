using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

namespace httpValidaCpf
{
    public static class fnvalidacpf
    {
        [FunctionName("fnvalidacpf")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Function,  "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            log.LogInformation("Function que valida cpf ");

            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            dynamic data = JsonConvert.DeserializeObject(requestBody);
            if(data == null|| data?.cpf == null)
            {
                return new BadRequestObjectResult("Por favor, informe o cpf");
            }

           string cpf = data?.cpf;
           string responseMessage=""; 
            Console.WriteLine($"CPF: {cpf} {data}");
            if(IsValid(cpf))
            {
                responseMessage = "CPF válido";
            }
            else
            {
                responseMessage = "CPF inválido";
            }

            return new OkObjectResult(responseMessage);
        }

        public static bool IsValid(string cpf)
        {
            // Remove caracteres não numéricos
            var numericCpf = new char[cpf.Length];
            int index = 0;
            foreach (var c in cpf)
            {
                if (char.IsDigit(c))
                {
                    numericCpf[index++] = c;
                }
            }
            cpf = new string(numericCpf, 0, index);

            // Verifica se o CPF tem 11 dígitos
            if (cpf.Length != 11)
                return false;

            // Verifica se todos os dígitos são iguais (ex: 00000000000)
            bool allDigitsSame = true;
            for (int i = 1; i < cpf.Length; i++)
            {
                if (cpf[i] != cpf[0])
                {
                    allDigitsSame = false;
                    break;
                }
            }
            if (allDigitsSame)
                return false;

            // Calcula os dígitos verificadores
            int[] multiplicador1 = { 10, 9, 8, 7, 6, 5, 4, 3, 2 };
            int[] multiplicador2 = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };

            string tempCpf = cpf[..9];
            int soma = 0;

            for (int i = 0; i < 9; i++)
                soma += int.Parse(tempCpf[i].ToString()) * multiplicador1[i];

            int resto = soma % 11;
            if (resto < 2)
                resto = 0;
            else
                resto = 11 - resto;

            string digito = resto.ToString();
            tempCpf += digito;
            soma = 0;

            for (int i = 0; i < 10; i++)
                soma += int.Parse(tempCpf[i].ToString()) * multiplicador2[i];

            resto = soma % 11;
            if (resto < 2)
                resto = 0;
            else
                resto = 11 - resto;

            digito += resto.ToString();

            return cpf.EndsWith(digito);
        }



    }
}
