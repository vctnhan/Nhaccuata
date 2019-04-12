package com.example.hanwool.saleapp.myInterface;

import com.example.hanwool.saleapp.modal.OnlineSongHtml;
import com.example.hanwool.saleapp.modal.OnlineSongUrlMp3;

import java.util.ArrayList;

public interface OnTaskCompleted{

    void onTaskCompleted(ArrayList<OnlineSongHtml> arrayList);
}