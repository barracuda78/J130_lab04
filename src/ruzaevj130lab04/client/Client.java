package ruzaevj130lab04.client;

import java.io.IOException;
import java.net.Socket;
import ruzaevj130lab04.ChatException;
import ruzaevj130lab04.Connection;
import ruzaevj130lab04.ConsoleHelper;
import ruzaevj130lab04.Message;
import ruzaevj130lab04.MessageType;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    //точка входа - запуск консольного клиента:
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    public class SocketThread extends Thread{


        @Override
        public void run(){
            String serverAddress = getServerAddress();
            int serverPort = getServerPort();
                       
            try (Socket socket = new Socket(serverAddress, serverPort);
                 Connection connection = new Connection(socket)){
                Client.this.connection = connection;
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }

        }

        //void processIncomingMessage(String message) - должен выводить текст message в консоль.
        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);

        }

        //void informAboutAddingNewUser(String userName) - должен выводить в консоль информацию о том, что участник с именем userName присоединился к чату.
        protected void informAboutAddingNewUser(String userName){
                ConsoleHelper.writeMessage("участник с именем " + userName + " присоединился к чату.");
        }

        //void informAboutDeletingNewUser(String userName) - должен выводить в консоль, что участник с именем userName покинул чат.
        protected void informAboutDeletingNewUser(String userName){
            ConsoleHelper.writeMessage("участник с именем " + userName + " покинул чат.");
        }

        //4) void notifyConnectionStatusChanged(boolean clientConnected) - этот метод должен:
        //а) Устанавливать значение поля clientConnected внешнего объекта Client в соответствии с переданным параметром.
        //б) Оповещать (пробуждать ожидающий) основной поток класса Client.
        //
        //использую синхронизацию на уровне текущего объекта внешнего класса и метод notify().
        //Для класса SocketThread внешним классом является класс Client.
        protected void notifyConnectionStatusChanged(boolean clientConnected){
            synchronized (Client.this) {
                Client.this.clientConnected = clientConnected;
                Client.this.notify();
            }
        }

        //Этот метод будет представлять клиента серверу.
        //
        //Он должен:
        //а) В цикле получать сообщения, используя соединение connection.
        //б) Если тип полученного сообщения NAME_REQUEST (сервер запросил имя),
              // запросить ввод имени пользователя с помощью метода getUserName(),
              // создать новое сообщение с типом MessageType.USER_NAME и введенным именем,
              // отправить сообщение серверу.
        //в) Если тип полученного сообщения MessageType.NAME_ACCEPTED (сервер принял имя), значит сервер принял имя клиента, нужно об этом сообщить главному потоку.
        //Делаю это с помощью метода notifyConnectionStatusChanged(), передав в него true.
        //После этого выхожу из метода.
        //г) Если пришло сообщение с каким-либо другим типом, идаю исключение IOException("Unexpected MessageType").
        protected void clientHandshake() throws IOException, ClassNotFoundException{
            while(true){
                Message message = connection.receive();
                if(message.getType() == MessageType.NAME_REQUEST){
                    String name = getUserName();
                    Message messageToServer = new Message(MessageType.USER_NAME, name);
                    connection.send(messageToServer);
                } else if(message.getType() == MessageType.NAME_ACCEPTED){
                    notifyConnectionStatusChanged(true);
                    break;
                } else {
                    throw new IOException("Unexpected MessageType");
                }

            }
        }

        //Этот метод реализовывает главный цикл обработки сообщений сервера. Внутри метода:
        //а) получаю сообщение от сервера, используя соединение connection.
        //б) ---Если это текстовое сообщение (тип MessageType.TEXT), обрабатываю его с помощью метода processIncomingMessage().
        //в) ---Если это сообщение с типом MessageType.USER_ADDED, обрабатываю его с помощью метода informAboutAddingNewUser().
        //г) ---Если это сообщение с типом MessageType.USER_REMOVED, обрабатываю его с помощью метода informAboutDeletingNewUser().
        //д) Если клиент получил сообщение какого-либо другого типа, бросаю исключение IOException("Unexpected MessageType").
        //Бесконечный цикл будет завершен автоматически если произойдет ошибка (будет брошено исключение) или поток, в котором работает цикл, будет прерван.
        protected void clientMainLoop() throws IOException, ClassNotFoundException{
            while(true) {
                Message messageFromServer = connection.receive();
                if (messageFromServer.getType() == MessageType.TEXT) {
                    processIncomingMessage(messageFromServer.getData());
                } else if (messageFromServer.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(messageFromServer.getData());
                } else if (messageFromServer.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(messageFromServer.getData());
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }


    }

    //это не многопоточный run()!!!
    //run() создает вспомогательный поток SocketThread, ожидает пока тот установит соединение с сервером, а после этого в цикле считывает сообщения с консоли и отправляет их серверу.
    //Условие выхода из цикла --- отключение клиента или ввод пользователем команды 'exit'.
    //Для информирования главного потока, что соединение установлено во вспомогательном потоке, использую методы wait() и notify() объекта класса Client.
    //Реализация метода run :
    //---а) Создавать новый сокетный поток с помощью метода getSocketThread() - обхект внутреннего класса.
    //---2. Метод run() должен создавать и запускать новый поток, полученный с помощью метода getSocketThread().
    //---3. Поток созданный с помощью метода getSocketThread() -  отмечен как демон (setDaemon(true)).
    //г) Заставить текущий поток ожидать, пока он не получит нотификацию из другого потока.
    //4. После запуска нового socketThread метод run() должен ожидать до тех пор, пока не будет пробужден.
    //использую wait() и синхронизацию на уровне объекта.
    //Если во время ожидания возникает исключение, сообщаю об этом пользователю и выйхожу из программы.

    public void run(){
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                ConsoleHelper.writeMessage("во время ожидания возникла ошибка. Выход из программы.");
                System.exit(100);
                // e.printStackTrace();
            }
            //д) После того, как поток дождался нотификации, проверяю значение clientConnected.
            //Если оно true - вывожу "Соединение установлено.
            //Для выхода наберите команду 'exit'.".
            //Если оно false - вывожу "Произошла ошибка во время работы клиента.".
            if(clientConnected){
                ConsoleHelper.writeMessage("Соединение установлено.\n" +
                        "Для выхода наберите команду 'exit'.");
            } else{
                ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
            }
            //е) Считываю сообщения с консоли пока клиент подключен.
            //Если будет введена команда 'exit', то выйду из цикла.
            //ж) После каждого считывания, если метод shouldSendTextFromConsole() возвращает true, отправляю считанный текст с помощью метода sendTextMessage().
            while(clientConnected){
                String s = ConsoleHelper.readString();
                if(s.equals("exit"))
                    break;
                if(shouldSendTextFromConsole()){
                    try {
                        sendTextMessage(s);
                    } catch (ChatException ex) {
                        System.out.println("Сообщение не отправлено" + ex.getMessage());
                    }
                }
            }
        }
    }

        //Sметод getServerAddress() -  запрашивает ввод адреса сервера у пользователя и возвращает введенное значение.
        //Адрес может быть строкой, содержащей ip, если клиент и сервер запущен на разных машинах или 'localhost', если клиент и сервер работают на одной машине.

    protected String getServerAddress(){
        ConsoleHelper.writeMessage("введите адрес сервера");
        return ConsoleHelper.readString();
    }

    //2. int getServerPort() - должен запрашивать ввод порта сервера и возвращать его.
    protected int getServerPort(){
        ConsoleHelper.writeMessage("введите номер порта сервера");
        return ConsoleHelper.readInt();
    }

    //String getUserName() - должен запрашивать и возвращать имя пользователя.
    protected String getUserName(){
        ConsoleHelper.writeMessage("введите имя пользователя");
        return ConsoleHelper.readString();
    }

    //4. boolean shouldSendTextFromConsole() - в  всегда должен возвращать true (мы всегда отправляем текст введенный в консоль).
    protected boolean shouldSendTextFromConsole(){
        return true;
    }

    //SocketThread getSocketThread() - должен создавать и возвращать новый объект класса SocketThread.
    protected SocketThread getSocketThread(){
        return new SocketThread();
    }

    //6. void sendTextMessage(String text) - создает новое текстовое сообщение, используя переданный текст и отправляет его серверу через соединение connection.
    //Если во время отправки произошло исключение IOException, то необходимо вывести информацию об этом пользователю и присвоить false полю clientConnected.
    //5. Метод sendTextMessage() должен создавать и отправлять новое текстовое сообщение используя connection
    // и устанавливать флаг clientConnected в false, если во время отправки или создания сообщения возникло исключение IOException.
    protected void sendTextMessage(String text) throws ChatException{

        try {
            Message message = new Message(MessageType.TEXT, text);
            connection.send(message);
        } catch (IOException e) {
            clientConnected = false;
            throw new ChatException();
            //ConsoleHelper.writeMessage("во время отправки произошло исключение IOException");
            //e.printStackTrace();
        }
    }
}