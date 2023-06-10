#!/bin/bash

# URL do endpoint /actuator/prometheus
endpoint_url="http://localhost:8080/actuator/prometheus"

# Função para fazer a consulta ao endpoint
query_prometheus() {
  curl -s "$endpoint_url"
}

# Função para recomendar limites para request e limits de memória e CPU
recommend_limits() {
  local result=$1

  # Extrair os valores de consumo de memória, memória máxima e uso de CPU
  memory_used=$(echo "$result" | grep "jvm_memory_used_bytes" | awk '{print $2}')
  memory_max=$(echo "$result" | grep "jvm_memory_max_bytes" | awk '{print $2}')
  cpu_usage=$(echo "$result" | grep "process_cpu_usage" | awk '{print $2}')

  # Calcular os limites recomendados para memória e CPU
  memory_limit=$(echo "scale=2; $memory_max * 1.2" | bc)
  cpu_limit=$(echo "scale=2; $cpu_usage * 1.2" | bc)

  echo "Recomendações de limites:"
  echo "Memória Request: $memory_limit"
  echo "Memória Limit: $memory_max"
  echo "CPU Request: $cpu_limit"
  echo "CPU Limit: $cpu_usage"
}

# Função para recomendar valores para os parâmetros da JVM -Xms e -Xmx
recommend_jvm_params() {
  local total_memory=$(free -m | awk '/^Mem:/{print $2}')
  local jvm_xms=$(echo "scale=2; $total_memory * 0.2" | bc)
  local jvm_xmx=$(echo "scale=2; $total_memory * 0.6" | bc)

  echo "Recomendações para os parâmetros da JVM:"
  echo "JVM -Xms: ${jvm_xms}M"
  echo "JVM -Xmx: ${jvm_xmx}M"
}

# Função para exibir a quantidade total de memória e CPU disponíveis no host
display_host_resources() {
  local total_memory=$(free -m | awk '/^Mem:/{print $2}')
  local total_cpu=$(grep -c ^processor /proc/cpuinfo)

  echo "Recursos disponíveis no host:"
  echo "Total de memória: ${total_memory}M"
  echo "Total de CPU: ${total_cpu}"
}

# Executar a função e extrair os valores
result=$(query_prometheus)

# Exibir recomendações de limites
recommend_limits "$result"

echo

# Exibir recomendações para parâmetros da JVM
recommend_jvm_params

echo

# Exibir recursos disponíveis no host
display_host_resources
