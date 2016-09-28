

	import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
	 
	/**
	 * Класс-клиент чат-сервера. Работает в консоли. Командой с консоли shutdown посылаем сервер в оффлайн
	 */
	public class ChatClient extends JPanel {
		
		String userString;
		String mch  = "";
		int j;
		String key ;
		final Socket s;  // это будет сокет для сервера
	    final BufferedReader socketReader; // буферизированный читатель с сервера
	    final BufferedWriter socketWriter; // буферизированный писатель на сервер
	    final BufferedReader userInput; // буферизированный читатель пользовательского ввода с консоли
	    /**
	     * Конструктор объекта клиента
	     * @param host - IP адрес или localhost или доменное имя
	     * @param port - порт, на котором висит сервер
	     * @throws java.io.IOException - если не смогли приконнектиться, кидается исключение, чтобы
	     * предотвратить создание объекта
	     */
	    public ChatClient(String host, int port) throws IOException {
	       
	    	s = new Socket(host, port); // создаем сокет
	        // создаем читателя и писателя в сокет с дефолной кодировкой UTF-8
	        socketReader = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
	        socketWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
	        // создаем читателя с консоли (от пользователя)
	        userInput = new BufferedReader(new InputStreamReader(System.in));
	        new Thread(new Receiver()).start();// создаем и запускаем нить асинхронного чтения из сокета
	    }
	 
	    /**
	     * метод, где происходит главный цикл чтения сообщений с консоли и отправки на сервер
	     */
	    public void run() {
 userString =null;

	    		
 System.out.println("You're using a viginer code.Enter the key");
	try {
		key = userInput.readLine();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	       
	        System.out.println("Type phrase(s) (hit Enter to exit):");
	        while (true) {
	            userString = null;
	            try {
	                userString = userInput.readLine(); // читаем строку от пользователя
	            } catch (IOException ignored) {} // с консоли эксепшена не может быть в принципе, игнорируем
	            //если что-то не так или пользователь просто нажал Enter...
	           
	            
	            if (userString == null || userString.length() == 0 || s.isClosed()) {
	                close(); // ...закрываем коннект.
	                break; // до этого break мы не дойдем, но стоит он, чтобы компилятор не ругался
	            } else { //...иначе...
	                try {
	                    shifr();
	                	socketWriter.write(mch); //пишем строку пользователя
	                    socketWriter.write("\n"); //добавляем "новою строку", дабы readLine() сервера сработал
	                    socketWriter.flush(); // отправляем
	                mch = "";
	                } catch (IOException e) {
	                    close(); // в любой ошибке - закрываем.
	                }
	            }
	        }
	    }
	 
	  
	    public void shifr(){
	    	
	    	
	    	int kascii[] = new int[key.length()];	
			
			for(int a = 0;a<key.length();a++){
				
				kascii[a] = (int)key.charAt(a); 
			}//перевод ключа в ascii код
			for (int c = 0; c <userString.length();c++){
				
				j = c%kascii.length;
				
				int shmessage = (userString.charAt(c)+ kascii[j]-97)%(26+96);
				if(shmessage<96){
					shmessage = shmessage+96;
				}
				
				mch =  mch + (char)( shmessage); 
				
				
				
				
			}
	    	
	    	
	    	
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    /**
	     * метод закрывает коннект и выходит из
	     * программы (это единственный  выход прервать работу BufferedReader.readLine(), на ожидании пользователя)
	     */
	    public synchronized void close() {//метод синхронизирован, чтобы исключить двойное закрытие.
	        if (!s.isClosed()) { // проверяем, что сокет не закрыт...
	            try {
	                s.close(); // закрываем...
	                System.exit(0); // выходим!
	            } catch (IOException ignored) {
	                ignored.printStackTrace();
	            }
	        }
	    }
	 
	    public static void main(String[] args)  { // входная точка программы
	    	
	    	JFrame jf1 = new JFrame();
	       JPanel jp1 = new JPanel();
	        jf1.add(jp1);
	        jf1.setBounds(200,0,900,600);
	       jf1.setVisible(true);
	    //	Stage s = new Stage();
	    	
	        
	        
	        
	    	try {
	            new ChatClient("localhost", 45000).run(); // Пробуем приконнетиться...
	        } catch (IOException e) { // если объект не создан...
	            System.out.println("Unable to connect. Server not running?"); // сообщаем...
	        }
	        
			//System.out.println(userString);
	    }
	 
	    /**
	     * Вложенный приватный класс асинхронного чтения
	     */
	    private class Receiver implements Runnable{
	        /**
	         * run() вызовется после запуска нити из конструктора клиента чата.
	         */
	        public void run() {
	            while (!s.isClosed()) { //сходу проверяем коннект.
	                String line = null;
	                try {
	                    line = socketReader.readLine(); // пробуем прочесть
	                } catch (IOException e) { // если в момент чтения ошибка, то...
	                    // проверим, что это не банальное штатное закрытие сокета сервером
	                    if ("Socket closed".equals(e.getMessage())) {
	                        break;
	                    }
	                    System.out.println("Connection lost"); // а сюда мы попадем в случае ошибок сети.
	                    close(); // ну и закрываем сокет (кстати, вызвается метод класса ChatClient, есть доступ)
	                }
	                if (line == null) {  // строка будет null если сервер прикрыл коннект по своей инициативе, сеть работает
	                    System.out.println("Server has closed connection");
	                    close(); // ...закрываемся
	                } else { // иначе печатаем то, что прислал сервер.
	                    System.out.println("Server:" + line );
	               // System.out.println("");
	                }
	            }
	        }
	    }

		
		

		
	}
	 

