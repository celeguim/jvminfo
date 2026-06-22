from prometheus_api_client import PrometheusConnect

# PROM_URL = "http://prometheus.monitoring.svc.cluster.local:9090"
PROM_URL = "http://localhost:9090"

prom = PrometheusConnect(url=PROM_URL, disable_ssl=True)


def query_value(query):
    result = prom.custom_query(query)
    if not result:
        return 0
    return round(float(result[0]["value"][1]), 2)


def get_rps():
    return query_value("""
    sum(
      rate(
        http_server_requests_seconds_count[5m]
      )
    )
    """)


def get_error_rate():
    return query_value("""
    (
      sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
      /
      sum(rate(http_server_requests_seconds_count[5m]))
    ) * 100
    """)


def get_cpu():
    return query_value("""
    sum(
      rate(
        container_cpu_usage_seconds_total[5m]
      )
    )
    """)


def get_memory():
    result = query_value("""
    sum(
      container_memory_working_set_bytes
    )
    """)
    return round(result / 1024 / 1024 / 1024, 2)


def get_p95():
    return query_value("""
    histogram_quantile(
      0.95,
      sum(
        rate(
          http_server_requests_seconds_bucket[5m]
        )
      )
    )
    """)


def get_metrics():
    return {
        "rps": get_rps(),
        "cpu": get_cpu(),
        "memory": get_memory(),
        "errors": get_error_rate(),
        "p95": get_p95(),
    }
