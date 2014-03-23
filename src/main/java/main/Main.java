package main;

import base.*;
import chat.ChatWebSocketServlet;
import chat.GameChatImpl;
import dbService.DBServiceImpl;
import frontend.FrontendImpl;
import frontend.UserDataImpl;
import frontend.WebSocketImpl;
import frontend.WebSocketServletImpl;
import gameMechanic.GameMechanicImpl;
import messageSystem.MessageSystemImpl;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import resource.ResourceFactory;
import system.SystemInfo;
import utils.TemplateHelper;


public class Main {

    public static final int GAME_SERVER_PORT = 8001;
    public static final int WEBSOCKET_SERVER_PORT = 8051;
    public static final int CHAT_SERVER_PORT = 8011;

    public static void main(String[] args) throws Exception {
        final SystemInfo sysInfo = new SystemInfo();

        final MessageSystem messageSystem = new MessageSystemImpl();
        final FrontendImpl frontend = new FrontendImpl(messageSystem);
        final GameMechanic gameMechanic = new GameMechanicImpl(messageSystem);
        final UserData userData = new UserDataImpl(messageSystem);
        final DataAccessObject dbService = new DBServiceImpl(messageSystem);
        final GameChat gameChat = new GameChatImpl(messageSystem);
        final WebSocket webSocket = new WebSocketImpl(messageSystem);

        Server server = new Server(GAME_SERVER_PORT);
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("static");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{frontend, resource_handler});
        server.setHandler(handlers);

        Server serverWS = new Server(WEBSOCKET_SERVER_PORT);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        serverWS.setHandler(context);
        context.addServlet(new ServletHolder(new WebSocketServletImpl()), "/*");
        serverWS.start();

        Server chatServer = new Server(CHAT_SERVER_PORT);
        ServletContextHandler context2 = new ServletContextHandler(ServletContextHandler.SESSIONS);
        chatServer.setHandler(context2);
        context2.addServlet(new ServletHolder(new ChatWebSocketServlet()), "/*");
        chatServer.start();

        startService(sysInfo);
        startService(dbService);
        startService(userData);
        startService(gameMechanic);
        startService(webSocket);
        startService(gameChat);

        server.start();
        TemplateHelper.init();
        ResourceFactory.instanse();
    }

    private static void startService(Runnable service) {
        new Thread(service).start();
    }
}