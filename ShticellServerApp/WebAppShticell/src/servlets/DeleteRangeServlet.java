package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shticell.engine.Engine;
import dto.RangeDTO;
import dto.SheetDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;

import static utils.ServletUtils.getEngine;

@WebServlet (name = "DeleteRangeServlet", urlPatterns = "/deleteRange")
public class DeleteRangeServlet extends  HttpServlet {
    private final static String RANGE = "range";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String range = request.getParameter(RANGE);
        Engine engine = getEngine(getServletContext());
        try {
            engine.removeRange(range);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.write("Failed to delete range: " + e.getMessage());
            out.flush();
            out.close();
        }
    }
}

