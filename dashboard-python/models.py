from dataclasses import dataclass


@dataclass
class HPAInfo:

    namespace: str

    app: str

    hpa: str

    min_replicas: int
    current_replicas: int
    desired_replicas: int
    max_replicas: int

    cpu_target: float | None
    cpu_current: float

    memory_target: float | None
    memory_current: float

    rps: float
    rps_per_pod: float

    capacity: float

    p95: float
    error_rate: float

    utilization: float

    status: str
