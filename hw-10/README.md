# 10 ДЗ

В папке собран полностью рабочий набор манифестов:

- `WorkflowTemplate` с тремя переиспользуемыми модулями:
	- `ingest-validate`
	- `transform-rows`
	- `aggregate-publish`
- основной `Workflow`, который вызывает эти модули через `templateRef`
- `namespace` и `RBAC` для корректного запуска шагов (RBAC файл для доступа к сервисному аккаунту). Возможно не пустит в сам Argo Workflow (учитывая, что уже пробросили порт `kubectl -n argo port-forward svc/hw10-data-pipeline-argo-workflows-server 2746:2746`) и возоможно у argocd сервера нужно поставить authMode: server (вместо клиентского), т.к. были проблемы с авторизацией по токену.

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
