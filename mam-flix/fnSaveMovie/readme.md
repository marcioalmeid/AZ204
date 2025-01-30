mvn clean install
mvn azure-functions:run


 

 #Specify a different port, such as 7073:


         <plugin>
            <groupId>com.microsoft.azure</groupId>
            <artifactId>azure-functions-maven-plugin</artifactId>
            <version>${azure.functions.maven.plugin.version}</version>
            <configuration>
                <appName>${functionAppName}</appName>
                <resourceGroup>${resourceGroup}</resourceGroup>
                <subscriptionId>${subscriptionId}</subscriptionId>
                <region>${region}</region>
                <runtime>
                    <os>windows</os>
                    <javaVersion>17</javaVersion>
                </runtime>
                <funcPort>7073</funcPort>
            </configuration>