package cn.edu.zjut.myong.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Server {

    public static void main(String[] args) throws Exception {
        // 启动Server
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(8888);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("./web");

        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath("/");
        servletHandler.addServlet(new ServletHolder(new ServletHeatMap()), "/ds/heatmap");
        servletHandler.addServlet(new ServletHolder(new ServletDistinct()), "/ds/distinct");
        servletHandler.addServlet(new ServletHolder(new ServletFeature()), "/ds/feature");
        servletHandler.addServlet(new ServletHolder(new ServletDynamic()), "/ds/dynamic");
        servletHandler.addServlet(new ServletHolder(new ServletAlarm()), "/ds/alarm");
        servletHandler.addServlet(new ServletHolder(new ServletRolling()), "/ds/rolling");

        HandlerCollection handlerCollection = new HandlerCollection(true);
        handlerCollection.setHandlers(new Handler[]{resource_handler, servletHandler});
        server.setHandler(handlerCollection);

        server.start();
        server.join();
    }
}
