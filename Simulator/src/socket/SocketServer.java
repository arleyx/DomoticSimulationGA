package socket;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class SocketServer extends WebSocketServer {
	
	protected WebSocket webSocketClient;
	
	public SocketServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		
//		System.out.println("Iniciando...");
//		System.out.println("Puerto: " + port);
	}

	public boolean hasClient() {
		return webSocketClient != null;
	}
	
	public void sendData(String message) {
		if (hasClient())
			webSocketClient.send(message);
		else
			System.out.println("No existen clientes conectados");
	}
	
	@Override
	public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
//		System.out.println("Se ha cerrado la conexión");
		webSocketClient = null;
	}

	@Override
	public void onError(WebSocket arg0, Exception arg1) {
//		System.out.println("Error de conexión");
	}

	@Override
	public void onMessage(WebSocket webSocket, String message) {
//		webSocket.send("Recieved: " + message);
//		System.out.println("Message: " + message);
	}

	@Override
	public void onOpen(WebSocket webSocket, ClientHandshake arg1) {
		webSocket.send("{\"method\":\"connected\"}");
		webSocketClient = webSocket;
	}
}
