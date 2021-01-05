package ruzaevj130lab04;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import ruzaevj130lab04.client.ClientGuiController;


/**
 *
 * @author ENVY
 */
public class SimpleChat implements ISimpleChat {

    //поля для сервера.
    private static StringBuffer sb = new StringBuffer();
    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    
//поля для клента:
    private String inetAddress;
    private int serverPort;
    
     /**
     * Запуск приложения в режиме клиента.
     *
     * @throws ChatException выбрасывается в случае общей ошибки в работе
     * приложения, например, в случае невозможности открыть соединение с
     * сервером.
     */
    @Override
    public void client() throws ChatException {
        
        new ClientGuiController().run(); // <--- запуск клиента в отдельном потоке ( в классе Client - есть внутренний (нестатический) класс SocketThread extends Thread
                                         // <--- а ClientGuiController extends Client.
                
        //закомментировал так как выношу логику клиента в отдкльный пакет client для удобства вместе с графическим интерфейсом клиента.:
        
        // Client.java - консольный клиент, - точка входа есть в этом классе Client.java 
        // ClientGuiView - оконный клиент (паттерн MVC) - точка входа есть в этом классе ClientGuiView.
        
        //        serverPort = ISimpleChat.SERVER_PORT;
        //        try {
        //            Socket s = new Socket(InetAddress.getByName(inetAddress), serverPort);
        //            OutputStream outputStream = s.getOutputStream();
        //            InputStream inputStream = s.getInputStream();
        //
        //            while (true) {
        //                String message = ConsoleHelper.readString();  // <---этот метод ждет ввода строки с клавиатуры. Реализоват в отдельном классе ConsoleHelper;
        //                if("exit".equals(message)){
        //                    break;
        //                }
        //                byte[] messageBytes = message.getBytes();
        //                outputStream.write(messageBytes);
        //            }
        //
        //        } catch (UnknownHostException ue) {
        //            System.out.println("Сервер не найден. Проверьте адрес." + ue.getMessage());
        //            throw new ChatException();
        //        } catch (IOException ie) {
        //            System.out.println("Ошибка ввода-вывода." + ie.getMessage());
        //            throw new ChatException();
        //        }
    }

     /**
     * @throws ChatException выбрасывается в случае общей ошибки в работе
     * приложения, например, в случае невозможности занять стандартный порт
     * сервера.
     */
    @Override
    public void server() throws ChatException {

        //закомментировал так как вынес логику сервера в отдкльный класс Server для удобства:

        //        try {
        //            sb = new StringBuffer();
        //            ServerSocket serverSocket = new ServerSocket(ISimpleChat.SERVER_PORT);
        //            clientSocket = serverSocket.accept();
        //            inputStream = clientSocket.getInputStream();
        //            outputStream = clientSocket.getOutputStream();
        //            byte[] buffer = new byte[ISimpleChat.BUFFER_SIZE];
        //            int n = 0;
        //            while(true){
        //                n = inputStream.read(buffer);
        //                //if (n < 0)                      // <---нет условия выхода, т.к. сервер работает вечно.
        //                //    break;
        //                outputStream.write(buffer, 0, n);
        //                String s = new String(buffer);
        //                sb.append(s);
        //            }
        //        } catch (IOException ex) {
        //            System.out.println("ServerSocket не создан или ошибка ввода/вывода. " + ex.getMessage());
        //            throw new ChatException();
        //        }

        try (ServerSocket serverSocket = new ServerSocket(ISimpleChat.SERVER_PORT)) {
            ConsoleHelper.writeMessage("сервер запущен");
            while(true){
                //запуск сервера в отдельном потоке для данного клиента (внутренний класс Server.Handler является Thread, в конструкторе принимает Socket) :
                new Server.Handler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Ошибка воссоздания сокета на сервере: " + e.getMessage());
            throw new ChatException();
        }
    }

     /**
     * Метод возвращает принятое сообщение.
     *
     * @return @throws ChatException выбрасывается в случае общей ошибки в
     * работе приложения.
     */
    @Override
    public String getMessage() throws ChatException {
        return sb.toString();
    }

    /**
     * Метод отправляет сообщение.
     *
     * @param message отправляемое сообщение.
     * @throws ChatException выбрасывается в случае общей ошибки в работе
     * приложения.
     */    
    @Override
    public void sendMessage(String message) throws ChatException {
        
    }

    @Override
    public void close() throws ChatException {
        try{
        outputStream.close();
        inputStream.close();
        clientSocket.close();
        }catch(IOException e){
            System.out.println("Ошибка закрытия сокета или потоков IO: " + e.getMessage());
            throw new ChatException("Не удалось закрыть потоки или сокет");
        }
    }
    
    //Проверка работоспособности программы:
    public static void main(String[] args) {
        SimpleChat SimpleChat = new SimpleChat();

        //запуск клиента (работает в отдельном потоке):
        //SimpleChat.client();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ClientGuiController.main(null);
            }
        }).start();

        //запуск сервера (работает в отдельном потоке):
        try {
            SimpleChat.server();
        } catch (ChatException ex) {
            System.out.println("Ошибка запуска сервера" + ex.getMessage());
        }
    }
}
