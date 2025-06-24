#!/bin/bash
java -cp practica5.jar jade.MicroBoot -host [un_host] -port 8026 -agents "monitorJALS:es.ujaen.ssmmaa.curso2023_24.agentes.AgenteMonitor(configuracion.properties)"
