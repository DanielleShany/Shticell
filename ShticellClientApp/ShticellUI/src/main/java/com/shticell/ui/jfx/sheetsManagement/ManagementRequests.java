package com.shticell.ui.jfx.sheetsManagement;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.shticell.ui.jfx.sheetsManagement.SheetsManagementController;
import com.shticell.ui.jfx.utils.http.HttpClientUtil;
import dto.SheetDTO;
import dto.json.MapOfSheetsDeserializer;
import dto.json.SheetDTODeserializer;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shticell.ui.jfx.utils.Constants.*;

public class ManagementRequests {

    private SheetsManagementController controller;

    public ManagementRequests(SheetsManagementController controller) {
        this.controller = controller;
    }

    protected void uploadFile(File file, UploadCallback callback) throws IOException {
        RequestBody body =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("XMLFile", file.getName(), RequestBody.create(file, MediaType.parse("text/xml")))
                        .build();

        String finalUrl = HttpUrl
                .parse(BASE_URL + UPLOAD_FILE_PAGE)
                .newBuilder()
                .toString();

        // async HTTP request
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Upload Error", "An error occurred while uploading the file: " + e.getMessage());
                    callback.onUploadFailed(e.getMessage());  // Notify failure
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    response.close();
                    Platform.runLater(() -> {
                        controller.showErrorAlert("Upload Error", "An error occurred while uploading the file: " + responseBody);
                        callback.onUploadFailed(responseBody);  // Notify failure
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            response.close();

                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                                    .create();

                            SheetDTO sheet = gson.fromJson(responseBody, SheetDTO.class);
                            callback.onUploadSuccess(sheet);  // Notify success
                        } catch (Exception e) {
                            controller.showErrorAlert("Upload Error", "An error occurred while uploading the file: " + e.getMessage());
                            callback.onUploadFailed(e.getMessage());  // Notify failure
                        }
                    });
                }
            }
        }, body);
    }

    public interface UploadCallback {
        void onUploadSuccess(SheetDTO sheet);
        void onUploadFailed(String errorMessage);
    }

    public void getActiveSheets() {
        String finalUrl = HttpUrl
                .parse(BASE_URL + GET_ALL_SHEETS)
                .newBuilder()
                .toString();

        // async
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Update Sheets Error", "An error occurred while trying to update available sheets " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                response.close();

                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        controller.showErrorAlert("Update Sheets", "An error occurred while trying to update available sheets " + responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            System.out.println("get sheets response body (200ok) : " + responseBody);

                            // Use Gson with the custom deserializer to parse the response
                            Map<String, List<SheetDTO>> sheets = new GsonBuilder()
                                    .registerTypeAdapter(new TypeToken<Map<String, List<SheetDTO>>>(){}.getType(), new MapOfSheetsDeserializer())
                                    .create()
                                    .fromJson(responseBody, new TypeToken<Map<String, List<SheetDTO>>>(){}.getType());

                            // Populate the table with the parsed sheets
                            controller.populateSheetsTable(sheets);

                        } catch (Exception e) {
                            controller.showErrorAlert("Update Sheets", "An error occurred while trying to update available sheets " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

}