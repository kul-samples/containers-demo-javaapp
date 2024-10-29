package com.example;
 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
 
//@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
 
 
	String hostname;
        String ipAddress;
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();  // Initialize inetAddress
            hostname = inetAddress.getHostName();  // Get the hostname
            ipAddress = inetAddress.getHostAddress();  // Get the IP address
        } catch (UnknownHostException e) {
            hostname = "Unknown Host";
            ipAddress = "Unknown IP Address";
        }
 
        out.println("<html><body>");
        out.println("<h1>Hello from " + hostname + " with IP Address: " + ipAddress + "</h1>");
        out.println("</body></html>");
    }
 
}