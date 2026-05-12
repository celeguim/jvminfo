# Faz requests infinitos para forçar o escalonamento
while true;
do
  clear;
  curl -s http://localhost:8080/ >/dev/null;
  echo "Request enviado...";
  DATA=$(date +%Y-%m-%d_%H-%M-%S)
  echo $DATA
  sleep 1;
done;
