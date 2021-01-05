package ruzaevj130lab04;

/*
 * Курс DEV-J130. Задание №4. Основы многопоточного программирования.
 */

/**
 ** Простой чат для двух собеседников. Реализация этого интерфейса должна
 * отличаться следующими особенностями:
 * <ol>
 * <li>класс реализации может работать в режиме сервера или клиента (режим
 * работы задаётся пользователем при старте приложения);</li>
 * <li>входящие сообщения, исходящие сообщения работают независимо друг от друга
 * в разных потоках;</li>
 * <li>при выходе из сеанса одной из сторон, второй стороне обязательно
 * передаётся соответствующее сообщение.</li>
 * </ol>
 *
 * Приложение может быть реализовано на основе использования стеков протоколов
 * UDP/IP или TCP/IP (по выбору программиста).
 *
 * @author (C)Y.D.Zakovryashin, 01.12.2020
 */
public interface ISimpleChat extends AutoCloseable {

    /**
     * Стандартный порт, на котором принимается сообщения сервером.
     */
    public static final int SERVER_PORT = 45678;
    /**
     * Стандартный размер буфера обмена.
     */
    public static final int BUFFER_SIZE = 4096;

    /**
     * Запуск приложения в режиме клиента.
     *
     * @throws ChatException выбрасывается в случае общей ошибки в работе
     * приложения, например, в случае невозможности открыть соединение с
     * сервером.
     */
    void client() throws ChatException;

    /**
     *
     * @throws ChatException выбрасывается в случае общей ошибки в работе
     * приложения, например, в случае невозможности занять стандартный порт
     * сервера.
     */
    void server() throws ChatException;

    /**
     * Метод возвращает принятое сообщение.
     *
     * @return @throws ChatException выбрасывается в случае общей ошибки в
     * работе приложения.
     */
    String getMessage() throws ChatException;

    /**
     * Метод отправляет сообщение.
     *
     * @param message отправляемое сообщение.
     * @throws ChatException выбрасывается в случае общей ошибки в работе
     * приложения.
     */
    void sendMessage(String message) throws ChatException;

    /**
     * Метод закрывает открытые сокеты.
     *
     * @throws ChatException выбрасывается в случае общей ошибки в работе
     * приложения.
     */
    @Override
    public void close() throws ChatException;
}