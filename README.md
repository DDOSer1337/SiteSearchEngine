# SiteSearchEngine
Оглавление
- [Для чего нужна программа](#для-чего-нужна-программа)
- [Использование](#использование)
  -  [Требование](#требование)
  -  [Настройка конфига](#настройка-конфига)

- [Описание работы программы](#описание-работы-программы)
   - [Frontend](#frontend)
   - [Backend](#backend)

- [Ошибки](#ошибки)
  
- [Обратная связь](#обратная-связь)

- [Команда разработчиков](#команда-разработчиков)

- [Отдельная благодарность](#отдельная-благодарность)

## Для чего нужна программа

 - Это программа выполняет роль поиского движка на подобии яндекса\гугл, но для указанных в конфигурационном файле сайтов
 - Она обходит сайты для того чтобы сделать лемматизацию слов и после этого проводит индексацию, чтобы потом было проще искать данные по ключевым словам
 - Она может проиндексировать отдельную страницу, которая находится в пределах сайта, который был добавлен в бд при индексации 
 - Если появилась необходимость принудительной остановки индексации сайтов, то в программе предусмотренна кнопка остановки
 - Результат работы программы можно посмотреть в первой вкладке под названием Dashbord


## Использование

Для запуска программы требуется запустить ее в среде разработки и иметь стабильное интернет подключение, после чего будет запущен локальный сервер (http://localhost:8080) 
 перейдите на него и используйте, перед использованием без вношения изменений прочтите [Frontend](#frontend)

### Требование

Для установки и использования трубуется подключение к интернету

для работы требуется openhdk-17

Все зависимости и фреймворки находятся в pom.xml

Требуется настройка конфига в application.yaml

### Настройка конфига

в application.yaml в 
spring:
  DataSource:
  - url указываем ссылку на бд + добавляем настройки подключения
    ( у меня это ?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC )
  - username указываем имя пользователя бд
  - password пароль к бд

в 
 indexing-settings:
     sites:
     перечисляем ссылки и название сайта

   пример
   
 indexing-settings:
     sites:
     
     -url: https://www.lenta.ru
     name: lenta.ru    
     -url: https://www.playback.ru
     name: PlayBack.Ru    
     -url: https://www.skillbox.ru
     name: skillbox.ru

## Описание работы программы
     
### Frontend

 - DASHBOARD

    в этой вкладке показывается информация об сайтах (время сайта при начале индексации, страницы, леммы, ошибки если есть)  

 - MANAGEMENT
 
    тут находятся кнопка начала индексации и возможность проиндексировать отдельную ссылку, которая находится в пределах сайта

 - SEARCH

    тут происходит поиск слову на всех и на определенном сайте

### Backend

#### Config

тут создается лист с информациями о сайтах из конфига 

#### Controller

  -  ApiController
  
  основной контроллер, с которым работает сайт
  
  -  DefaulrController
  
  не используется но нужен



#### dto


- result

Тут находится ответы на респонс запросы где нужны только булевые ответы
  
- SearchByWord

 Тут находится ответы на респонс запрос на поиск слова
  
- Statistics

 Тут находится ответы на респонс запрос на получении статистики

#### model

- enum

  тут находятся константы для статусов сайтов

- Классы котороые находятся в этом пакете
  
    основные объекты для работы с базой данных

#### repositories

 репозитории для взаимодействия с базой данных


#### services

- QueryProcessing
  -   AddOrUpdatePage

      - PageIndexer

        В этом классе происходит индексация определенной ссылки
     
   -   LinkHandling
     
       -   LinkParser

           В  этом классе происходит основная работа программы по индексации

     
   - SiteParser
     
    В этом классе начинается индексация страницы
       
 - SearchByWord
   - SearchByWord
  
     тут создается тело ответа на поисковый запрос

     
 - LemmaCreator 

  В этом классе происходит создание и загрузка лемм в базу данных


 - Lucene

  тут происходит проверка слова на то является ли это слово словом и является ли это слово русским\английским а также не является ли это слово служеюной частью речи

- interface честно, не знаю зачем они нужны но они нужны для работы программы
  
 - IndexingImpl

   возращает ответ на запрос индексации всех сайтов из конфига

 - IndexPageImpl
 
   возвращает ответ на запрос индексации определенной ссылки в пределах сайтов из конфига

 - SearchEngineImpl
 
    возвращает ответ на запрос поиска слова 

 - StatisticsServiceImpl 
 
   возвращает ответ на запрос статистики

## Ошибки

в классе Lucene по какой-то мне неизвестной причине иногда может пройти проверка служебная часть речи 

## Обратная связь

gopesrayona@gmail.com

## Команда разработчиков

- Данил Ш. - Backend
- Неизвестный разработчик от skillbox - Frontend

## Отдельная благодарность

Отдельная благодарность за консультирование в создании проекта куратору skillbox Владиславу К.
