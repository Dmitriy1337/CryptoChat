

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
	 * �����-������ ���-�������. �������� � �������. �������� � ������� shutdown �������� ������ � �������
	 */
	public class ChatClient extends JPanel {
		
		String userString;
		String mch  = "";
		int j;
		String key ;
		final Socket s;  // ��� ����� ����� ��� �������
	    final BufferedReader socketReader; // ���������������� �������� � �������
	    final BufferedWriter socketWriter; // ���������������� �������� �� ������
	    final BufferedReader userInput; // ���������������� �������� ����������������� ����� � �������
	    /**
	     * ����������� ������� �������
	     * @param host - IP ����� ��� localhost ��� �������� ���
	     * @param port - ����, �� ������� ����� ������
	     * @throws java.io.IOException - ���� �� ������ ���������������, �������� ����������, �����
	     * ������������� �������� �������
	     */
	    public ChatClient(String host, int port) throws IOException {
	       
	    	s = new Socket(host, port); // ������� �����
	        // ������� �������� � �������� � ����� � �������� ���������� UTF-8
	        socketReader = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
	        socketWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
	        // ������� �������� � ������� (�� ������������)
	        userInput = new BufferedReader(new InputStreamReader(System.in));
	        new Thread(new Receiver()).start();// ������� � ��������� ���� ������������ ������ �� ������
	    }
	 
	    /**
	     * �����, ��� ���������� ������� ���� ������ ��������� � ������� � �������� �� ������
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
	                userString = userInput.readLine(); // ������ ������ �� ������������
	            } catch (IOException ignored) {} // � ������� ��������� �� ����� ���� � ��������, ����������
	            //���� ���-�� �� ��� ��� ������������ ������ ����� Enter...
	           
	            
	            if (userString == null || userString.length() == 0 || s.isClosed()) {
	                close(); // ...��������� �������.
	                break; // �� ����� break �� �� ������, �� ����� ��, ����� ���������� �� �������
	            } else { //...�����...
	                try {
	                    shifr();
	                	socketWriter.write(mch); //����� ������ ������������
	                    socketWriter.write("\n"); //��������� "����� ������", ���� readLine() ������� ��������
	                    socketWriter.flush(); // ����������
	                mch = "";
	                } catch (IOException e) {
	                    close(); // � ����� ������ - ���������.
	                }
	            }
	        }
	    }
	 
	  
	    public void shifr(){
	    	
	    	
	    	int kascii[] = new int[key.length()];	
			
			for(int a = 0;a<key.length();a++){
				
				kascii[a] = (int)key.charAt(a); 
			}//������� ����� � ascii ���
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
	     * ����� ��������� ������� � ������� ��
	     * ��������� (��� ������������  ����� �������� ������ BufferedReader.readLine(), �� �������� ������������)
	     */
	    public synchronized void close() {//����� ���������������, ����� ��������� ������� ��������.
	        if (!s.isClosed()) { // ���������, ��� ����� �� ������...
	            try {
	                s.close(); // ���������...
	                System.exit(0); // �������!
	            } catch (IOException ignored) {
	                ignored.printStackTrace();
	            }
	        }
	    }
	 
	    public static void main(String[] args)  { // ������� ����� ���������
	    	
	    	JFrame jf1 = new JFrame();
	       JPanel jp1 = new JPanel();
	        jf1.add(jp1);
	        jf1.setBounds(200,0,900,600);
	       jf1.setVisible(true);
	    //	Stage s = new Stage();
	    	
	        
	        
	        
	    	try {
	            new ChatClient("localhost", 45000).run(); // ������� ��������������...
	        } catch (IOException e) { // ���� ������ �� ������...
	            System.out.println("Unable to connect. Server not running?"); // ��������...
	        }
	        
			//System.out.println(userString);
	    }
	 
	    /**
	     * ��������� ��������� ����� ������������ ������
	     */
	    private class Receiver implements Runnable{
	        /**
	         * run() ��������� ����� ������� ���� �� ������������ ������� ����.
	         */
	        public void run() {
	            while (!s.isClosed()) { //����� ��������� �������.
	                String line = null;
	                try {
	                    line = socketReader.readLine(); // ������� ��������
	                } catch (IOException e) { // ���� � ������ ������ ������, ��...
	                    // ��������, ��� ��� �� ��������� ������� �������� ������ ��������
	                    if ("Socket closed".equals(e.getMessage())) {
	                        break;
	                    }
	                    System.out.println("Connection lost"); // � ���� �� ������� � ������ ������ ����.
	                    close(); // �� � ��������� ����� (������, ��������� ����� ������ ChatClient, ���� ������)
	                }
	                if (line == null) {  // ������ ����� null ���� ������ ������� ������� �� ����� ����������, ���� ��������
	                    System.out.println("Server has closed connection");
	                    close(); // ...�����������
	                } else { // ����� �������� ��, ��� ������� ������.
	                    System.out.println("Server:" + line );
	               // System.out.println("");
	                }
	            }
	        }
	    }

		
		

		
	}
	 

