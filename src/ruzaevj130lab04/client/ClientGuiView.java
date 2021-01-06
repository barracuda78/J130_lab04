package ruzaevj130lab04.client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ruzaevj130lab04.ChatException;

public class ClientGuiView {
    private final ClientGuiController controller;

    private JFrame frame = new JFrame("ruzaev chat");
    private JTextField textField = new JTextField(50);
    private JTextArea messages = new JTextArea(10, 50);
    private JTextArea users = new JTextArea(10, 10);

    public ClientGuiView(ClientGuiController controller) {
        this.controller = controller;
        initView();
    }

    private void initView() {
        textField.setEditable(false);
        messages.setEditable(false);
        users.setEditable(false);

        frame.getContentPane().add(textField, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(messages), BorderLayout.WEST);
        frame.getContentPane().add(new JScrollPane(users), BorderLayout.EAST);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.sendTextMessage(textField.getText());
                } catch (ChatException ex) {
                    System.out.println("Не удалось получить сообщение. " + ex.getMessage());
                }
                textField.setText("");
            }
        });
}

    public String getServerAddress() {
        //если пользователь нажал escape или закрыл окно ввода адреса сервера - запросить еще раз и еще раз. В случае третьей неудачи выйти экстренно из программы.
        int counter = 0;
        while(true){
            String serverAddress = JOptionPane.showInputDialog(
                frame,
                "Введите адрес сервера:",
                "Конфигурация клиента",
                JOptionPane.QUESTION_MESSAGE);
            
                if(serverAddress == null){
                    counter++;

                    JOptionPane.showMessageDialog(
                        frame,
                        "Вы не ввели адрес сервера.\nПопробуйте localhost, если тестируете программу.",
                        "Конфигурация клиента",
                        JOptionPane.ERROR_MESSAGE);
                    
                    if (counter == 3){
                        JOptionPane.showMessageDialog(
                            frame,
                            "Вы так и не ввели корректный адрес сервера\nБудет выполнен выход из программы.",
                            "Конфигурация клиента",
                            JOptionPane.ERROR_MESSAGE);
                        System.exit(100);
                    }
                        
                }
            if(serverAddress == null)
                continue;
            return serverAddress;
        }

    }

    public int getServerPort() {
        int counter = 0;
        while (true) {
            String port = JOptionPane.showInputDialog(
                    frame,
                    "Введите порт сервера:",
                    "Конфигурация клиента",
                    JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                counter++;
                
                if(counter == 3){
                    JOptionPane.showMessageDialog(
                        frame,
                        "Вы так и не ввели корректный адрес порта.\n будет выполнен выход из программы.",
                        "Конфигурация клиента",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(100);
                }
                if(counter == 2){
                    JOptionPane.showMessageDialog(
                        frame,
                        "Если Вы - Игорь Владимирович - попробуйте порт 45678!!!",
                        "Конфигурация клиента",
                        JOptionPane.ERROR_MESSAGE);
                }                
                
                if(counter < 2){
                JOptionPane.showMessageDialog(
                        frame,
                        "Был введен некорректный порт сервера. Попробуйте еще раз.",
                        "Конфигурация клиента",
                        JOptionPane.ERROR_MESSAGE);                    
                }
            }
        }
    }

    public String getUserName() {
        return JOptionPane.showInputDialog(
                frame,
                "Введите ваше имя:",
                "Конфигурация клиента",
                JOptionPane.QUESTION_MESSAGE);
    }

    public void notifyConnectionStatusChanged(boolean clientConnected) {
        textField.setEditable(clientConnected);
        if (clientConnected) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Соединение с сервером установлено",
                    "Чат",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    frame,
                    "Клиент не подключен к серверу",
                    "Чат",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public void refreshMessages() {
        messages.append(controller.getModel().getNewMessage() + "\n");
    }

    public void refreshUsers() {
        ClientGuiModel model = controller.getModel();
        StringBuilder sb = new StringBuilder();
        for (String userName : model.getAllUserNames()) {
            sb.append(userName).append("\n");
        }
        users.setText(sb.toString());
    }
}
