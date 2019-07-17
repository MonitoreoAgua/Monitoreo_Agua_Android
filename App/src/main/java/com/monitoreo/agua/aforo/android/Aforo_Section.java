package com.monitoreo.agua.aforo.android;

public class Aforo_Section {

    private double distance;
    private double depth;
    private double speed;
    private double area;
    private double discharge;
    private String comment;

    public Aforo_Section() {

    }

    public Aforo_Section(double distance, double depth, double speed, String comment) {
        this.distance = distance;
        this.depth = depth;
        this.speed = speed;
        this.area = 0;
        this.discharge = 0;
        this.comment = comment;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) { this.depth = depth; }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public void setDischarge(double discharge) {
        this.discharge = discharge;
    }

    public double getDischarge() {
        return this.distance;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
