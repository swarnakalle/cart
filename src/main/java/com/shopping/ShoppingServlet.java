package com.shopping;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/")
public class ShoppingServlet extends HttpServlet {

    String url = "jdbc:mysql://localhost:3306/shopping_db";
    String dbUser = "root";
    String dbPass = "root";

    Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, dbUser, dbPass);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String page = request.getParameter("page");

        if ("register".equals(page)) {
            showRegister(response);
        } else if ("shopping".equals(page)) {
            showShopping(request, response);
        } else {
            showLogin(response, "");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        if ("login".equals(action)) {
            login(request, response);
        } else if ("register".equals(action)) {
            register(request, response);
        }
    }

    void showLogin(HttpServletResponse response, String msg) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        out.println("<h2>Login Page</h2>");
        out.println("<form method='post'>");
        out.println("<input type='hidden' name='action' value='login'>");
        out.println("Username: <input name='username'><br><br>");
        out.println("Password: <input type='password' name='password'><br><br>");
        out.println("<button type='submit'>Login</button>");
        out.println("</form>");
        out.println("<br><a href='?page=register'>Create Account</a>");
        out.println("<p style='color:red'>" + msg + "</p>");
    }

    void showRegister(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        out.println("<h2>Create Account</h2>");
        out.println("<form method='post'>");
        out.println("<input type='hidden' name='action' value='register'>");
        out.println("Username: <input name='username'><br><br>");
        out.println("Password: <input type='password' name='password'><br><br>");
        out.println("<button type='submit'>Create Account</button>");
        out.println("</form>");
        out.println("<br><a href='./'>Back to Login</a>");
    }

    void showShopping(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("./");
            return;
        }

        String username = session.getAttribute("username").toString();

        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        out.println("<h2>Welcome " + username + "</h2>");
        out.println("<h3>Shopping Items</h3>");
        out.println("<p>Laptop - $800 <button>Buy</button></p>");
        out.println("<p>Mobile Phone - $500 <button>Buy</button></p>");
        out.println("<p>Headphones - $100 <button>Buy</button></p>");
        out.println("<p>Watch - $150 <button>Buy</button></p>");
        out.println("<br><a href='./'>Logout</a>");
    }

    void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            Connection con = getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM users WHERE username=? AND password=?"
            );

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                request.getSession().setAttribute("username", username);
                response.sendRedirect("?page=shopping");
            } else {
                showLogin(response, "Invalid login details");
            }

        } catch (Exception e) {
            showLogin(response, "Database error");
        }
    }

    void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            Connection con = getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users(username, password) VALUES (?, ?)"
            );

            ps.setString(1, username);
            ps.setString(2, password);

            ps.executeUpdate();

            showLogin(response, "Account created successfully. Please login.");

        } catch (Exception e) {
            showLogin(response, "Username already exists");
        }
    }
}