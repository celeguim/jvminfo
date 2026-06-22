from kubernetes import client, config
from kubernetes.config import list_kube_config_contexts


def get_contexts():
    contexts, active = list_kube_config_contexts()
    return {"active": active["name"], "contexts": [c["name"] for c in contexts]}


def load_context(context_name):
    config.load_kube_config(context=context_name)
    return {"core": client.CoreV1Api(), "autoscaling": client.AutoscalingV2Api()}


def get_hpas(context_name):
    config.load_kube_config(context=context_name)

    api = client.AutoscalingV2Api()
    hpas = api.list_horizontal_pod_autoscaler_for_all_namespaces()

    result = []

    for hpa in hpas.items:
        cpu = "-"
        memory = "-"

        if hpa.spec.metrics:

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

                if resource.name == "cpu":
                    cpu_current = current.average_utilization
                    cpu_current_value = current.average_value

                elif resource.name == "memory":
                    memory_current = current.average_utilization
                    memory_current_value = current.average_value

                if metric.type == "Resource":
                    if metric.resource.name == "cpu":
                        cpu = metric.resource.target.average_utilization

                    if metric.resource.name == "memory":
                        memory = metric.resource.target.average_utilization

        result.append(
            {
                "namespace": hpa.metadata.namespace,
                "name": hpa.metadata.name,
                "target": hpa.spec.scale_target_ref.name,
                "min": hpa.spec.min_replicas,
                "max": hpa.spec.max_replicas,
                "current": hpa.status.current_replicas,
                "desired": hpa.status.desired_replicas,
                "cpu": cpu,
                "memory": memory,
            }
        )

    return result
