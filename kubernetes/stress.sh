# Faz requests infinitos para forçar o escalonamento
while true;
do
  clear;
  curl -s http://localhost:8080/ >/dev/null;
  echo "Request enviado...";
#  sleep 1;
done;
