import asyncio
import flet as ft

from k8s_client import get_contexts, get_hpas

REFRESH_SECONDS = 30


def metric_card(title, value):

    return ft.Card(
        content=ft.Container(
            content=ft.Column(
                [
                    ft.Text(title),
                    ft.Text(str(value), size=24, weight=ft.FontWeight.BOLD),
                ]
            ),
            padding=10,
            width=170,
        )
    )


def main(page: ft.Page):

    page.title = "K8s Dashboard"
    page.theme_mode = ft.ThemeMode.DARK
    page.window_width = 1800
    page.window_height = 1000

    contexts = get_contexts()

    cluster = ft.Dropdown(
        label="Cluster",
        value=contexts["active"],
        width=500,
        options=[ft.dropdown.Option(x) for x in contexts["contexts"]],
    )

    cards = ft.Row()

    table = ft.DataTable(
        columns=[
            ft.DataColumn(ft.Text("Namespace")),
            ft.DataColumn(ft.Text("App")),
            ft.DataColumn(ft.Text("HPA")),
            ft.DataColumn(ft.Text("Min")),
            ft.DataColumn(ft.Text("Current")),
            ft.DataColumn(ft.Text("Desired")),
            ft.DataColumn(ft.Text("Max")),
            ft.DataColumn(ft.Text("CPU Target")),
            ft.DataColumn(ft.Text("Mem Target")),
            ft.DataColumn(ft.Text("RPS")),
            ft.DataColumn(ft.Text("RPS/Pod")),
            ft.DataColumn(ft.Text("P95")),
            ft.DataColumn(ft.Text("Errors %")),
            ft.DataColumn(ft.Text("Status")),
        ],
        rows=[],
    )

    async def refresh():

        while True:

            try:

                hpas = get_hpas(cluster.value)

                total_rps = round(sum(x.rps for x in hpas), 2)

                avg_error = round(
                    sum(x.error_rate for x in hpas) / max(len(hpas), 1), 2
                )

                avg_p95 = round(sum(x.p95 for x in hpas) / max(len(hpas), 1), 2)

                cards.controls = [
                    metric_card("HPAs", len(hpas)),
                    metric_card("RPS", total_rps),
                    metric_card("Avg Error %", avg_error),
                    metric_card("Avg P95", avg_p95),
                ]

                table.rows.clear()

                for item in hpas:

                    table.rows.append(
                        ft.DataRow(
                            cells=[
                                ft.DataCell(ft.Text(item.namespace)),
                                ft.DataCell(ft.Text(item.app)),
                                ft.DataCell(ft.Text(item.hpa)),
                                ft.DataCell(ft.Text(str(item.min_replicas))),
                                ft.DataCell(ft.Text(str(item.current_replicas))),
                                ft.DataCell(ft.Text(str(item.desired_replicas))),
                                ft.DataCell(ft.Text(str(item.max_replicas))),
                                ft.DataCell(ft.Text(item.cpu_target)),
                                ft.DataCell(ft.Text(item.memory_target)),
                                ft.DataCell(ft.Text(str(item.rps))),
                                ft.DataCell(ft.Text(str(item.rps_per_pod))),
                                ft.DataCell(ft.Text(str(item.p95))),
                                ft.DataCell(ft.Text(str(item.error_rate))),
                                ft.DataCell(ft.Text(item.status)),
                            ]
                        )
                    )

                page.update()

            except Exception as e:

                print(e)

            await asyncio.sleep(REFRESH_SECONDS)

    page.add(
        ft.Column(
            [
                ft.Text("Kubernetes Operations Dashboard", size=28),
                cluster,
                cards,
                ft.Divider(),
                ft.Container(
                    content=ft.Column([table], scroll=ft.ScrollMode.AUTO), expand=True
                ),
            ],
            expand=True,
        )
    )

    page.run_task(refresh)


if __name__ == "__main__":
    ft.run(main=main, view=ft.AppView.WEB_BROWSER)
