package chat;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ChatWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = -8429412978041606662L;

	public ChatWebSocketServlet() {
	}

	public void configure(WebSocketServletFactory factory) {
		factory.register(ChatWebSocketImpl.class);
	}
}