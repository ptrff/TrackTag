<br/>
<p align="center">
  <a href="">
    <img src="https://i.ibb.co/C2MdwwV/ic13.png" alt="Logo" width="85" height="120">
  </a>
</p>

<h2 align="center"> TrackTag  </h2>

### 📃 О приложении

🤔 Кругу путешественников, которые посещают различные места по миру, захотелось отмечать на картах точки, где они побывали и делиться этими метками друг с другом.
✅ **TrackTag** позволяет путешественникам легче общаться и оценивать изысканность посещенных мест.


### 📖 Содержание

1. [О приложении](#-о-приложении)
2. [Скриншоты](#️-скриншоты)
3. [Функционал](#️-функционал)
4. [Технологический стэк](#️-технологический-стэк)
5. [Почему MVVM?](#-почему-mvvm)
6. [Почему Material You?](#-почему-material-you)
7. [Как установить?](#️-как-установить)

### 🖼️ Скриншоты

|<div align="center"> <a href=""><img src="https://i.ibb.co/ZVbgw8Y/b0a43da3-edba-4ff3-a2eb-98b39a4aab0b.jpg" alt='image' width='800'/></a> </div>|<div align="center"> <a href=""><img src="https://i.ibb.co/cx2pcX3/974eab97-0d2d-4708-8d13-0c58c3eff94c.jpg" alt='image' width='800'/></a> </div>|<div align="center"> <a href=""><img src="https://i.ibb.co/ZhxkW6x/87680587-be58-4925-81ba-74b41af3cec6.jpg" alt='image' width='800'/></a> </div> |
|--|--|--|
|<div align="center"> <a href=""><img src="https://i.ibb.co/y4NYsp6/5b2b5b05-a980-474f-826f-7eff433cdbf8.jpg" alt='image' width='800'/></a> </div>|<div align="center"> <a href=""><img src="https://i.ibb.co/FHMvxtG/498f3e06-ccf4-448e-bbf9-2ab22849d978.jpg" alt='image' width='800'/></a> </div>|<div align="center"> <a href=""><img src="https://i.ibb.co/Wf0DnG1/84d7e9ab-4fed-4985-af95-dbde15046939.jpg" alt='image' width='800'/></a> </div>|
|<div align="center"> <a href=""><img src="https://i.ibb.co/LSk9RLV/8ce15908-6787-440c-883a-2b4692b0b3c7.jpg" alt='image' width='800'/></a> </div>|<div align="center"> <a href=""><img src="https://i.ibb.co/Qphq4DH/0beea0e5-6959-448b-8ed0-a08fef3a86ad.jpg" alt='image' width='800'/></a> </div>|<div align="center"> <a href=""><img src="https://i.ibb.co/sjQ0htz/7f234f8b-a13c-40ea-866d-176cb067e25c.jpg" alt='image' width='800'/></a> </div>|

### ⚙️ Функционал

- **Весь**, требуемый по [ТЗ](https://github.com/RTUITLab/Recruit/blob/master/requirements/mobile/README.md#функционал-гость)
- **Страница настроек:** Частота уведомлений, изменение интерфейса, принудительная тёмная тема.
- **Анимированный интерфейс**
- **Жестовое управление**
- **Больше опций поиска**

### 🏗️ Технологический стэк

- **Single Activity:** Упрощение навигации, анимаций, меньше ресурсов.
- **MVVM:** о нём ниже.
- **Yandex Mapkit:** Работа с картами.
- **Retrofit2:** Взаимодействие с сетью и обработка HTTP-запросов.
- **Gson:** Удобная сериализация и десериализация JSON.
- **RxJava3:** Реактивное программирование.
- **Glide:** Асинхронная загрузка изображений.
- **Room:** Работа с локальной БД SQLite.
- **WorkManager:** Позволяет выполнять периодические задачи в фоне.
- **Navigation Component:** Модно и современно управляет фрагментами.
-  **Java**
-  **ViewBinding**

### 🧐 Почему MVVM?

-   **Отделение бизнес-логики от UI:** Чёткое разделение между данными и их отображением. Это модульность (а, следовательно, и переиспользуемость) кода и удобство поддержки и расширения.
-   **Лайвдата:** Это реактивный подход. Удобно.
-   **Живёт обособленно от View:** Предотвращает утечку памяти, т.к. не уничтожается и не пересоздаётся вместе с view.
-   **Двустороннее связывание:** ViewModel и View легко связать в две стороны, чего и требует архитектура.

### 🎨 Почему Material You?

- **Согласованность:** Являясь рекомендуемой Google дизайн-системой, обеспечивает согласованность интерфейса на различных устройствах и платформах.
- **Современный дизайн:** Material You в тренде.
- **Официальная поддержка:** В настоящий момент именно Material You получает поддержку и обновления от Google.


### 🛠️ Как установить?
1) Клонируйте репозиторий
2) Откройте его в Android Studio
3) Проведите Gradle Sync
3) Вставьте свой Yandex MapKit api ключ в файл ``local.properties`` следующим образом:
```  
# other code  
MAPKIT_API_KEY = 2ccdc51a-d356-7459-as8c-t432023b785a #your api key here  
```  
4) Соберите приложение и запустите