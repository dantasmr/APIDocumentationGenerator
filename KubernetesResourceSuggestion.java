import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class KubernetesResourceSuggestion {

    public static void main(String[] args) {
        String endpointUrl = "http://localhost:8080/actuator/prometheus"; // Altere para o URL do seu endpoint
        int cpuUtilizationThreshold = 80; // Limite de utilização da CPU em porcentagem (exemplo: 80%)
        int memoryUtilizationThreshold = 90; // Limite de utilização de memória em porcentagem (exemplo: 90%)
        int memoryRequestLimitPercentage = 70; // Porcentagem do limite máximo de memória para definir o valor de memory request (exemplo: 70%)
        int cpuRequestLimitPercentage = 30; // Porcentagem do limite máximo de CPU para definir o valor de CPU request (exemplo: 30%)

        try {
            URL url = new URL(endpointUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                double cpuUtilization = 0;
                double memoryUtilization = 0;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("process_cpu_usage")) {
                        String[] parts = line.split(" ");
                        if (parts.length >= 2) {
                            String value = parts[1];
                            cpuUtilization = Double.parseDouble(value);
                        }
                    } else if (line.startsWith("jvm_memory_used_bytes")) {
                        String[] parts = line.split(" ");
                        if (parts.length >= 2) {
                            String value = parts[1];
                            memoryUtilization = Double.parseDouble(value);
                        }
                    }
                }

                reader.close();

                if (cpuUtilization > 0 && memoryUtilization > 0) {
                    suggestResourceLimits(cpuUtilization, memoryUtilization, cpuUtilizationThreshold,
                            memoryUtilizationThreshold, memoryRequestLimitPercentage, cpuRequestLimitPercentage);
                } else {
                    System.out.println("Não foi possível obter informações sobre a utilização de recursos.");
                }
            } else {
                System.out.println("Falha na requisição. Código de resposta: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void suggestResourceLimits(double cpuUtilization, double memoryUtilization,
                                              int cpuUtilizationThreshold, int memoryUtilizationThreshold,
                                              int memoryRequestLimitPercentage, int cpuRequestLimitPercentage) {
        // Cálculo das sugestões para requests/limits de CPU e memória
        double cpuRequest = cpuUtilization * (cpuRequestLimitPercentage / 100.0);
        double memoryRequest = memoryUtilization * (memoryRequestLimitPercentage / 100.0);
        double cpuLimit = cpuUtilization * (cpuUtilizationThreshold / 100.0);
        double memoryLimit = memoryUtilization * (memoryUtilizationThreshold / 100.0);

        System.out.println("Sugestão para requests.cpu: " + cpuRequest);
        System
