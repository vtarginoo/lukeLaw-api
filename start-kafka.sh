#!/bin/bash

# Iniciar o Zookeeper
echo "Iniciando Zookeeper..."
$KAFKA_HOME/bin/zookeeper-server-start.sh -daemon $KAFKA_HOME/config/zookeeper.properties
sleep 5  # Aguarda 5 segundos para garantir que o Zookeeper tenha iniciado

# Iniciar o Kafka
echo "Iniciando Kafka..."
$KAFKA_HOME/bin/kafka-server-start.sh -daemon $KAFKA_HOME/config/server.properties
sleep 10  # Aguarda 10 segundos para garantir que o Kafka tenha iniciado

echo "Kafka e Zookeeper foram iniciados."