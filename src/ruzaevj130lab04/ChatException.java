package ruzaevj130lab04;

/**
 * Класс, представляющий основное исключение для простого чата.
 *
 * @author (C)Y.D.Zakovryashin, 01.12.2020
 */
class ChatException extends Exception {

    public ChatException() {
    }

    public ChatException(String string) {
        super(string);
    }

}
