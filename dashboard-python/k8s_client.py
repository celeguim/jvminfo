from pprint import pprint

from kubernetes import client, config
from kubernetes.config import list_kube_config_contexts

from models import HPAInfo

from prometheus_client import (
    get_all_rps,
    get_all_errors,
    get_all_p95,
    get_cpu_by_pod,
    get_memory_by_pod,
)


def format_memory_value(value):

    if not value:
        return "-"

    try:
        if value.endswith("m"):
            bytes_value = float(value[:-1]) / 1000
            mib = bytes_value / 1024 / 1024
            return f"{mib:.1f} Mi"
        return value

    except Exception:
        return value


def get_contexts():
    contexts, active = list_kube_config_contexts()
    return {"active": active["name"], "contexts": [c["name"] for c in contexts]}


def load_context(context_name):
    config.load_kube_config(context=context_name)


def get_namespaces(context_name):

    load_context(context_name)

    core = client.CoreV1Api()

    namespaces = core.list_namespace()

    return sorted([ns.metadata.name for ns in namespaces.items])


def resolve_app_name(deployment):

    labels = (deployment.spec.template.metadata.labels) or {}

    for key in [
        "app",
        "app.kubernetes.io/name",
        "application",
    ]:

        value = labels.get(key)

        if value:
            return value

    return deployment.metadata.name


def get_hpas(context_name, namespace_filter=None):

    load_context(context_name)

    core = client.CoreV1Api()
    apps = client.AppsV1Api()
    autoscaling = client.AutoscalingV2Api()

    #
    # Prometheus
    #
    rps_map = get_all_rps()
    error_map = get_all_errors()
    p95_map = get_all_p95()
    cpu_map = get_cpu_by_pod()
    memory_map = get_memory_by_pod()

    print("RPS MAP")
    print(rps_map)

    #
    # HPAs
    #
    hpas = autoscaling.list_horizontal_pod_autoscaler_for_all_namespaces()

    result = []

    for hpa in hpas.items:
        namespace = hpa.metadata.namespace

        # pprint(hpa.status.to_dict())

        if (
            namespace_filter
            and namespace_filter != "All"
            and namespace != namespace_filter
        ):
            continue

        deployment_name = hpa.spec.scale_target_ref.name

        try:
            deployment = apps.read_namespaced_deployment(deployment_name, namespace)
            app = resolve_app_name(deployment)

            print(f"""
                deployment={deployment_name}
                app={app}
                rps={rps_map.get(app)}
                """)

            if app == deployment_name:
                app = deployment_name.replace(f"{namespace}-", "").replace(
                    "-deployment", ""
                )

        except Exception:
            app = deployment_name

        #
        # Targets
        #
        cpu_target = None
        memory_target = None

        metrics = hpa.spec.metrics or []

        for metric in metrics:

            if metric.type != "Resource":
                continue

            resource = metric.resource

            if not resource:
                continue

            if resource.name == "cpu":
                cpu_target = resource.target.average_utilization

            elif resource.name == "memory":
                memory_target = resource.target.average_utilization

        #
        # Current Metrics
        #
        cpu_current = None
        cpu_current_value = None

        memory_current = None
        memory_current_value = None

        current_metrics = (
            hpa.status.current_metrics if hpa.status.current_metrics else []
        )

        for metric in current_metrics:

            if metric.type != "Resource":
                continue

            resource = metric.resource

            if not resource:
                continue

            current = resource.current

            if not current:
                continue

            if resource.name == "cpu":
                print("cpu", current)
                cpu_current = current.average_utilization
                cpu_current_value = current.average_value

            elif resource.name == "memory":
                print("memory", current)
                memory_current = current.average_utilization
                memory_current_value = current.average_value

        #
        # GAPS
        #
        cpu_gap = None

        if cpu_target is not None and cpu_current is not None:
            cpu_gap = round(cpu_current - cpu_target, 1)

        memory_gap = None

        if memory_target is not None and memory_current is not None:
            memory_gap = round(memory_current - memory_target, 1)

        #
        # Replicas
        #
        current = hpa.status.current_replicas or 0
        desired = hpa.status.desired_replicas or 0
        minimum = hpa.spec.min_replicas or 1
        maximum = hpa.spec.max_replicas or 1

        #
        # Prometheus
        #
        key = f"{namespace}/{app}"
        rps = rps_map.get(key, 0)

        error_rate = error_map.get(key, 0)
        p95 = p95_map.get(key, 0)

        #
        # Pods do Deployment
        #
        cpu_total = 0
        memory_total = 0
        pod_count = 0

        try:
            pods = core.list_namespaced_pod(
                namespace=namespace, label_selector=f"app={app}"
            )

            for pod in pods.items:
                pod_name = pod.metadata.name
                key = f"{namespace}/{pod_name}"
                cpu_total += cpu_map.get(key, 0)
                memory_total += memory_map.get(key, 0)
                pod_count += 1

        except Exception:
            pass

        #
        # CPU Médio por Pod
        #
        # cpu_current = round(cpu_total / max(pod_count, 1), 2)

        #
        # Memory Média por Pod
        #
        # memory_current = round(memory_total / max(pod_count, 1), 2)

        #
        # RPS por Pod
        #
        rps_per_pod = round(rps / max(current, 1), 2)

        #
        # Capacity
        #
        capacity = round(rps_per_pod * maximum, 2)

        #
        # Utilização do HPA
        #
        utilization = round((current / max(maximum, 1)) * 100, 1)

        #
        # Status
        #
        if current >= maximum:
            status = "🔴 MAX"

        elif (
            cpu_current is not None
            and cpu_target is not None
            and cpu_current > cpu_target
        ):
            status = "🔴 CPU"

        elif (
            memory_current is not None
            and memory_target is not None
            and memory_current > memory_target
        ):
            status = "🔴 MEM"

        elif error_rate > 1:
            status = "🔴 ERR"

        elif p95 > 500:
            status = "🟡 LAT"

        elif utilization > 80:
            status = "🟡 HIGH"

        else:
            status = "🟢 OK"

        result.append(
            HPAInfo(
                namespace=namespace,
                app=app,
                hpa=hpa.metadata.name,
                min_replicas=minimum,
                current_replicas=current,
                desired_replicas=desired,
                max_replicas=maximum,
                cpu_target=cpu_target,
                cpu_current=cpu_current,
                cpu_current_value=cpu_current_value,
                memory_target=memory_target,
                memory_current=memory_current,
                memory_current_value=format_memory_value(memory_current_value),
                cpu_gap=cpu_gap,
                memory_gap=memory_gap,
                rps=rps,
                rps_per_pod=rps_per_pod,
                capacity=capacity,
                p95=p95,
                error_rate=error_rate,
                utilization=utilization,
                status=status,
            )
        )

    result.sort(key=lambda x: x.rps, reverse=True)

    return result
