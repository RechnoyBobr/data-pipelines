# 10 ДЗ

В папке собран полностью рабочий набор манифестов для задания:

- `WorkflowTemplate` с тремя переиспользуемыми модулями:
	- `ingest-validate`
	- `transform-rows`
	- `aggregate-publish`
- основной `Workflow`, который вызывает эти модули через `templateRef`
- `namespace` и `RBAC` для корректного запуска шагов

## Что делает pipeline

1. Генерирует тестовый CSV (`generate-source-data`)
2. Валидирует вход и отделяет невалидные строки (`ingest-validate`)
3. Выполняет трансформацию (`transform-rows`)
4. Агрегирует данные и формирует отчет (`aggregate-publish`)
5. Печатает сводку в логах (`print-summary`)

## Быстрый запуск

```bash
kubectl apply -k hw-10
kubectl -n argo get wf
kubectl -n argo wait --for=condition=Completed workflow/hw10-data-pipeline-run --timeout=300s
kubectl -n argo logs -l workflows.argoproj.io/workflow=hw10-data-pipeline-run -c main
```
