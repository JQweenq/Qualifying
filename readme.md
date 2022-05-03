# Получить символы акций в зависимости от биржи

```kotlin
apiClient.stockSymbols("<Код биржи>", "", "", "")
```
[see](https://docs.google.com/spreadsheets/d/1I3pBxjfXB056-g_JYf_6o3Rns3BV2kMGG1nCatb91ls/edit#gid=0)

# Получить цену акции

```kotlin
apiClient.quote("<Символ акции>")
```

# TODO

- Закинуть в RecyclerView список символов
- Загружать цены из символов
