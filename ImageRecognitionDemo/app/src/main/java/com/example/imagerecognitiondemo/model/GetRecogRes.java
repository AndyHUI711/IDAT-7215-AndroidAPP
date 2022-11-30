package com.example.imagerecognitiondemo.model;

import java.util.List;

public class GetRecogRes {
    public List<RecogResBean> getResult() {
        return result;
    }

    public void setResult(List<RecogResBean> result) {
        this.result = result;
    }

    private List<RecogResBean> result;


    public static class RecogResBean {
        private String cls_name;
        private double cnfig;
        private List<Double> boxes;

        public String getCls_name() {
            return cls_name;
        }

        public void setCls_name(String cls_name) {
            this.cls_name = cls_name;
        }

        public double getCnfig() {
            return cnfig;
        }

        public void setCnfig(double cnfig) {
            this.cnfig = cnfig;
        }

        public List<Double> getBoxes() {
            return boxes;
        }

        public void setBoxes(List<Double> boxes) {
            this.boxes = boxes;
        }
    }
}
