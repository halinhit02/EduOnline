package com.halinhit.eduonline;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;

class ShowFileTask {

    private Context mContext;
    private String nameFile;
    private String urlFile;

    ShowFileTask(Context mContext, String nameFile, String urlFile) {
        this.mContext = mContext;
        this.nameFile = nameFile;
        this.urlFile = urlFile;
    }

    void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View viewCustom = inflater.inflate(R.layout.dialog_viewfile, null);
        builder.setView(viewCustom);
        builder.setCancelable(true);
        ImageButton imgBtnOpen = viewCustom.findViewById(R.id.imgBtnOpen);
        final PDFView pdfView = viewCustom.findViewById(R.id.pdfViewer);
        pdfView.setVisibility(View.VISIBLE);
        TextView txtFileDialog = viewCustom.findViewById(R.id.txtNameFile);
        txtFileDialog.setText(nameFile);
        imgBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOpenFile = new Intent(Intent.ACTION_VIEW);
                intentOpenFile.setData(Uri.parse(urlFile));
                mContext.startActivity(intentOpenFile);
            }
        });
        FileLoader.with(mContext)
                .load(urlFile)
                .fromDirectory("PDFFiles", FileLoader.DIR_EXTERNAL_PUBLIC)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        pdfView.fromFile(response.getBody())
                                .password(null)
                                .defaultPage(0)
                                .enableDoubletap(true)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .onRender(new OnRenderListener() {
                                    @Override
                                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                                        pdfView.fitToWidth();
                                    }
                                })
                                .enableAnnotationRendering(true)
                                .invalidPageColor(Color.WHITE)
                                .load();
                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        Toast.makeText(mContext, "Lỗi! Kiểm tra kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }
}
