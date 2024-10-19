package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shticell.engine.Engine;
import dto.SheetDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;

import static utils.ServletUtils.getEngine;

@WebServlet (name = "SortSheetServlet", urlPatterns = "/sortSheet")
public class SortSheetServlet extends HttpServlet {
    private static final String RANGE_TO_SORT = "rangeToSort";
    private static final String COLUMNS_TO_SORT_BY = "columnsToSortBy";

    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String rangeToSort = req.getParameter(RANGE_TO_SORT);
        String columnToSortBy = req.getParameter(COLUMNS_TO_SORT_BY);
        Engine engine = getEngine(getServletContext());
        try {
            SheetDTO sheetDTO = engine.sortSheet(rangeToSort, columnToSortBy);
            Type sheetType = new TypeToken<SheetDTO>() {
            }.getType();
            String json = new Gson().toJson(sheetDTO, sheetType);
            PrintWriter out = resp.getWriter();
            out.write(json);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = resp.getWriter();
            out.write("Failed to sort sheet: " + e.getMessage());
            out.flush();
            out.close();
        }
    }
}
