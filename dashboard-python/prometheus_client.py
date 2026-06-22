from prometheus_api_client import PrometheusConnect

PROMETHEUS_URL = "http://localhost:9090"

prom = PrometheusConnect(url=PROMETHEUS_URL, disable_ssl=True)


def query_value(query):

    try:
        result = prom.custom_query(query)

        if not result:
            return 0

        return round(float(result[0]["value"][1]), 2)

    except Exception:
        return 0


def get_rps(namespace, app):

    query = f"""
    sum(
      rate(
        http_server_requests_seconds_count{{
          namespace="{namespace}",
          app="{app}"
        }}[5m]
      )
    )
    """

    return query_value(query)


def get_error_rate(namespace, app):

    query = f"""
    (
      sum(
        rate(
          http_server_requests_seconds_count{{
            namespace="{namespace}",
            app="{app}",
            status=~"5.."
          }}[5m]
        )
      )
      /
      sum(
        rate(
          http_server_requests_seconds_count{{
            namespace="{namespace}",
            app="{app}"
          }}[5m]
        )
      )
    ) * 100
    """

    return query_value(query)


def get_p95(namespace, app):

    query = f"""
    histogram_quantile(
      0.95,
      sum(
        rate(
          http_server_requests_seconds_bucket{{
            namespace="{namespace}",
            app="{app}"
          }}[5m]
        )
      ) by (le)
    )
    """

    return query_value(query)


def query_dict(query, group_label="app"):

    result = prom.custom_query(query)
    values = {}

    for row in result:
        metric = row.get("metric", {})
        key = metric.get(group_label)

        if not key:
            continue

        values[key] = round(float(row["value"][1]), 2)

    return values


def get_all_rps():

    query = """
    sum(
      rate(
        http_server_requests_seconds_count[5m]
      )
    ) by (namespace, app)
    """

    result = prom.custom_query(query)
    data = {}

    for row in result:
        metric = row["metric"]
        namespace = metric.get("namespace")
        app = metric.get("app")

        if not namespace or not app:
            continue

        key = f"{namespace}/{app}"
        data[key] = round(float(row["value"][1]), 2)

    return data


def get_all_errors():

    query = """
    (
      sum(
        rate(
          http_server_requests_seconds_count{
            status=~"5.."
          }[5m]
        )
      ) by (namespace, app)
      /
      sum(
        rate(
          http_server_requests_seconds_count[5m]
        )
      ) by (namespace, app)
    ) * 100
    """

    result = prom.custom_query(query)
    data = {}

    for row in result:

        namespace = row["metric"].get("namespace")
        app = row["metric"].get("app")

        if not namespace or not app:
            continue

        key = f"{namespace}/{app}"
        data[key] = round(float(row["value"][1]), 2)

    return data


def get_all_p95():

    query = """
    histogram_quantile(
      0.95,
      sum(
        rate(
          http_server_requests_seconds_bucket[5m]
        )
      ) by (namespace, app, le)
    )
    """

    result = prom.custom_query(query)
    data = {}

    for row in result:

        app = row["metric"].get("app")
        namespace = row["metric"].get("namespace")

        if not namespace or not app:
            continue
        if not app:
            continue

        key = f"{namespace}/{app}"
        data[key] = round(float(row["value"][1]) * 1000, 0)

    return data


def get_all_cpu():

    query = """
    100 *
    (
      sum(
        rate(
          container_cpu_usage_seconds_total{
            pod!=""
          }[5m]
        )
      ) by (pod)
    )
    """

    return prom.custom_query(query)


def query_app_map(query):

    result = prom.custom_query(query)
    data = {}

    for row in result:
        metric = row["metric"]
        app = metric.get("app")

        if not app:
            continue

        data[app] = round(float(row["value"][1]), 2)

    return data


def get_cpu_by_pod():

    result = prom.custom_query("""
    sum(
      rate(
        container_cpu_usage_seconds_total{
          pod!=""
        }[5m]
      )
    ) by (namespace,pod)
    """)

    data = {}

    for row in result:
        namespace = row["metric"].get("namespace")
        pod = row["metric"].get("pod")
        key = f"{namespace}/{pod}"
        data[key] = round(float(row["value"][1]) * 1000, 2)

    return data


def get_memory_by_pod():

    result = prom.custom_query("""
    sum(
      container_memory_working_set_bytes{
        pod!=""
      }
    ) by (namespace,pod)
    """)

    data = {}

    for row in result:
        namespace = row["metric"].get("namespace")
        pod = row["metric"].get("pod")
        key = f"{namespace}/{pod}"
        data[key] = round(float(row["value"][1]) / 1024 / 1024, 0)

    return data
