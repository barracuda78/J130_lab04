package ruzaevj130lab04;

//Енам для типов сообщений - чтоюы понимать, что длать в каком случае: является полем объекта класса Message (композиция)
public enum MessageType {
    NAME_REQUEST, //для запроса имени  у пользователя
    USER_NAME,    //передача введенного пользователем имени серверу
    NAME_ACCEPTED, //сервер принял имя.
    TEXT,          //сообщения пользователей - основной тип сообщений.
    USER_ADDED,    // когда инфомируем всех участников чата, что новый пользователь пришел.
    USER_REMOVED   // когда пользователь вышел - тоже всем сообщаю.
}