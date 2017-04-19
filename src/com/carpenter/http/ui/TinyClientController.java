/**
 * 
 */
package com.carpenter.http.ui;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import com.carpenter.http.HttpSegment;
import com.carpenter.http.HttpSegmentSolver;
import com.carpenter.server.TinyHttp;
import com.carpenter.udp.main.RDT;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @Title: TinyClientController.java
 * @Package com.carpenter.http.controller
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月14日 下午9:48:55
 * @version V1.0
 */
public class TinyClientController implements Initializable{
	@FXML
	private TextField Search;
	@FXML
	private Button Enter;
	@FXML
	private Button Back;
	@FXML
	private Button Pre;
	@FXML
	private Button Index1;
	@FXML
	private Button Index2;
	@FXML
	private Button Index3;
	@FXML
	private WebView HTML;
	@FXML
	private Tab Web;
	@FXML
	private Tab Details;
	@FXML
	private TabPane MyTab;
	@FXML
	private TextArea request;
	@FXML
	private TextArea response;
	
	private static RDT rdtSocket; 
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Thread tinyHttp = null;
		try {
			tinyHttp = new TinyHttp(8001, InetAddress.getLocalHost(), 8000);
			tinyHttp.start();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		try {
			rdtSocket =  new RDT(InetAddress.getLocalHost(), 8000, 8001);
		} catch (Exception e) {
			e.printStackTrace();
		}
		rdtSocket.setLossRate(0.2);
		DropShadow shadow = new DropShadow();   
		Index1.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {  
		    Index1.setEffect(shadow);  
		});  
		   
		Index1.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {  
		    Index1.setEffect(null);  
		});  
		
		Index2.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {  
		    Index2.setEffect(shadow);  
		});  
		   
		Index2.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {  
		    Index2.setEffect(null);  
		}); 
		
		Index3.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {  
		    Index3.setEffect(shadow);  
		});  
		   
		Index3.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {  
		    Index3.setEffect(null);  
		}); 
		

	}
	
	
	@FXML
	public void getURL() throws UnknownHostException, Exception {
		Search.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER){
					try {
						System.out.println("get");
						String url = Search.getText();
						WebEngine webEngine = HTML.getEngine();
						webEngine.load("http://" + url);
						System.out.println("success");
						MyTab.getSelectionModel().select(Web);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

	}
	
	@FXML
	public void getIndex() throws UnknownHostException, Exception{

		HttpSegment requestSeg = new HttpSegment();
		requestSeg.setVersion(HttpSegment.Version);
		requestSeg.setHost("www.webTest.com");
		requestSeg.setUserAgent(HttpSegment.UserAgent);
		
		Index1.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				requestSeg.setRequestMethod(HttpSegment.GET);
				requestSeg.setRequestURL("//Index1.html");
				try {
					sender(requestSeg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Index2.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				requestSeg.setRequestMethod(HttpSegment.GET);
				requestSeg.setRequestURL("//Index2.html");
				try {
					sender(requestSeg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Index3.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				requestSeg.setRequestMethod(HttpSegment.POST);
				requestSeg.setRequestURL("//Index3.html");
				try {
					sender(requestSeg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void sender(HttpSegment requestSeg) throws UnknownHostException, Exception {
		byte[] tmp = HttpSegmentSolver.toByteArray(requestSeg);
		rdtSocket.send(tmp, tmp.length);
		request.setText("Method : " + requestSeg.getRequestMethod() + "\n" +
				"URL : " + requestSeg.getRequestURL() + "\n" + 
				"Version : " + requestSeg.getVersion() + "\n" +
				"User-Agent : " + requestSeg.getUserAgent() + "\n" +
				"Host : " + requestSeg.getHost() + "\n");
		byte[] data = new byte[RDT.MSS];
		int size = 0;
		while(size == 0){
			size = rdtSocket.receive(data, RDT.MSS);		
		}
		HttpSegment responseSeg = HttpSegmentSolver.byteArrayToObject(data, HttpSegment.class, size);
		WebEngine webEngine = HTML.getEngine();
		webEngine.loadContent(responseSeg.getData());
		response.setText("Version : " + responseSeg.getVersion() + "\n" + 
				"State : " + responseSeg.getState() + "\n" +
				"Describe : " + responseSeg.getDescribe() + "\n" +
				"Server : " + responseSeg.getServer() + "\n" +
				"Content-Length : " + responseSeg.getContentLength() + "\n" +
				"Content-Type : " + responseSeg.getContentType() + "\n");
		
		MyTab.getSelectionModel().select(Web);
	}


}
