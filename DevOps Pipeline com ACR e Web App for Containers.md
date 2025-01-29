# Reprodução de Configurações e Solução de Problemas no Azure DevOps Pipeline com ACR e Web App for Containers

Este documento detalha todas as etapas realizadas para configurar e solucionar problemas relacionados a pipelines no Azure DevOps, integração com Azure Container Registry (ACR) e Azure Web App for Containers. O objetivo é permitir a reprodução precisa dos resultados obtidos.

---

## **1. Configurando o Azure Container Registry (ACR)**

### **Passos:**

1. **Criar o ACR:**

   - No portal Azure, navegue até **Container Registries**.
   - Clique em **Create** e preencha os campos necessários:
     - Escolha o **Resource Group**.
     - Nomeie o registro (ex.: `acrmamapitempo`).
     - Defina a SKU (Standard, Premium, etc.).
   - Clique em **Review + Create**.

2. **Permitir autenticação via Service Principal:**

   - Navegue até o ACR criado.
   - Desative o "Admin User" para maior segurança.
   - Crie uma aplicação no **App Registrations** para uso do Service Principal:
     - Registre um novo aplicativo e copie o **Application (client) ID** e **Directory (tenant) ID**.
     - Gere uma **client secret** e salve o valor.
   - No ACR, configure permissões do Service Principal:
     - **Access Control (IAM)** > **Add Role Assignment** > Escolha "AcrPush" > Selecione o aplicativo registrado.

---

## **2. Configurando o Azure Web App for Containers**

### **Passos:**

1. **Criar o Web App for Containers:**

   - No portal Azure, navegue até **App Services**.
   - Clique em **Create** e selecione "Docker Container" como opção de publicação.
   - Escolha o mesmo Resource Group do ACR.
   - No menu de "Docker", configure o repositório para apontar para o ACR:
     - **Registry source:** Azure Container Registry.
     - **Image:** Selecione a imagem desejada.
     - **Tag:** Escolha a tag apropriada.
   - Clique em **Review + Create**.

2. **Habilitar a integração com o ACR:**

   - Certifique-se de que o Service Principal usado tenha acesso ao ACR.

3. **Configurar variáveis de ambiente:**

   - No menu do Web App, configure as variáveis de ambiente exigidas pelo container.

---

## **3. Configurando o Azure DevOps Pipeline**

### **Passos:**

1. **Criar o Pipeline:**

   - Navegue até o projeto no Azure DevOps.
   - Clique em **Pipelines > New Pipeline**.
   - Escolha o repositório onde está o código e o arquivo `Dockerfile`.

2. **Criar o YAML do pipeline:**

   - Adicione o seguinte YAML ao repositório:

```yaml
pool:
  name: 'mam-api-pool'  # Substitua pelo seu Agent Pool

steps:
- script: echo "Testando o agente"
  displayName: "Teste do Agente"

- task: Docker@2
  inputs:
    containerRegistry: 'acrapidemomamtempo'
    repository: 'api-mam-tempo-test'
    command: 'buildAndPush'
    Dockerfile: '**/Dockerfile'
```

3. **Configurar o Agent Pool:**

   - Verifique se o **Agent Pool** está corretamente configurado:
     - **Organization Settings > Agent Pools**.
     - Certifique-se de que o agente aparece como **Online** e está no pool especificado no YAML.

4. **Executar o Pipeline:**

   - Clique em **Run Pipeline**.
   - Selecione os parâmetros necessários e execute.

---

## **4. Resolvendo Problemas Comuns**

### **Erro: "Cannot perform credential operations"**

- Certifique-se de que o "Admin User" no ACR está desativado.
- Verifique se o Service Principal tem a função **AcrPush** no ACR.

### **Erro: "No hosted parallelism has been purchased or granted"**

- Solicite paralelismo gratuito preenchendo o formulário em [https://aka.ms/azpipelines-parallelism-request](https://aka.ms/azpipelines-parallelism-request).

### **Erro: "Windows containers are not allowed"**

- Crie um novo Resource Group, pois alguns tipos de recursos não são permitidos em grupos padrão.

### **Erro: "No pool was specified"**

- Certifique-se de que o YAML especifica o **Agent Pool** correto:

```yaml
pool:
  name: 'mam-api-pool'
```

### **Erro: "VS30063: You are not authorized to access **[**https://dev.azure.com**](https://dev.azure.com)**"**

- Verifique se as credenciais do agente estão corretas.
- Certifique-se de que o Service Principal ou PAT usado tenha permissões para acessar o Azure DevOps.

---

## **5. Testando e Validando**

- Execute o pipeline com uma tarefa simples para garantir que o agente está configurado corretamente:

```yaml
steps:
- script: echo "Agente configurado e funcional!"
  displayName: "Teste de Validação"
```

- Verifique os logs de execução no Azure DevOps para identificar e corrigir eventuais problemas.

---

## **Conclusão**

Seguindo este guia passo a passo, você deve conseguir configurar e executar pipelines no Azure DevOps com integração ao Azure Container Registry e Web App for Containers. Caso encontre novos problemas, utilize os logs do agente e do pipeline para diagnosticar erros e aplicar as soluções descritas aqui.

