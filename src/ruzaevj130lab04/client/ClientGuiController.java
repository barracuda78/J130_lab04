package ruzaevj130lab04.client;


//компонент контроллер (Controller):
//1. класс ClientGuiController унаследованный от Client.
public class ClientGuiController extends Client {
    
    //точка входа - запуск оконного клиента: запускаеьтся так же из класса SimpleChat.
    public static void main(String[] args) {
        new ClientGuiController().run();
    }

    //2.  поле, отвечающее за модель ClientGuiModel model.
    private ClientGuiModel model = new ClientGuiModel();

    //3.  поле, отвечающее за представление ClientGuiView view.
    private ClientGuiView view = new ClientGuiView(this);

    //4.  внутренний класс GuiSocketThread унаследованный от SocketThread.
    public class GuiSocketThread extends SocketThread{
        //переопределил следующие методы:
        //а) void processIncomingMessage(String message) - должен устанавливать новое сообщение у модели и вызывать обновление вывода сообщений у представления.
        @Override
        protected void processIncomingMessage(String message){
            model.setNewMessage(message);
            view.refreshMessages();
        }
        //б) void informAboutAddingNewUser(String userName) - должен добавлять нового пользователя в модель и вызывать обновление вывода пользователей у отображения.
        protected void informAboutAddingNewUser(String userName){
            model.addUser(userName);
            view.refreshUsers();
        }

        //в) void informAboutDeletingNewUser(String userName) - должен удалять пользователя из модели и вызывать обновление вывода пользователей у отображения (View).
        protected void informAboutDeletingNewUser(String userName){
            model.deleteUser(userName);
            view.refreshUsers();
        }
        //г) void notifyConnectionStatusChanged(boolean clientConnected) - должен вызывать аналогичный метод у представления (Model).
        protected void notifyConnectionStatusChanged(boolean clientConnected){
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }

    //5. Переопределил методы в классе ClientGuiController:
    //а) SocketThread getSocketThread() - должен создавать и возвращать объект типа GuiSocketThread.
    @Override
    protected SocketThread getSocketThread(){
        return new GuiSocketThread();
    }

    //б) void run() - должен получать объект SocketThread через метод getSocketThread() и вызывать у него метод run().
    //лнет необходимости вызывать метод run() в отдельном потоке, как  для консольного клиента.
    //8. Метод run() в классе ClientGuiController должен получать объект SocketThread через метод getSocketThread() и вызывать у него метод run().
    public void run(){
        getSocketThread().run();
    }

    //в) getServerAddress(), getServerPort(), getUserName().
    //Они вызывают одноименные методы из представления (view).
    protected String getServerAddress(){
        return view.getServerAddress();
    }

    protected int getServerPort(){
        return view.getServerPort();
    }

    protected String getUserName(){
        return view.getUserName();
    }

    //6.  метод ClientGuiModel getModel(), который должен возвращать модель.
    public ClientGuiModel getModel(){
        return model;
    }
}