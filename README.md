# Приложение "Заметки"
## О приложении
 
Является учебным проектом по курсу "Android-разработчик" от Нетологии. 
**Реализовано:**
* Внедрение зависимостей для хранения заметок и PIN-кодов
* Шифрование пароля с солью. Хэш и соль хранятся в SharedPreferences.  
* Возможность смена языка - русский и английский
* Хранение заметок в внутренних файлах. 
* Стили текста по Material Design.

Приложение состоит из четырёх экранов:
1. Экран ввода пин кода
1. Экран со списком заметок
1. Экран создания новой заметки
1. Экран с настройками

## Экран ввода Пин кода
<img src="https://i.imgur.com/CPoWekL.png" width="300">

**Экран включает:**
* Placeholder'ы, выполненные в виде shape фигур и отображающие сколько цифр уже введено;
* Цифровая клавиатура + кнопка удалить;
* Текстовое поле.
* Контейнер - ConstraintLayout

**Поведение:**
* При нажатии на клавиатуру загорается дополнительный кружочек, в порядке слева направо;
* При нажатии на клавишу "Удалить" соответствующий кружок гаснет;
* При нажатии на клавиатуру появляется анимация нажатия;
* При введении 4 цифр происходит проверка пин-кода с хранилищем, если пин-код не верный – отображается Toast сообщение, а неверно введённый пароль сбрасывается;
* Если пин-код не был задан (первый запуск приложения) – осуществляется переход на экран с настройками.

## Экран со списком заметок
<img src="https://i.imgur.com/6nwGwEt.png" width="300"> <img src="https://i.imgur.com/8rJY1Z8.png" width="300">

**Экран состоит из:**
* Списка заметок в виде карточек (ListView со своим адаптером + CardView);
* Кнопки добавить новую заметку (FAB);
* Кнопки настроек (OptionsMenu).
* Контейнер – `FrameLayout`

Поведение:
* Карточка состоит из 3 полей, если в заметку не внесены данные для этого поля, оно не отображается (и не занимает место);
* В заголовке заметки отображается максимум 1 строка текста, иначе обрывается и знак `…`;
* В теле заметки отображается максимум 3 строчки, иначе обрывается и знак `…`;
* Для разных полей используются разные стили Material Design;
* При нажатии на кнопку добавить открывается пустое окно редактирования;
* При нажатии на элемент списка открывается окно редактирования этой заметки;
* При долгом нажатии на элемент появляется диалог подтверждения удаления.
* При двойном нажатии на кнопку "Назад" - выход из приложения, при одинарном - Toast сообщение. 
* Заметки сортируются по следующему принципу:
    1. Заметки сортируются по дате дедлайна: чем ближе срок истечения, тем выше заметка в списке (просроченные заметки оказываются в самом верху). 
    1. Если дедлайны совпали или заметка не имеет дедлайна, тогда сортировка происходит по дате последнего изменения (новые или отредактированные оказываются выше).
    1. Любая заметка с дедлайном всегда выше заметки без дедлайна.

## Экран с настройками
<img src="https://i.imgur.com/dCOwfoz.png" width="300"> <img src="https://i.imgur.com/4amct8o.png" width="300">

**Экран состоит из:**
* Два текстовых поля;
* Поля ввода пин-кода;
* Кнопка показать/скрыть пин-код;
* Две кнопки "Сохранить";
* Элемент spinner для выбора языка (со своим адаптером).
* Контейнер - ConstraintLayout

**Поведение:**
* Поле ввода Пин кода позволяет задать максимум 4 цифры;
* Когда текстовое поле пустое, отображается hint с примером пароля;
* При вводе используется системная клавиатура из цифр;
* По умолчанию введенные цифры скрываются кружочками;
* При нажатии на кнопку пин-код отображается/скрывается;
* При нажатии на сохранить под полем ввода, приложение запоминает новый пин-код;
* При нажатии на кнопку сохранить под спиннером, меняется локализация на ту, что выбрана на спинере. При перезапуске приложения локализация сохраняется.

## Экран создания новой заметки
<img src="https://i.imgur.com/moLfa5C.png" width="300"> <img src="https://i.imgur.com/o8kFwV2.png" width="300">
<img src="https://i.imgur.com/Rkc4yXY.png" width="300"> <img src="https://i.imgur.com/YRPrw3F.png" width="300">

**Экран состоит из:**
* Поля ввода заголовка заметки (тип text);
* Поля ввода тела заметки (тип textMultiline);
* Кнопки сохранить (OptionsMenu);
* Кнопки назад;
* Checkbox'a есть ли дедлайн у заметки;
* Полем ручного ввода дедлайна;
* Кнопкой выбора даты и времени (диалоги).
* Контейнер - ConstraintLayout

**Поведение:**
* Это окно может открываться как для новой заметки, так и для редактирования существующей;
* Если поле пустое, то отображается hint с подсказкой, для чего это поле;
* По умолчанию поле дедлайна и кнопка выбора даты недоступна - нужно поставить галочку checkbox'a;
* При снятии галочки дедлайна, поле дедлайн очищается;
* При нажатии на кнопку выбора даты открывается стандартный диалог выбора даты из календаря, а затем и диалог для времени.
