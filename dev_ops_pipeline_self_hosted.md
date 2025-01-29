# Configuração de Azure DevOps Pipeline com Agente Self-Hosted

Este documento detalha os passos realizados para configurar um pipeline no Azure DevOps utilizando um agente self-hosted no macOS. Inclui solução de problemas enfrentados e ajustes necessários.

---

## **1. Contexto Inicial**
A configuração consistia em:
- **Azure Container Registry (ACR)** para hospedar imagens Docker.
- **Azure Web App for Containers** para executar a aplicação.
- **Pipeline do Azure DevOps** para automatizar build e deploy.
- **Agente self-hosted** configurado em um macOS.

## **2. Problemas e Soluções**

### **Problema 1: Configuração de permissões no ACR**
Erro ao usar ACR no Web App:
```
Cannot perform credential operations as admin user is disabled.
```
#### Solução:
1. Ativar o **Admin User** no ACR:
   - Acesse o portal do Azure.
   - Navegue até o recurso **Container Registry**.
   - Habilite **Admin User** em **Access Keys**.

2. Alternativamente, configurar **Service Principal**:
   - Crie um Service Principal no Azure.
   - Atribua permissões **AcrPull** e **AcrPush** ao Web App.

---

### **Problema 2: Agente self-hosted no macOS**
Erro ao configurar o agente:
```
ClientSecretCredential authentication failed: Invalid client secret provided.
```
#### Solução:
1. Gerei um novo client secret no Azure Active Directory para o App Registration associado ao Service Principal.
2. Atualizei o arquivo `.env` ou configurações do agente com o novo secret.

Erro posterior:
```
VS30063: You are not authorized to access https://dev.azure.com.
```
#### Solução:
1. Reconfigurei o agente utilizando um Personal Access Token (PAT).
2. Testei o acesso à organização do Azure DevOps e confirmei permissões.

---

### **Problema 3: Pool de agentes**
Erro no pipeline:
```
No pool was specified.
```
#### Solução:
1. Verifiquei o nome do **Agent Pool** no YAML:
   ```yaml
   pool:
     name: 'mam-api-pool'
   ```
2. Validei que o agente estava atribuído ao pool no Azure DevOps (**Organization Settings > Agent Pools**).

---

### **Problema 4: Erro ao executar containers Windows**
Erro:
```
Windows containers are not allowed in resource group 'DefaultResourceGroup-CCAN'.
```
#### Solução:
1. Criei um novo Resource Group para hospedar o Web App.
2. Certifiquei-me de que o Web App fosse configurado para containers Linux.

---

### **Problema 5: Execução do pipeline falhando**
Erro:
```
Free disk space on / is lower than 5%; Currently used: 97.77%.
```
#### Solução:
1. Liberei espaço em disco no macOS utilizando os comandos:
   ```bash
   sudo rm -rf /path/to/unused/files
   ```
2. Reiniciei o agente após liberar espaço:
   ```bash
   ./svc.sh restart
   ```

---

## **3. Configuração Final do YAML**
O YAML do pipeline configurado ficou assim:

```yaml
pool:
  name: 'mam-api-pool'  # Nome do Agent Pool

steps:
- script: echo "Pipeline funcionando com o pool mam-api-pool!"
  displayName: "Teste do Pipeline"

- task: Docker@2
  inputs:
    containerRegistry: 'acrapidemomamtempo'  # Nome do Container Registry configurado
    repository: 'api-mam-tempo-test'
    command: 'buildAndPush'
    Dockerfile: '**/Dockerfile'
```

---

## **4. Validação Final**
Para garantir que tudo funcionava corretamente:
1. Executei o pipeline manualmente no Azure DevOps.
2. Confirmei que o agente self-hosted executava as tarefas e fazia o push da imagem Docker para o ACR.
3. Validei o deploy do Web App para Containers no portal do Azure.

---

## **5. Dicas Gerais**
- Sempre valide permissões ao configurar agentes e registros.
- Use scripts simples no YAML para testar a configuração antes de adicionar etapas complexas.
- Monitore os logs do agente em `./_diag` no host para identificar problemas.

---

**Status: Configuração bem-sucedida e pipeline operacional.**

