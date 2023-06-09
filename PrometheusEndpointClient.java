import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PrometheusEndpointClient {
    public static void main(String[] args) {
        String endpointUrl = "http://localhost:8080/actuator/prometheus"; // Altere para o URL do seu endpoint

        try {
            URL url = new URL(endpointUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Processe as linhas do response do Prometheus conforme necessário
                    if (line.startsWith("process_cpu_usage")) {
                        System.out.println("Consumo de CPU: " + line);
                    } else if (line.startsWith("jvm_memory_used_bytes")) {
                        System.out.println("Consumo de memória JVM: " + line);
                    } else if (line.startsWith("node_memory_MemTotal")) {
                        System.out.println("Quantidade total de memória: " + line);
                    } else if (line.startsWith("node_cpu_seconds_total")) {
                        System.out.println("Quantidade total de CPU: " + line);
                    }
                }
                reader.close();
            } else {
                System.out.println("Falha na requisição. Código de resposta: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
