#!/bin/bash

# URL do endpoint /actuator/prometheus
endpoint_url="http://localhost:8080/actuator/prometheus"

# Função para fazer a consulta ao endpoint
query_prometheus() {
  curl -s "$endpoint_url" | grep -E "jvm_memory_used_bytes|jvm_memory_max_bytes|process_cpu_usage"
}

# Executar a função e extrair os valores
result=$(query_prometheus)

# Extrair os valores de consumo de memória, memória máxima e uso de CPU
memory_used=$(echo "$result" | grep "jvm_memory_used_bytes" | awk '{print $2}')
memory_max=$(echo "$result" | grep "jvm_memory_max_bytes" | awk '{print $2}')
cpu_usage=$(echo "$result" | grep "process_cpu_usage" | awk '{print $2}')

# Exibir os resultados no console
echo "Consumo medido de memória: $memory_used"
echo "Memória máxima: $memory_max"
echo "Uso de CPU: $cpu_usage"
